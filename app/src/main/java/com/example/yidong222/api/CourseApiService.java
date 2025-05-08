package com.example.yidong222.api;

import com.example.yidong222.models.ApiResponse;
import com.example.yidong222.models.ApiResponseList;
import com.example.yidong222.models.CourseDto;
import com.example.yidong222.models.CourseSchedule;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CourseApiService {

        @GET("api/courses")
        Call<ApiResponseList<CourseDto>> getCourses(
                        @Query("page") int page,
                        @Query("limit") int limit);

        @GET("api/courses/{id}")
        Call<ApiResponse<CourseDto>> getCourseById(@Path("id") int id);

        @GET("api/course_schedules")
        Call<ApiResponse<List<CourseSchedule>>> getCourseSchedules(
                        @Query("page") int page,
                        @Query("limit") int limit);

        @POST("api/courses")
        Call<ApiResponse<CourseDto>> createCourse(@Body CourseDto course);

        @PUT("api/courses/{id}")
        Call<ApiResponse<CourseDto>> updateCourse(
                        @Path("id") int id,
                        @Body CourseDto course);

        @DELETE("api/courses/{id}")
        Call<ApiResponse<Void>> deleteCourse(@Path("id") int id);
}