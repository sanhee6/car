package com.example.yidong222.api;

import com.example.yidong222.models.ApiResponse;
import com.example.yidong222.models.ApiResponseList;
import com.example.yidong222.models.GradeDto;
import com.example.yidong222.models.GradeStatsDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GradeApiService {

        @GET("api/grades")
        Call<ApiResponseList<GradeDto>> getGrades(
                        @Query("page") int page);

        @GET("api/grades/{id}")
        Call<ApiResponse<GradeDto>> getGrade(@Path("id") int id);

        @GET("api/grades/course/{courseId}")
        Call<ApiResponseList<GradeDto>> getGradesByCourse(@Path("courseId") int courseId);

        @GET("api/grades/student/{studentId}")
        Call<ApiResponseList<GradeDto>> getGradesByStudent(@Path("studentId") String studentId);

        @GET("api/grades/assignment/{assignmentId}")
        Call<ApiResponseList<GradeDto>> getGradesByAssignment(@Path("assignmentId") int assignmentId);

        @GET("api/grades/search")
        Call<ApiResponseList<GradeDto>> searchGrades(@Query("query") String query);

        @GET("api/grades/stats/course/{courseId}")
        Call<ApiResponse<GradeStatsDto>> getCourseStats(@Path("courseId") int courseId);

        @POST("api/grades")
        Call<ApiResponse<GradeDto>> createGrade(@Body GradeDto gradeDto);

        @PUT("api/grades/{id}")
        Call<ApiResponse<GradeDto>> updateGrade(
                        @Path("id") int id,
                        @Body GradeDto gradeDto);

        @DELETE("api/grades/{id}")
        Call<ApiResponse<Void>> deleteGrade(@Path("id") int id);
}