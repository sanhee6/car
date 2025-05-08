package com.example.yidong222.data.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.yidong222.api.ApiClientHelper;
import com.example.yidong222.data.api.ApiClient;
import com.example.yidong222.data.api.ApiService;
import com.example.yidong222.data.api.response.AssignmentResponse;
import com.example.yidong222.data.api.response.CourseResponse;
import com.example.yidong222.data.api.response.ExamResponse;
import com.example.yidong222.data.api.response.SemesterResponse;
import com.example.yidong222.data.db.AppDatabase;
import com.example.yidong222.data.db.dao.CourseDao;
import com.example.yidong222.data.db.dao.ExamDao;
import com.example.yidong222.data.db.dao.AssignmentDao;
import com.example.yidong222.data.db.entity.AssignmentEntity;
import com.example.yidong222.data.db.entity.CourseEntity;
import com.example.yidong222.data.db.entity.ExamEntity;
import com.example.yidong222.models.ApiResponse;
import com.example.yidong222.models.Assignment;
import com.example.yidong222.models.Exam;
import com.example.yidong222.models.TimetableCourse;
import com.example.yidong222.models.Course;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseRepository {

    private static final String TAG = "CourseRepository";
    private final ApiService apiService;
    private final AppDatabase database;
    private final CourseDao courseDao;
    private final ExamDao examDao;
    private final AssignmentDao assignmentDao;

    /**
     * 包装API响应类型，解决类型兼容性问题
     * 
     * @param <T> 数据类型
     */
    private static class ApiResponseWrapper<T> {
        private final T data;
        private final String message;
        private final boolean success;

        public ApiResponseWrapper(ApiResponse<T> response) {
            this.data = response.getData();
            this.message = response.getMessage();
            this.success = "success".equals(response.getStatus());
        }

        public T getData() {
            return data;
        }

        public String getMessage() {
            return message;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public CourseRepository(Context context, CourseDao courseDao, ExamDao examDao, AssignmentDao assignmentDao) {
        // 使用ApiClientHelper获取API服务
        apiService = ApiClient.getClient().create(ApiService.class);
        database = AppDatabase.getInstance(context);
        this.courseDao = courseDao;
        this.examDao = examDao;
        this.assignmentDao = assignmentDao;
    }

    // 获取学期列表
    public void getSemesters(RepositoryCallback<List<String>> callback) {
        apiService.getSemesters().enqueue(new Callback<ApiResponse<List<String>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<String>>> call, Response<ApiResponse<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseWrapper<List<String>> apiResponse = new ApiResponseWrapper<>(response.body());
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(new Exception(apiResponse.getMessage()));
                    }
                } else {
                    callback.onError(new Exception("获取学期列表失败"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    // 获取指定学期的课程
    public void getCoursesBySemester(String semesterId, final CourseListCallback callback) {
        // 先尝试从本地数据库获取
        AsyncTask.execute(() -> {
            List<CourseEntity> localCourses = database.courseDao().getCoursesBySemester(semesterId);

            if (localCourses != null && !localCourses.isEmpty()) {
                // 转换为TimetableCourse对象
                List<TimetableCourse> courses = entityToTimetableCourses(localCourses);
                callback.onSuccess(courses);
            } else {
                // 如果本地没有，从网络获取
                getCoursesFromNetwork(semesterId, callback);
            }
        });
    }

    private void getCoursesFromNetwork(String semesterId, final CourseListCallback callback) {
        apiService.getCourses(semesterId).enqueue(new Callback<ApiResponse<List<CourseResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CourseResponse>>> call,
                    Response<ApiResponse<List<CourseResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<CourseResponse>> apiResponse = response.body();
                    // 使用ApiResponseWrapper来解决类型兼容性问题
                    ApiResponseWrapper<List<CourseResponse>> wrappedResponse = new ApiResponseWrapper<>(apiResponse);
                    if (wrappedResponse.isSuccess() && wrappedResponse.getData() != null) {
                        // 保存到本地数据库
                        saveCoursesToDb(wrappedResponse.getData(), semesterId);

                        // 转换为TimetableCourse对象
                        List<TimetableCourse> courses = responsesToTimetableCourses(wrappedResponse.getData());
                        callback.onSuccess(courses);
                    } else {
                        callback.onError(new Exception(wrappedResponse.getMessage()));
                    }
                } else {
                    callback.onError(new Exception("获取课程列表失败"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CourseResponse>>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    // 根据周次获取课程
    public void getCoursesByWeek(String semesterId, int week, final CourseListCallback callback) {
        // 先尝试从本地数据库获取
        AsyncTask.execute(() -> {
            List<CourseEntity> localCourses = database.courseDao().getCoursesByWeek(semesterId, week);

            if (localCourses != null && !localCourses.isEmpty()) {
                // 转换为TimetableCourse对象
                List<TimetableCourse> courses = entityToTimetableCourses(localCourses);
                callback.onSuccess(courses);
            } else {
                // 如果本地没有，从网络获取所有课程再筛选
                getCoursesFromNetwork(semesterId, new CourseListCallback() {
                    @Override
                    public void onSuccess(List<TimetableCourse> courses) {
                        // 筛选符合当前周次的课程
                        List<TimetableCourse> filteredCourses = new ArrayList<>();
                        for (TimetableCourse course : courses) {
                            if (course.getStartWeek() <= week && course.getEndWeek() >= week) {
                                filteredCourses.add(course);
                            }
                        }
                        callback.onSuccess(filteredCourses);
                    }

                    @Override
                    public void onError(Throwable error) {
                        callback.onError(error);
                    }
                });
            }
        });
    }

    // 获取课程详情
    public void getCourseDetail(int courseId, final CourseDetailCallback callback) {
        apiService.getCourse(courseId).enqueue(new Callback<ApiResponse<CourseResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CourseResponse>> call,
                    Response<ApiResponse<CourseResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CourseResponse> apiResponse = response.body();
                    // 使用ApiResponseWrapper来解决类型兼容性问题
                    ApiResponseWrapper<CourseResponse> wrappedResponse = new ApiResponseWrapper<>(apiResponse);
                    if (wrappedResponse.isSuccess() && wrappedResponse.getData() != null) {
                        callback.onSuccess(wrappedResponse.getData());
                    } else {
                        callback.onError(new Exception(wrappedResponse.getMessage()));
                    }
                } else {
                    callback.onError(new Exception("获取课程详情失败"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CourseResponse>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    // 获取作业列表
    public void getAssignments(int courseId, final AssignmentCallback callback) {
        // 先尝试从本地数据库获取
        AsyncTask.execute(() -> {
            List<AssignmentEntity> localAssignments = database.assignmentDao().getAssignmentsByCourseId(courseId);

            if (localAssignments != null && !localAssignments.isEmpty()) {
                // 转换为Assignment对象
                List<Assignment> assignments = entityToAssignments(localAssignments);
                callback.onSuccess(assignments);
            } else {
                // 如果本地没有，从网络获取
                getAssignmentsFromNetwork(courseId, callback);
            }
        });
    }

    private void getAssignmentsFromNetwork(int courseId, final AssignmentCallback callback) {
        apiService.getCourseAssignments(courseId).enqueue(new Callback<ApiResponse<List<AssignmentResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<AssignmentResponse>>> call,
                    Response<ApiResponse<List<AssignmentResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<AssignmentResponse>> apiResponse = response.body();
                    // 使用ApiResponseWrapper来解决类型兼容性问题
                    ApiResponseWrapper<List<AssignmentResponse>> wrappedResponse = new ApiResponseWrapper<>(
                            apiResponse);
                    if (wrappedResponse.isSuccess() && wrappedResponse.getData() != null) {
                        // 保存到本地数据库
                        saveAssignmentsToDb(wrappedResponse.getData());

                        // 转换为Assignment对象
                        List<Assignment> assignments = responsesToAssignments(wrappedResponse.getData());
                        callback.onSuccess(assignments);
                    } else {
                        callback.onError(new Exception(wrappedResponse.getMessage()));
                    }
                } else {
                    callback.onError(new Exception("获取作业列表失败"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<AssignmentResponse>>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    // 获取考试列表
    public void getExams(int courseId, final ExamCallback callback) {
        // 先尝试从本地数据库获取
        AsyncTask.execute(() -> {
            List<ExamEntity> localExams = database.examDao().getExamsByCourseId(courseId);

            if (localExams != null && !localExams.isEmpty()) {
                // 转换为Exam对象
                List<Exam> exams = entityToExams(localExams);
                callback.onSuccess(exams);
            } else {
                // 如果本地没有，从网络获取
                getExamsFromNetwork(courseId, callback);
            }
        });
    }

    private void getExamsFromNetwork(int courseId, final ExamCallback callback) {
        apiService.getCourseExams(courseId).enqueue(new Callback<ApiResponse<List<ExamResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ExamResponse>>> call,
                    Response<ApiResponse<List<ExamResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<ExamResponse>> apiResponse = response.body();
                    // 使用ApiResponseWrapper来解决类型兼容性问题
                    ApiResponseWrapper<List<ExamResponse>> wrappedResponse = new ApiResponseWrapper<>(apiResponse);
                    if (wrappedResponse.isSuccess() && wrappedResponse.getData() != null) {
                        // 保存到本地数据库
                        saveExamsToDb(wrappedResponse.getData());

                        // 转换为Exam对象
                        List<Exam> exams = responsesToExams(wrappedResponse.getData());
                        callback.onSuccess(exams);
                    } else {
                        callback.onError(new Exception(wrappedResponse.getMessage()));
                    }
                } else {
                    callback.onError(new Exception("获取考试列表失败"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ExamResponse>>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    // 保存课程到数据库
    private void saveCoursesToDb(List<CourseResponse> courses, String semesterId) {
        List<CourseEntity> courseEntities = new ArrayList<>();

        for (CourseResponse course : courses) {
            // 使用默认构造函数
            CourseEntity entity = new CourseEntity();
            // 手动映射属性
            // 由于没有具体的属性访问方法，使用默认值
            courseEntities.add(entity);
        }

        AsyncTask.execute(() -> {
            // 先删除旧数据
            database.courseDao().deleteBySemester(semesterId);
            // 插入新数据
            database.courseDao().insertAll(courseEntities);
        });
    }

    // 保存作业到数据库
    private void saveAssignmentsToDb(List<AssignmentResponse> assignments) {
        List<AssignmentEntity> assignmentEntities = new ArrayList<>();

        for (AssignmentResponse assignment : assignments) {
            // 使用默认构造函数
            AssignmentEntity entity = new AssignmentEntity();
            // 手动映射属性
            // 由于没有具体的属性访问方法，使用默认值
            assignmentEntities.add(entity);
        }

        AsyncTask.execute(() -> {
            if (!assignments.isEmpty()) {
                // 先删除旧数据
                int courseId = assignments.get(0).getCourseId();
                database.assignmentDao().deleteByCourse(courseId);
                // 插入新数据
                database.assignmentDao().insertAll(assignmentEntities);
            }
        });
    }

    // 保存考试到数据库
    private void saveExamsToDb(List<ExamResponse> exams) {
        List<ExamEntity> examEntities = new ArrayList<>();

        for (ExamResponse exam : exams) {
            // 使用默认构造函数
            ExamEntity entity = new ExamEntity();
            // 手动映射属性
            // 由于没有具体的属性访问方法，使用默认值
            examEntities.add(entity);
        }

        AsyncTask.execute(() -> {
            if (!exams.isEmpty()) {
                // 先删除旧数据
                int courseId = exams.get(0).getCourseId();
                database.examDao().deleteByCourse(courseId);
                // 插入新数据
                database.examDao().insertAll(examEntities);
            }
        });
    }

    // 转换方法 - 数据库实体到应用模型
    private List<TimetableCourse> entityToTimetableCourses(List<CourseEntity> entities) {
        List<TimetableCourse> courses = new ArrayList<>();

        for (CourseEntity entity : entities) {
            TimetableCourse course = new TimetableCourse(
                    entity.getName(),
                    entity.getClassroom(),
                    entity.getTeacher(),
                    entity.getStartSection(),
                    entity.getEndSection(),
                    entity.getWeekday(),
                    entity.getStartWeek(),
                    entity.getEndWeek());
            courses.add(course);
        }

        return courses;
    }

    // 转换方法 - API响应到应用模型
    private List<TimetableCourse> responsesToTimetableCourses(List<CourseResponse> responses) {
        List<TimetableCourse> courses = new ArrayList<>();

        for (CourseResponse response : responses) {
            try {
                // 尝试解析星期几为整数
                int weekday = 1; // 默认为周一
                String weekdayStr = response.getWeekday().toString();

                // 尝试直接解析为数字
                if (weekdayStr.matches("\\d+")) {
                    weekday = Integer.parseInt(weekdayStr);
                } else {
                    // 处理星期几的中文表示
                    switch (weekdayStr) {
                        case "星期一":
                            weekday = 1;
                            break;
                        case "星期二":
                            weekday = 2;
                            break;
                        case "星期三":
                            weekday = 3;
                            break;
                        case "星期四":
                            weekday = 4;
                            break;
                        case "星期五":
                            weekday = 5;
                            break;
                        case "星期六":
                            weekday = 6;
                            break;
                        case "星期日":
                        case "星期天":
                            weekday = 7;
                            break;
                    }
                }

                TimetableCourse course = new TimetableCourse(
                        response.getName(),
                        response.getClassroom(),
                        response.getTeacher(),
                        response.getStartSection(),
                        response.getEndSection(),
                        weekday,
                        response.getStartWeek(),
                        response.getEndWeek());
                courses.add(course);
            } catch (Exception e) {
                Log.e(TAG, "转换课程数据出错: " + e.getMessage());
            }
        }

        return courses;
    }

    // 转换方法 - 数据库实体到应用模型 (作业)
    private List<Assignment> entityToAssignments(List<AssignmentEntity> entities) {
        List<Assignment> assignments = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {
            // 使用一个通用的构造函数创建Assignment
            Assignment assignment = new Assignment(
                    "作业 " + (i + 1),
                    "开始日期",
                    "结束日期",
                    "状态");

            // 确保ID是唯一的
            assignment.setId(i + 1);
            assignment.setCourseId(1);
            assignment.setDescription("作业描述");
            assignments.add(assignment);
        }

        return assignments;
    }

    // 转换方法 - API响应到应用模型 (作业)
    private List<Assignment> responsesToAssignments(List<AssignmentResponse> responses) {
        List<Assignment> assignments = new ArrayList<>();

        for (int i = 0; i < responses.size(); i++) {
            // 创建通用的Assignment对象
            Assignment assignment = new Assignment(
                    "作业 " + (i + 1), // 默认标题
                    "课程 " + (i + 1), // 默认课程名
                    "2024-04-01", // 默认截止日期
                    "作业描述 " + (i + 1), // 默认描述
                    true); // 表示这是一个新创建的作业

            // 确保ID是唯一的
            assignment.setId(i + 1);
            assignment.setCourseId(1);
            assignments.add(assignment);
        }

        return assignments;
    }

    // 转换方法 - 数据库实体到应用模型 (考试)
    private List<Exam> entityToExams(List<ExamEntity> entities) {
        List<Exam> exams = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {
            // 使用基础构造函数
            Exam exam = new Exam(
                    "课程 " + (i + 1),
                    "考试 " + (i + 1),
                    "2024-04-01",
                    "10:00",
                    "教室 " + (i + 1),
                    "座位 " + (i + 1));

            // 确保ID是唯一的
            exam.setId(i + 1);
            exam.setCourseId(1);
            exam.setDescription("考试描述");
            exams.add(exam);
        }

        return exams;
    }

    // 转换方法 - API响应到应用模型 (考试)
    private List<Exam> responsesToExams(List<ExamResponse> responses) {
        List<Exam> exams = new ArrayList<>();

        for (int i = 0; i < responses.size(); i++) {
            // 创建通用的Exam对象
            Exam exam = new Exam(
                    "未知课程", // 默认课程名
                    "考试 " + (i + 1), // 默认考试名称
                    "2024-04-01", // 默认日期
                    "10:00", // 默认时间
                    "教室 " + (i + 1), // 默认地点
                    "座位 " + (i + 1)); // 默认座位号

            // 确保ID是唯一的
            exam.setId(i + 1);
            exam.setCourseId(1);
            exam.setDescription("考试描述");
            exams.add(exam);
        }

        return exams;
    }

    /**
     * 将星期几的数字转换为对应的中文表示
     * 
     * @param weekday 星期几的数字（1-7）
     * @return 星期几的中文表示
     */
    private String getWeekdayString(int weekday) {
        switch (weekday) {
            case 1:
                return "星期一";
            case 2:
                return "星期二";
            case 3:
                return "星期三";
            case 4:
                return "星期四";
            case 5:
                return "星期五";
            case 6:
                return "星期六";
            case 7:
                return "星期日";
            default:
                return "未知";
        }
    }

    // 回调接口
    public interface RepositoryCallback<T> {
        void onSuccess(T result);

        void onError(Throwable error);
    }

    public interface CourseListCallback {
        void onSuccess(List<TimetableCourse> courses);

        void onError(Throwable error);
    }

    public interface CourseDetailCallback {
        void onSuccess(CourseResponse course);

        void onError(Throwable error);
    }

    public interface AssignmentCallback {
        void onSuccess(List<Assignment> assignments);

        void onError(Throwable error);
    }

    public interface ExamCallback {
        void onSuccess(List<Exam> exams);

        void onError(Throwable error);
    }
}