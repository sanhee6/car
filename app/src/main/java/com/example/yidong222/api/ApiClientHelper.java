package com.example.yidong222.api;

import android.util.Log;

/**
 * API客户端帮助类，统一处理不同包下的API客户端调用
 */
public class ApiClientHelper {
    private static final String TAG = "ApiClientHelper";

    /**
     * 获取API基础URL
     * 
     * @return API基础URL
     */
    public static String getBaseUrl() {
        return ApiClient.getBaseUrl();
    }

    /**
     * 获取课程API服务
     * 
     * @return 课程API服务
     */
    public static CourseApiService getCourseApiService() {
        return ApiClient.getCourseApiService();
    }

    /**
     * 获取考试API服务
     * 
     * @return 考试API服务
     */
    public static ExamApiService getExamApiService() {
        return ApiClient.getExamApiService();
    }

    /**
     * 获取作业API服务
     * 
     * @return 作业API服务
     */
    public static AssignmentApiService getAssignmentApiService() {
        return ApiClient.getAssignmentApiService();
    }

    /**
     * 获取成绩API服务
     * 
     * @return 成绩API服务
     */
    public static GradeApiService getGradeApiService() {
        return ApiClient.getGradeApiService();
    }

    /**
     * 刷新API客户端
     */
    public static void refreshClient() {
        Log.d(TAG, "通过ApiClientHelper刷新API客户端");
        ApiClient.refreshClient();
    }

    /**
     * 记录响应错误信息
     * 
     * @param response API响应
     */
    public static <T> void logResponseError(retrofit2.Response<T> response) {
        ApiClient.logResponseError(response);
    }
}