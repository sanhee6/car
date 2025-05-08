package com.example.yidong222.data.api;

import com.example.yidong222.data.api.request.CourseScheduleRequest;
import com.example.yidong222.data.api.response.AssignmentResponse;
import com.example.yidong222.data.api.response.CourseResponse;
import com.example.yidong222.data.api.response.CourseScheduleResponse;
import com.example.yidong222.data.api.response.ExamResponse;
import com.example.yidong222.models.ApiResponse;
import com.example.yidong222.models.ApiResponseList;
import com.example.yidong222.models.AssignmentDto;
import com.example.yidong222.models.ExamDto;
import com.example.yidong222.models.GradeDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/semesters")
    Call<ApiResponse<List<String>>> getSemesters();

    @GET("api/courses")
    Call<ApiResponse<List<CourseResponse>>> getCourses(@Query("semesterId") String semesterId);

    @GET("api/courses/{id}")
    Call<ApiResponse<CourseResponse>> getCourse(@Path("id") int courseId);

    @GET("api/courses/{courseId}/assignments")
    Call<ApiResponse<List<AssignmentResponse>>> getCourseAssignments(@Path("courseId") int courseId);

    @GET("api/courses/{courseId}/exams")
    Call<ApiResponse<List<ExamResponse>>> getCourseExams(@Path("courseId") int courseId);

    @GET("api/course_schedules")
    Call<ApiResponse<CourseScheduleResponse>> getCourseSchedules(@Query("page") int page, @Query("limit") int limit);

    @POST("api/course_schedules")
    Call<ApiResponse<CourseScheduleResponse>> createCourseSchedule(@Body CourseScheduleRequest request);

    @PUT("api/course_schedules/{id}")
    Call<ApiResponse<CourseScheduleResponse>> updateCourseSchedule(@Path("id") int id,
            @Body CourseScheduleRequest request);

    @DELETE("api/course_schedules/{id}")
    Call<ApiResponse<CourseScheduleResponse>> deleteCourseSchedule(@Path("id") int id);

    // 获取作业列表
    @GET("api/assignments")
    Call<ApiResponseList<AssignmentDto>> getAssignments(@Query("page") int page);

    // 获取考试列表
    @GET("api/exams")
    Call<ApiResponseList<ExamDto>> getExams(@Query("page") int page);

    // 获取成绩列表
    @GET("api/grades")
    Call<ApiResponseList<GradeDto>> getGrades(@Query("page") int page);
}