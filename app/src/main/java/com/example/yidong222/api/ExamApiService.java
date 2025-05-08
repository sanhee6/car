package com.example.yidong222.api;

import com.example.yidong222.models.ApiResponse;
import com.example.yidong222.models.ApiResponseList;
import com.example.yidong222.models.ExamDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ExamApiService {

    @GET("api/exams")
    Call<ApiResponseList<ExamDto>> getExams(
            @Query("page") int page,
            @Query("limit") int limit);

    @GET("api/exams/{id}")
    Call<ApiResponse<ExamDto>> getExamById(@Path("id") int id);

    @GET("api/exams/course/{courseId}")
    Call<ApiResponseList<ExamDto>> getExamsByCourse(@Path("courseId") int courseId);

    @GET("api/exams/search")
    Call<ApiResponseList<ExamDto>> searchExams(@Query("query") String query);

    @POST("api/exams")
    Call<ApiResponse<ExamDto>> createExam(@Body ExamDto exam);

    @PUT("api/exams/{id}")
    Call<ApiResponse<ExamDto>> updateExam(
            @Path("id") int id,
            @Body ExamDto exam);

    @DELETE("api/exams/{id}")
    Call<ApiResponse<Void>> deleteExam(@Path("id") int id);
}