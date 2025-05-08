package com.example.yidong222.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.yidong222.data.api.ApiClient;
import com.example.yidong222.data.api.ApiService;
import com.example.yidong222.data.api.response.CourseScheduleResponse;
import com.example.yidong222.data.repository.CourseScheduleRepository;
import com.example.yidong222.models.Assignment;
import com.example.yidong222.models.Exam;
import com.example.yidong222.models.CourseSchedule;
import com.example.yidong222.models.Grade;
import com.example.yidong222.api.ApiClientHelper;
import com.example.yidong222.models.ApiResponse;
import com.example.yidong222.models.ApiResponseList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 数据同步管理器，确保前端显示的课程与数据库保持一致
 */
public class DataSyncManager {
    private static final String TAG = "DataSyncManager";
    private static DataSyncManager instance;
    private final Context context;
    private final CourseScheduleRepository repository;
    private List<CourseSchedule> localCourseSchedules;
    private boolean isSyncing = false;
    private ApiService apiService;

    private DataSyncManager(Context context) {
        this.context = context.getApplicationContext();
        ApiService apiService = com.example.yidong222.data.api.ApiClient.getClient().create(ApiService.class);
        this.repository = new CourseScheduleRepository(apiService);
        this.localCourseSchedules = new ArrayList<>();
        this.apiService = apiService;

        Log.d(TAG, "DataSyncManager初始化完成，API BASE_URL: " + com.example.yidong222.data.api.ApiClient.getBaseUrl());
    }

    public static synchronized DataSyncManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataSyncManager(context);
        }
        return instance;
    }

    /**
     * 从服务器加载课程表数据
     */
    public void syncCourseSchedules(SyncCallback<List<CourseSchedule>> callback) {
        if (!isNetworkAvailable()) {
            // 如果网络不可用，显示错误提示
            Toast.makeText(context, "网络不可用，请检查网络连接", Toast.LENGTH_SHORT).show();
            callback.onError(new Exception("网络不可用"));
            return;
        }

        // 添加网络请求的逻辑
        try {
            Log.d(TAG, "正在请求课程表数据，API路径: api/course_schedules");
            Log.d(TAG, "基础URL: " + ApiClientHelper.getBaseUrl());
            Log.d(TAG, "当前使用的完整URL: " + ApiClientHelper.getBaseUrl() + "api/course_schedules");

            // 记录网络配置信息
            logNetworkConfiguration();

            // 使用ApiClient的courseApiService获取课程表数据
            ApiClientHelper.getCourseApiService().getCourseSchedules(1, 100)
                    .enqueue(new Callback<ApiResponse<List<CourseSchedule>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<CourseSchedule>>> call,
                                Response<ApiResponse<List<CourseSchedule>>> response) {
                            Log.d(TAG, "收到服务器响应，状态码: " + response.code());
                            Log.d(TAG, "请求URL: " + call.request().url());

                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<List<CourseSchedule>> apiResponse = response.body();
                                Log.d(TAG, "API响应状态: " + apiResponse.getStatus());
                                Log.d(TAG, "完整API响应: " + apiResponse.toString());

                                if (apiResponse.getStatus().equals("success")) {
                                    List<CourseSchedule> schedules = apiResponse.getData();

                                    if (schedules != null && !schedules.isEmpty()) {
                                        Log.d(TAG, "成功获取到 " + schedules.size() + " 条课程数据");

                                        // 检查第一条数据的字段
                                        CourseSchedule firstSchedule = schedules.get(0);
                                        Log.d(TAG, "第一条课程数据检查 - ID: " + firstSchedule.getId()
                                                + ", 课程名: " + firstSchedule.getCourseName()
                                                + ", 教师: " + firstSchedule.getTeacherName()
                                                + ", 教室: " + firstSchedule.getClassroom()
                                                + ", 时间: " + firstSchedule.getClassTime());

                                        localCourseSchedules.clear();
                                        localCourseSchedules.addAll(schedules);
                                        callback.onSuccess(schedules);
                                    } else {
                                        Log.w(TAG, "服务器返回了空的课程列表");
                                        callback.onError(new Exception("没有课程数据"));
                                    }
                                } else {
                                    Log.e(TAG, "API返回错误: " + apiResponse.getMessage());
                                    callback.onError(new Exception(apiResponse.getMessage()));
                                }
                            } else {
                                try {
                                    String errorBody = response.errorBody() != null
                                            ? response.errorBody().string()
                                            : "未知错误";
                                    Log.e(TAG, "API错误: " + errorBody);
                                    Log.e(TAG, "请求头信息: " + call.request().headers());

                                    // 检查是否是API路径错误
                                    if (errorBody.contains("找不到路径")
                                            && errorBody.contains("/api/course_schedules")) {
                                        Log.e(TAG, "API路径错误: 服务器找不到路径 /api/course_schedules，请确认后端API是否正确配置");
                                        Log.e(TAG,
                                                "请确认后端API路径是否使用下划线('course_schedules')而不是连字符('course-schedules')");
                                        callback.onError(new Exception("API路径错误，请联系管理员检查后端API配置"));
                                    } else if (errorBody.contains("找不到路径")
                                            && errorBody.contains("/api/course-schedules")) {
                                        Log.e(TAG, "API路径格式错误: 应使用下划线而不是连字符");
                                        callback.onError(new Exception("API路径格式错误，请联系开发人员"));
                                    } else {
                                        Log.e(TAG, "服务器响应错误，状态码: " + response.code());
                                        callback.onError(new Exception("服务器响应错误: " + errorBody));
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "解析错误响应失败", e);
                                    callback.onError(new Exception("服务器响应错误"));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<List<CourseSchedule>>> call,
                                Throwable t) {
                            Log.e(TAG, "网络请求失败", t);
                            Log.e(TAG, "请求URL: " + call.request().url());
                            Log.e(TAG, "请求方法: " + call.request().method());
                            Log.e(TAG, "请求头: " + call.request().headers());

                            // 尝试识别常见的网络错误类型
                            String errorMessage = t.getMessage();
                            if (errorMessage != null) {
                                if (errorMessage.contains("Failed to connect")) {
                                    Log.e(TAG, "连接服务器失败，请检查服务器地址是否正确");
                                    callback.onError(new Exception("无法连接到服务器，请检查网络和服务器状态"));
                                } else if (errorMessage.contains("timeout")) {
                                    Log.e(TAG, "连接超时，服务器响应时间过长");
                                    callback.onError(new Exception("服务器响应超时，请稍后再试"));
                                } else {
                                    callback.onError(t);
                                }
                            } else {
                                callback.onError(t);
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "请求异常", e);
            callback.onError(e);
        }
    }

    /**
     * 添加课程到服务器并更新本地缓存
     */
    public void addCourseSchedule(String courseName, String teacherName, String classTime, String classroom,
            final SyncCallback<CourseSchedule> callback) {
        if (!isNetworkAvailable()) {
            callback.onError(new Exception("网络不可用"));
            return;
        }

        repository.createCourseSchedule(courseName, teacherName, classTime, classroom,
                new CourseScheduleRepository.RepositoryCallback<CourseScheduleResponse>() {
                    @Override
                    public void onSuccess(CourseScheduleResponse result) {
                        if (result != null && result.getSingleData() != null) {
                            localCourseSchedules.add(result.getSingleData());
                            callback.onSuccess(result.getSingleData());
                        } else {
                            callback.onError(new Exception("创建失败"));
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        callback.onError(error);
                    }
                });
    }

    /**
     * 更新服务器的课程信息并更新本地缓存
     */
    public void updateCourseSchedule(int id, String courseName, String teacherName, String classTime, String classroom,
            final SyncCallback<CourseSchedule> callback) {
        if (!isNetworkAvailable()) {
            callback.onError(new Exception("网络不可用"));
            return;
        }

        repository.updateCourseSchedule(id, courseName, teacherName, classTime, classroom,
                new CourseScheduleRepository.RepositoryCallback<CourseScheduleResponse>() {
                    @Override
                    public void onSuccess(CourseScheduleResponse result) {
                        if (result != null && result.getSingleData() != null) {
                            // 更新本地缓存中的这条记录
                            for (int i = 0; i < localCourseSchedules.size(); i++) {
                                if (localCourseSchedules.get(i).getId() == id) {
                                    localCourseSchedules.set(i, result.getSingleData());
                                    break;
                                }
                            }
                            callback.onSuccess(result.getSingleData());
                        } else {
                            callback.onError(new Exception("更新失败"));
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        callback.onError(error);
                    }
                });
    }

    /**
     * 从服务器删除课程并更新本地缓存
     */
    public void deleteCourseSchedule(int id, SyncCallback<Void> callback) {
        if (!isNetworkAvailable()) {
            callback.onError(new Exception("网络不可用"));
            return;
        }

        apiService.deleteCourseSchedule(id).enqueue(new Callback<ApiResponse<CourseScheduleResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CourseScheduleResponse>> call,
                    Response<ApiResponse<CourseScheduleResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CourseScheduleResponse> apiResponse = response.body();
                    if (apiResponse.getStatus().equals("success")) {
                        // 从本地缓存中移除
                        for (int i = 0; i < localCourseSchedules.size(); i++) {
                            if (localCourseSchedules.get(i).getId() == id) {
                                localCourseSchedules.remove(i);
                                break;
                            }
                        }
                        callback.onSuccess(null);
                    } else {
                        callback.onError(new Exception(apiResponse.getMessage()));
                    }
                } else {
                    callback.onError(new Exception("请求失败"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CourseScheduleResponse>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    /**
     * 获取本地缓存的课程表数据
     */
    public List<CourseSchedule> getLocalCourseSchedules() {
        return new ArrayList<>(localCourseSchedules);
    }

    /**
     * 获取今天的课程
     */
    public List<CourseSchedule> getTodayCourses(String weekday) {
        List<CourseSchedule> todayCourses = new ArrayList<>();
        for (CourseSchedule course : localCourseSchedules) {
            if (course.getClassTime().contains(weekday)) {
                todayCourses.add(course);
            }
        }
        return todayCourses;
    }

    /**
     * 强制与数据库同步
     */
    public void forceSyncWithDatabase(int page, int pageSize, SyncCallback<List<CourseSchedule>> callback) {
        syncCourseSchedules(callback);
    }

    /**
     * 检查网络是否可用
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities capabilities = connectivityManager
                    .getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }

    /**
     * 记录网络配置信息
     */
    public void logNetworkConfiguration() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork != null) {
                Log.d(TAG, "Network Type: " + activeNetwork.getTypeName());
                Log.d(TAG, "Network Subtype: " + activeNetwork.getSubtypeName());
                Log.d(TAG, "Is Connected: " + activeNetwork.isConnected());
                Log.d(TAG, "Is Available: " + activeNetwork.isAvailable());
                Log.d(TAG, "Is Failover: " + activeNetwork.isFailover());
                Log.d(TAG, "Is Roaming: " + activeNetwork.isRoaming());
                Log.d(TAG, "Reason: " + activeNetwork.getReason());
                Log.d(TAG, "Extra Info: " + activeNetwork.getExtraInfo());
            } else {
                Log.d(TAG, "No active network");
            }
        } else {
            Log.d(TAG, "ConnectivityManager is null");
        }
    }

    /**
     * 同步回调接口
     */
    public interface SyncCallback<T> {
        void onSuccess(T result);

        void onError(Throwable error);
    }

    /**
     * 获取作业数据
     * 
     * @param callback 回调
     */
    public void getAssignments(DataManager.DataCallback<Assignment> callback) {
        if (!isNetworkAvailable()) {
            callback.onFailure("网络不可用");
            return;
        }

        apiService.getAssignments(1) // 添加参数1，表示页码
                .enqueue(
                        new Callback<com.example.yidong222.models.ApiResponseList<com.example.yidong222.models.AssignmentDto>>() {
                            @Override
                            public void onResponse(
                                    retrofit2.Call<com.example.yidong222.models.ApiResponseList<com.example.yidong222.models.AssignmentDto>> call,
                                    retrofit2.Response<com.example.yidong222.models.ApiResponseList<com.example.yidong222.models.AssignmentDto>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    // 解析响应
                                    com.example.yidong222.models.ApiResponseList<com.example.yidong222.models.AssignmentDto> apiResponse = response
                                            .body();
                                    if (apiResponse.isSuccess() && apiResponse.getData() != null
                                            && !apiResponse.getData().isEmpty()) {
                                        // 转换为Assignment对象列表
                                        List<Assignment> assignments = new ArrayList<>();
                                        for (com.example.yidong222.models.AssignmentDto dto : apiResponse.getData()) {
                                            assignments.add(new Assignment(
                                                    dto.getTitle(),
                                                    dto.getCourseName(),
                                                    dto.getDeadline(),
                                                    dto.getDescription(),
                                                    false));
                                        }
                                        callback.onSuccess(assignments);
                                    } else {
                                        callback.onFailure("没有数据");
                                    }
                                } else {
                                    callback.onFailure("获取作业失败");
                                }
                            }

                            @Override
                            public void onFailure(
                                    retrofit2.Call<com.example.yidong222.models.ApiResponseList<com.example.yidong222.models.AssignmentDto>> call,
                                    Throwable t) {
                                callback.onFailure(t.getMessage());
                            }
                        });
    }

    /**
     * 获取考试数据
     * 
     * @param callback 回调
     */
    public void getExams(DataManager.DataCallback<Exam> callback) {
        if (!isNetworkAvailable()) {
            callback.onFailure("网络不可用");
            return;
        }

        apiService.getExams(1) // 添加参数1，表示页码
                .enqueue(
                        new Callback<com.example.yidong222.models.ApiResponseList<com.example.yidong222.models.ExamDto>>() {
                            @Override
                            public void onResponse(
                                    retrofit2.Call<com.example.yidong222.models.ApiResponseList<com.example.yidong222.models.ExamDto>> call,
                                    retrofit2.Response<com.example.yidong222.models.ApiResponseList<com.example.yidong222.models.ExamDto>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    // 解析响应
                                    com.example.yidong222.models.ApiResponseList<com.example.yidong222.models.ExamDto> apiResponse = response
                                            .body();
                                    if (apiResponse.isSuccess() && apiResponse.getData() != null
                                            && !apiResponse.getData().isEmpty()) {
                                        // 转换为Exam对象列表
                                        List<Exam> exams = new ArrayList<>();
                                        for (com.example.yidong222.models.ExamDto dto : apiResponse.getData()) {
                                            exams.add(new Exam(
                                                    dto.getCourseName(),
                                                    dto.getTitle(),
                                                    dto.getExamDate(),
                                                    dto.getFormattedTime(),
                                                    dto.getLocation(),
                                                    dto.getDescription()));
                                        }
                                        callback.onSuccess(exams);
                                    } else {
                                        callback.onFailure("没有数据");
                                    }
                                } else {
                                    callback.onFailure("获取考试信息失败");
                                }
                            }

                            @Override
                            public void onFailure(
                                    retrofit2.Call<com.example.yidong222.models.ApiResponseList<com.example.yidong222.models.ExamDto>> call,
                                    Throwable t) {
                                callback.onFailure(t.getMessage());
                            }
                        });
    }

    /**
     * 获取成绩数据
     * 
     * @param callback 回调
     */
    public void getGrades(SyncCallback<List<Map<String, Object>>> callback) {
        if (!isNetworkAvailable()) {
            // 网络不可用时返回错误
            callback.onError(new Exception("网络不可用，请检查网络连接"));
            return;
        }

        Log.d(TAG, "正在请求成绩数据，API路径: api/grades");

        // 使用ApiClient中的GradeApiService获取成绩，而不是使用DataSyncManager的apiService
        ApiClientHelper.getGradeApiService().getGrades(1).enqueue(
                new retrofit2.Callback<com.example.yidong222.models.ApiResponseList<com.example.yidong222.models.GradeDto>>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<com.example.yidong222.models.ApiResponseList<com.example.yidong222.models.GradeDto>> call,
                            retrofit2.Response<com.example.yidong222.models.ApiResponseList<com.example.yidong222.models.GradeDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            com.example.yidong222.models.ApiResponseList<com.example.yidong222.models.GradeDto> apiResponse = response
                                    .body();
                            if (apiResponse.getStatus().equals("success")) {
                                List<com.example.yidong222.models.GradeDto> gradeDtos = apiResponse.getData();

                                // 使用GradeDto的toGrade方法正确转换
                                List<Map<String, Object>> gradesMapList = new ArrayList<>();
                                double totalGpa = 0;
                                int totalCredits = 0;

                                for (com.example.yidong222.models.GradeDto dto : gradeDtos) {
                                    // 转换DTO到业务对象
                                    Grade grade = dto.toGrade();

                                    // 计算GPA
                                    double gpaValue = 0.0;
                                    try {
                                        gpaValue = Double.parseDouble(grade.getGpa());
                                    } catch (NumberFormatException e) {
                                        // 忽略解析错误，使用默认值0.0
                                        Log.e(TAG, "GPA解析错误: " + grade.getGpa(), e);
                                    }

                                    // 创建单个成绩的Map
                                    Map<String, Object> gradeMap = new HashMap<>();
                                    gradeMap.put("id", grade.getId());
                                    gradeMap.put("courseName", grade.getCourseName());
                                    gradeMap.put("courseType", grade.getCourseType());
                                    gradeMap.put("semester", grade.getSemester());
                                    gradeMap.put("score", grade.getScore());
                                    gradeMap.put("credit", grade.getCredit());
                                    gradeMap.put("gpa", grade.getGpa());

                                    gradesMapList.add(gradeMap);

                                    totalGpa += gpaValue;
                                    totalCredits++;
                                }

                                double avgGpa = totalCredits > 0 ? totalGpa / totalCredits : 0;

                                // 创建返回的List<Map<String, Object>>
                                List<Map<String, Object>> result = new ArrayList<>();

                                // 添加汇总信息
                                Map<String, Object> summaryMap = new HashMap<>();
                                summaryMap.put("type", "summary");
                                summaryMap.put("totalGpa", totalGpa);
                                summaryMap.put("totalCredits", totalCredits);
                                summaryMap.put("avgGpa", avgGpa);
                                result.add(summaryMap);

                                // 添加所有成绩
                                result.addAll(gradesMapList);

                                callback.onSuccess(result);
                            } else {
                                // API返回错误
                                callback.onError(new Exception("获取成绩数据失败: " + apiResponse.getMessage()));
                            }
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string()
                                        : "未知错误";
                                Log.e(TAG, "API错误: " + errorBody);

                                // 检查是否是API路径错误
                                if (errorBody.contains("找不到路径") && errorBody.contains("/api/grades")) {
                                    Log.e(TAG, "API路径错误: 服务器找不到路径 /api/grades，请确认后端API是否正确配置");
                                    Log.e(TAG, "服务器实际API可能为: api/grades (不带前导斜杠)，请检查服务器配置");
                                    callback.onError(new Exception("成绩API路径错误，请联系管理员检查后端API配置"));
                                } else if (errorBody.contains("找不到路径") && errorBody.contains("/api/student-grades")) {
                                    Log.e(TAG, "API路径格式错误: 应使用 '/api/grades' 而不是 '/api/student-grades'");
                                    Log.e(TAG, "API客户端BASE_URL: " + ApiClientHelper.getBaseUrl());
                                    callback.onError(new Exception("成绩API路径格式错误，请联系开发人员"));
                                } else {
                                    Log.e(TAG, "服务器响应错误，状态码: " + response.code());
                                    Log.e(TAG, "URL: " + response.raw().request().url());
                                    callback.onError(new Exception("获取成绩失败: " + errorBody));
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "响应处理错误", e);
                                callback.onError(new Exception("响应处理错误: " + e.getMessage()));
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<com.example.yidong222.models.ApiResponseList<com.example.yidong222.models.GradeDto>> call,
                            Throwable t) {
                        Log.e(TAG, "网络请求失败", t);
                        callback.onError(t);
                    }
                });
    }

    /**
     * 格式化学期显示
     */
    private String formatSemester(String date) {
        if (date == null || date.isEmpty()) {
            return "未知学期";
        }
        return date;
    }

    /**
     * 根据分数计算等级
     * 
     * @param score 分数
     * @return 等级
     */
    private String calculateGrade(Double score) {
        if (score == null)
            return "N/A";

        if (score >= 90) {
            return "A";
        } else if (score >= 80) {
            return "B";
        } else if (score >= 70) {
            return "C";
        } else if (score >= 60) {
            return "D";
        } else {
            return "F";
        }
    }
}