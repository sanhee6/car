package com.example.yidong222.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

// 使用完全限定类名引用而不是导入
// import com.example.yidong222.api.ApiClient;
import com.example.yidong222.models.ApiResponse;
import com.example.yidong222.models.ApiResponseList;
import com.example.yidong222.models.Assignment;
import com.example.yidong222.models.AssignmentDto;
import com.example.yidong222.models.CourseSchedule;
import com.example.yidong222.models.Exam;
import com.example.yidong222.models.ExamDto;
import com.example.yidong222.models.Grade;
import com.example.yidong222.models.GradeDto;
import com.example.yidong222.models.GradeStatsDto;
import com.example.yidong222.api.ServerFixHelper;
import com.example.yidong222.api.ApiClientHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 数据管理类，用于处理应用程序的数据操作
 */
public class DataManager {
    private static final String TAG = "DataManager";
    private static DataSyncManager dataSyncManager;

    /**
     * 初始化DataManager
     * 
     * @param context 应用程序上下文
     */
    public static void init(Context context) {
        if (dataSyncManager == null) {
            dataSyncManager = DataSyncManager.getInstance(context);
        }
    }

    /**
     * 检查网络是否可用
     * 
     * @param context 上下文
     * @return 网络是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        if (dataSyncManager != null) {
            return dataSyncManager.isNetworkAvailable();
        } else {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
            return false;
        }
    }

    /**
     * 获取课程表数据
     * 
     * @param callback 回调
     */
    public static void getCourseSchedules(DataCallback<CourseSchedule> callback) {
        if (dataSyncManager == null) {
            callback.onFailure("DataSyncManager未初始化");
            return;
        }

        dataSyncManager.syncCourseSchedules(new DataSyncManager.SyncCallback<List<CourseSchedule>>() {
            @Override
            public void onSuccess(List<CourseSchedule> result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(Throwable error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    /**
     * 获取作业数据
     * 
     * @param callback 回调
     */
    public static void getAssignments(DataCallback<Assignment> callback) {
        if (dataSyncManager == null) {
            callback.onFailure("DataSyncManager未初始化");
            return;
        }

        dataSyncManager.getAssignments(callback);
    }

    /**
     * 创建新作业
     * 
     * @param assignmentDto 作业DTO
     * @param callback      回调
     */
    public static void createAssignment(AssignmentDto assignmentDto, DetailCallback<Assignment> callback) {
        // 创建新作业
        ApiClientHelper.getAssignmentApiService()
                .createAssignment(assignmentDto)
                .enqueue(new Callback<ApiResponse<AssignmentDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<AssignmentDto>> call,
                            Response<ApiResponse<AssignmentDto>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            callback.onSuccess(response.body().getData().toAssignment());
                        } else {
                            // 如果API调用失败，尝试创建一个本地模拟作业
                            try {
                                Assignment assignment = new Assignment(
                                        assignmentDto.getTitle(),
                                        assignmentDto.getCourseName(),
                                        assignmentDto.getDeadline(),
                                        assignmentDto.getDescription(),
                                        true);
                                callback.onSuccess(assignment);
                            } catch (Exception e) {
                                callback.onFailure("创建作业失败: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<AssignmentDto>> call, Throwable t) {
                        // 如果API调用失败，尝试创建一个本地模拟作业
                        try {
                            Assignment assignment = new Assignment(
                                    assignmentDto.getTitle(),
                                    assignmentDto.getCourseName(),
                                    assignmentDto.getDeadline(),
                                    assignmentDto.getDescription(),
                                    true);
                            callback.onSuccess(assignment);
                        } catch (Exception e) {
                            callback.onFailure("创建作业失败: " + e.getMessage());
                        }
                    }
                });
    }

    /**
     * 更新作业
     * 
     * @param assignmentId  作业ID
     * @param assignmentDto 作业DTO
     * @param callback      回调
     */
    public static void updateAssignment(int assignmentId, AssignmentDto assignmentDto,
            DetailCallback<Assignment> callback) {
        // 更新作业
        ApiClientHelper.getAssignmentApiService()
                .updateAssignment(assignmentId, assignmentDto)
                .enqueue(new Callback<ApiResponse<AssignmentDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<AssignmentDto>> call,
                            Response<ApiResponse<AssignmentDto>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            callback.onSuccess(response.body().getData().toAssignment());
                        } else {
                            // 如果API调用失败，尝试创建一个本地模拟作业
                            try {
                                Assignment assignment = new Assignment(
                                        assignmentDto.getTitle(),
                                        assignmentDto.getCourseName(),
                                        assignmentDto.getDeadline(),
                                        assignmentDto.getDescription(),
                                        false);
                                assignment.setId(assignmentId);
                                callback.onSuccess(assignment);
                            } catch (Exception e) {
                                callback.onFailure("更新作业失败: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<AssignmentDto>> call, Throwable t) {
                        // 如果API调用失败，尝试创建一个本地模拟作业
                        try {
                            Assignment assignment = new Assignment(
                                    assignmentDto.getTitle(),
                                    assignmentDto.getCourseName(),
                                    assignmentDto.getDeadline(),
                                    assignmentDto.getDescription(),
                                    false);
                            assignment.setId(assignmentId);
                            callback.onSuccess(assignment);
                        } catch (Exception e) {
                            callback.onFailure("更新作业失败: " + e.getMessage());
                        }
                    }
                });
    }

    /**
     * 删除作业
     * 
     * @param assignmentId 作业ID
     * @param callback     回调
     */
    public static void deleteAssignment(int assignmentId, DetailCallback<Void> callback) {
        // 删除作业
        ApiClientHelper.getAssignmentApiService()
                .deleteAssignment(assignmentId)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                        if (response.isSuccessful()) {
                            callback.onSuccess(null);
                        } else {
                            callback.onFailure("删除作业失败: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        callback.onFailure("删除作业失败: " + t.getMessage());
                    }
                });
    }

    /**
     * 获取考试数据
     * 
     * @param callback 回调
     */
    public static void getExams(DataCallback<Exam> callback) {
        if (dataSyncManager == null) {
            callback.onFailure("DataSyncManager未初始化");
            return;
        }

        dataSyncManager.getExams(callback);
    }

    /**
     * 创建新考试
     * 
     * @param examDto  考试DTO
     * @param callback 回调
     */
    public static void createExam(ExamDto examDto, DetailCallback<Exam> callback) {
        // 创建新考试
        ApiClientHelper.getExamApiService()
                .createExam(examDto)
                .enqueue(new Callback<ApiResponse<ExamDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<ExamDto>> call,
                            Response<ApiResponse<ExamDto>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            callback.onSuccess(response.body().getData().toExam());
                        } else {
                            // 如果API调用失败，尝试创建一个本地模拟考试
                            try {
                                Exam exam = new Exam(
                                        examDto.getCourseName(),
                                        examDto.getTitle(),
                                        examDto.getExamDate() != null ? examDto.getExamDate().split("T")[0] : "",
                                        examDto.getFormattedTime(),
                                        examDto.getLocation(),
                                        "" // 座位号未提供
                                );
                                callback.onSuccess(exam);
                            } catch (Exception e) {
                                callback.onFailure("创建考试失败: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<ExamDto>> call, Throwable t) {
                        // 如果API调用失败，尝试创建一个本地模拟考试
                        try {
                            Exam exam = new Exam(
                                    examDto.getCourseName(),
                                    examDto.getTitle(),
                                    examDto.getExamDate() != null ? examDto.getExamDate().split("T")[0] : "",
                                    examDto.getFormattedTime(),
                                    examDto.getLocation(),
                                    "" // 座位号未提供
                            );
                            callback.onSuccess(exam);
                        } catch (Exception e) {
                            callback.onFailure("创建考试失败: " + e.getMessage());
                        }
                    }
                });
    }

    /**
     * 更新考试
     * 
     * @param examId   考试ID
     * @param examDto  考试DTO
     * @param callback 回调
     */
    public static void updateExam(int examId, ExamDto examDto, DetailCallback<Exam> callback) {
        // 更新考试
        ApiClientHelper.getExamApiService()
                .updateExam(examId, examDto)
                .enqueue(new Callback<ApiResponse<ExamDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<ExamDto>> call,
                            Response<ApiResponse<ExamDto>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            callback.onSuccess(response.body().getData().toExam());
                        } else {
                            // 如果API调用失败，尝试创建一个本地模拟考试
                            try {
                                Exam exam = new Exam(
                                        examDto.getCourseName(),
                                        examDto.getTitle(),
                                        examDto.getExamDate() != null ? examDto.getExamDate().split("T")[0] : "",
                                        examDto.getFormattedTime(),
                                        examDto.getLocation(),
                                        "" // 座位号未提供
                                );
                                exam.setId(examId);
                                callback.onSuccess(exam);
                            } catch (Exception e) {
                                callback.onFailure("更新考试失败: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<ExamDto>> call, Throwable t) {
                        // 如果API调用失败，尝试创建一个本地模拟考试
                        try {
                            Exam exam = new Exam(
                                    examDto.getCourseName(),
                                    examDto.getTitle(),
                                    examDto.getExamDate() != null ? examDto.getExamDate().split("T")[0] : "",
                                    examDto.getFormattedTime(),
                                    examDto.getLocation(),
                                    "" // 座位号未提供
                            );
                            exam.setId(examId);
                            callback.onSuccess(exam);
                        } catch (Exception e) {
                            callback.onFailure("更新考试失败: " + e.getMessage());
                        }
                    }
                });
    }

    /**
     * 删除考试
     * 
     * @param examId   考试ID
     * @param callback 回调
     */
    public static void deleteExam(int examId, DetailCallback<Void> callback) {
        // 删除考试
        ApiClientHelper.getExamApiService()
                .deleteExam(examId)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                        if (response.isSuccessful()) {
                            callback.onSuccess(null);
                        } else {
                            callback.onFailure("删除考试失败: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        callback.onFailure("删除考试失败: " + t.getMessage());
                    }
                });
    }

    /**
     * 获取成绩数据
     * 
     * @param callback 回调
     */
    public static void getGrades(DataCallback<Map<String, Object>> callback) {
        if (dataSyncManager == null) {
            callback.onFailure("DataSyncManager未初始化");
            return;
        }

        dataSyncManager.getGrades(new DataSyncManager.SyncCallback<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(Throwable error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    /**
     * 数据回调接口
     * 
     * @param <T> 数据类型
     */
    public interface DataCallback<T> {
        void onSuccess(List<T> data);

        void onFailure(String message);
    }

    /**
     * 详细数据回调接口
     * 
     * @param <T> 数据类型
     */
    public interface DetailCallback<T> {
        void onSuccess(T data);

        void onFailure(String message);
    }

    // 打印网络配置信息
    public static void logNetworkConfiguration() {
        String baseUrl = ApiClientHelper.getBaseUrl();
        Log.d(TAG, "API基础URL: " + baseUrl);
        // 记录DataSyncManager使用的API客户端信息
        Log.d(TAG, "DataSyncManager使用的API客户端URL: " + com.example.yidong222.data.api.ApiClient.getBaseUrl());
    }

    // 考试相关方法
    public static void getExamsByCourse(int courseId, final DataCallback<Exam> callback) {
        ApiClientHelper.getExamApiService().getExamsByCourse(courseId)
                .enqueue(new Callback<ApiResponseList<ExamDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponseList<ExamDto>> call,
                            Response<ApiResponseList<ExamDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponseList<ExamDto> apiResponse = response.body();
                            if (apiResponse.getData() != null) {
                                List<Exam> exams = new ArrayList<>();
                                for (ExamDto dto : apiResponse.getData()) {
                                    exams.add(dto.toExam());
                                }
                                callback.onSuccess(exams);
                            } else {
                                callback.onFailure("未找到任何考试");
                            }
                        } else {
                            callback.onFailure(
                                    "获取考试失败: " + (response.errorBody() != null ? response.errorBody().toString()
                                            : "未知错误"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponseList<ExamDto>> call, Throwable t) {
                        Log.e(TAG, "获取考试错误", t);
                        callback.onFailure("网络错误: " + t.getMessage());
                    }
                });
    }

    // 成绩相关方法
    public static void getGradesByCourse(int courseId, final DataCallback<Grade> callback) {
        ApiClientHelper.getGradeApiService().getGradesByCourse(courseId)
                .enqueue(new Callback<ApiResponseList<GradeDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponseList<GradeDto>> call,
                            Response<ApiResponseList<GradeDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponseList<GradeDto> apiResponse = response.body();
                            if (apiResponse.getData() != null) {
                                List<Grade> grades = new ArrayList<>();
                                for (GradeDto dto : apiResponse.getData()) {
                                    grades.add(dto.toGrade());
                                }
                                callback.onSuccess(grades);
                            } else {
                                callback.onFailure("未找到任何成绩");
                            }
                        } else {
                            callback.onFailure(
                                    "获取成绩失败: " + (response.errorBody() != null ? response.errorBody().toString()
                                            : "未知错误"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponseList<GradeDto>> call, Throwable t) {
                        Log.e(TAG, "获取成绩错误", t);
                        callback.onFailure("网络错误: " + t.getMessage());
                    }
                });
    }

    public static void getGradesByStudent(String studentId, final DataCallback<Grade> callback) {
        Log.d(TAG, "开始获取学生 " + studentId + " 的成绩");
        ApiClientHelper.getGradeApiService().getGradesByStudent(studentId)
                .enqueue(new Callback<ApiResponseList<GradeDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponseList<GradeDto>> call,
                            Response<ApiResponseList<GradeDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponseList<GradeDto> apiResponse = response.body();
                            if (apiResponse.getData() != null) {
                                List<Grade> grades = new ArrayList<>();
                                for (GradeDto dto : apiResponse.getData()) {
                                    grades.add(dto.toGrade());
                                }
                                callback.onSuccess(grades);
                            } else {
                                // 记录详细错误信息
                                ApiClientHelper.logResponseError(response);
                                // 使用ServerFixHelper记录列名相关问题
                                ServerFixHelper.logColumnIssues(response);

                                String errorMsg = "获取成绩失败: ";
                                if (response.errorBody() != null) {
                                    try {
                                        String errorContent = response.errorBody().string();
                                        errorMsg += errorContent;

                                        // 如果是列名问题，提供更明确的错误信息
                                        if (ServerFixHelper.isGradeColumnIssue(errorContent)) {
                                            errorMsg = "服务器数据库列名不匹配: feedback 字段不存在";
                                        }
                                    } catch (IOException e) {
                                        errorMsg += "未知错误 (" + response.code() + ")";
                                    }
                                } else {
                                    errorMsg += "未知错误 (" + response.code() + ")";
                                }
                                Log.e(TAG, errorMsg);
                                callback.onFailure(errorMsg);
                            }
                        } else {
                            // 记录详细错误信息
                            ApiClientHelper.logResponseError(response);
                            // 使用ServerFixHelper记录列名相关问题
                            ServerFixHelper.logColumnIssues(response);

                            String errorMsg = "获取成绩失败: ";
                            if (response.errorBody() != null) {
                                try {
                                    String errorContent = response.errorBody().string();
                                    errorMsg += errorContent;

                                    // 如果是列名问题，提供更明确的错误信息
                                    if (ServerFixHelper.isGradeColumnIssue(errorContent)) {
                                        errorMsg = "服务器数据库列名不匹配: feedback 字段不存在";
                                    }
                                } catch (IOException e) {
                                    errorMsg += "未知错误 (" + response.code() + ")";
                                }
                            } else {
                                errorMsg += "未知错误 (" + response.code() + ")";
                            }
                            Log.e(TAG, errorMsg);
                            callback.onFailure(errorMsg);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponseList<GradeDto>> call, Throwable t) {
                        Log.e(TAG, "获取成绩错误", t);
                        callback.onFailure("网络错误: " + t.getMessage());
                    }
                });
    }

    public static void getCourseStats(int courseId, final StatsCallback callback) {
        ApiClientHelper.getGradeApiService().getCourseStats(courseId)
                .enqueue(new Callback<ApiResponse<GradeStatsDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<GradeStatsDto>> call,
                            Response<ApiResponse<GradeStatsDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<GradeStatsDto> apiResponse = response.body();
                            if (apiResponse.getData() != null) {
                                callback.onSuccess(apiResponse.getData());
                            } else {
                                callback.onFailure("获取统计数据失败");
                            }
                        } else {
                            callback.onFailure(
                                    "获取统计数据失败: " + (response.errorBody() != null ? response.errorBody().toString()
                                            : "未知错误"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<GradeStatsDto>> call, Throwable t) {
                        Log.e(TAG, "获取统计数据错误", t);
                        callback.onFailure("网络错误: " + t.getMessage());
                    }
                });
    }

    public static void createGrade(GradeDto gradeDto, final DetailCallback<Grade> callback) {
        ApiClientHelper.getGradeApiService().createGrade(gradeDto)
                .enqueue(new Callback<ApiResponse<GradeDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<GradeDto>> call, Response<ApiResponse<GradeDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<GradeDto> apiResponse = response.body();
                            if (apiResponse.getData() != null) {
                                callback.onSuccess(apiResponse.getData().toGrade());
                            } else {
                                callback.onFailure("创建成绩失败");
                            }
                        } else {
                            callback.onFailure(
                                    "创建成绩失败: " + (response.errorBody() != null ? response.errorBody().toString()
                                            : "未知错误"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<GradeDto>> call, Throwable t) {
                        Log.e(TAG, "创建成绩错误", t);
                        callback.onFailure("网络错误: " + t.getMessage());
                    }
                });
    }

    public static void updateGrade(int gradeId, GradeDto gradeDto, final DetailCallback<Grade> callback) {
        ApiClientHelper.getGradeApiService().updateGrade(gradeId, gradeDto)
                .enqueue(new Callback<ApiResponse<GradeDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<GradeDto>> call, Response<ApiResponse<GradeDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<GradeDto> apiResponse = response.body();
                            if (apiResponse.getData() != null) {
                                callback.onSuccess(apiResponse.getData().toGrade());
                            } else {
                                callback.onFailure("更新成绩失败");
                            }
                        } else {
                            callback.onFailure(
                                    "更新成绩失败: " + (response.errorBody() != null ? response.errorBody().toString()
                                            : "未知错误"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<GradeDto>> call, Throwable t) {
                        Log.e(TAG, "更新成绩错误", t);
                        callback.onFailure("网络错误: " + t.getMessage());
                    }
                });
    }

    public static void deleteGrade(int gradeId, final DetailCallback<Void> callback) {
        ApiClientHelper.getGradeApiService().deleteGrade(gradeId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onFailure(
                            "删除成绩失败: " + (response.errorBody() != null ? response.errorBody().toString() : "未知错误"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e(TAG, "删除成绩错误", t);
                callback.onFailure("网络错误: " + t.getMessage());
            }
        });
    }

    public interface StatsCallback {
        void onSuccess(GradeStatsDto stats);

        void onFailure(String message);
    }
}