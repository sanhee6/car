package com.example.yidong222.api;

import com.example.yidong222.models.ApiResponse;
import com.example.yidong222.models.ApiResponseList;
import com.example.yidong222.models.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApiService {

    // 登录
    @POST("/api/auth/login")
    Call<ApiResponse<User>> login(@Body Map<String, String> credentials);

    // 注册
    @POST("/api/auth/register")
    Call<ApiResponse<User>> register(@Body Map<String, String> userData);

    // 获取用户列表（管理员）
    @GET("/api/users")
    Call<ApiResponseList<User>> getUserList(
            @Header("Authorization") String token,
            @Query("page") int page,
            @Query("limit") int limit);

    // 获取单个用户
    @GET("/api/users/{id}")
    Call<ApiResponse<User>> getUser(
            @Header("Authorization") String token,
            @Path("id") int userId);

    // 创建用户（管理员）
    @POST("/api/users")
    Call<ApiResponse<User>> createUser(
            @Header("Authorization") String token,
            @Body Map<String, Object> userData);

    // 更新用户
    @PUT("/api/users/{id}")
    Call<ApiResponse<User>> updateUser(
            @Header("Authorization") String token,
            @Path("id") int userId,
            @Body Map<String, Object> userData);

    // 删除用户
    @DELETE("/api/users/{id}")
    Call<ApiResponse<Void>> deleteUser(
            @Header("Authorization") String token,
            @Path("id") int userId);
}