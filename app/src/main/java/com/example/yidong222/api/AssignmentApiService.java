package com.example.yidong222.api;

import com.example.yidong222.models.ApiResponse;
import com.example.yidong222.models.ApiResponseList;
import com.example.yidong222.models.AssignmentDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AssignmentApiService {

        @GET("api/assignments")
        Call<ApiResponseList<AssignmentDto>> getAssignments(
                        @Query("page") int page,
                        @Query("limit") int limit);

        @GET("api/assignments/{id}")
        Call<ApiResponse<AssignmentDto>> getAssignmentById(@Path("id") int id);

        @GET("api/assignments/course/{courseId}")
        Call<ApiResponseList<AssignmentDto>> getAssignmentsByCourse(@Path("courseId") int courseId);

        @GET("api/assignments/search")
        Call<ApiResponseList<AssignmentDto>> searchAssignments(@Query("query") String query);

        @POST("api/assignments")
        Call<ApiResponse<AssignmentDto>> createAssignment(@Body AssignmentDto assignment);

        @PUT("api/assignments/{id}")
        Call<ApiResponse<AssignmentDto>> updateAssignment(
                        @Path("id") int id,
                        @Body AssignmentDto assignment);

        @DELETE("api/assignments/{id}")
        Call<ApiResponse<Void>> deleteAssignment(@Path("id") int id);
}