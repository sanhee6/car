package com.example.yidong222.api;

import android.util.Log;

/**
 * API路径修复帮助类
 * 用于处理服务器端API路径不匹配的问题
 */
public class ApiPathFixHelper {
    private static final String TAG = "ApiPathFixHelper";

    /**
     * 判断是否是API路径不存在的错误
     * 
     * @param errorMessage 错误信息
     * @return 是否是路径问题
     */
    public static boolean isApiPathNotFound(String errorMessage) {
        return errorMessage != null && errorMessage.contains("找不到路径");
    }

    /**
     * 获取正确的课程表API路径
     * 
     * @return 正确的API路径
     */
    public static String getCorrectCourseScheduleApiPath() {
        // 修改为正确的API路径
        return "api/course_schedules";
    }

    /**
     * 获取正确的成绩API路径
     * 
     * @return 正确的API路径
     */
    public static String getCorrectGradesApiPath() {
        // 修改为正确的API路径
        return "api/grades";
    }

    /**
     * 记录API路径问题
     * 
     * @param errorMessage 错误信息
     */
    public static void logApiPathIssue(String errorMessage) {
        if (isApiPathNotFound(errorMessage)) {
            Log.e(TAG, "API路径错误: " + errorMessage);

            if (errorMessage.contains("/api/course-schedules")) {
                Log.e(TAG, "建议修复: 使用 " + getCorrectCourseScheduleApiPath() + " 替代 /api/course-schedules");
            } else if (errorMessage.contains("/api/course_schedules")) {
                Log.e(TAG, "API路径'/api/course_schedules'已正确设置，但服务器不存在该路径");
            } else if (errorMessage.contains("/api/student-grades")) {
                Log.e(TAG, "建议修复: 使用 " + getCorrectGradesApiPath() + " 替代 /api/student-grades");
            }
        }
    }
}