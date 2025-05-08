package com.example.yidong222;

import android.util.Log;

import com.example.yidong222.models.ApiResponse;
import com.example.yidong222.models.ApiResponseList;
import com.example.yidong222.models.CourseSchedule;
import com.example.yidong222.api.ApiClientHelper;

/**
 * 测试API类型是否统一的类
 */
public class ApiTypeTest {
    private static final String TAG = "ApiTypeTest";

    /**
     * 测试API响应类型
     */
    public static void testApiTypes() {
        Log.d(TAG, "测试API类型是否统一...");

        // 测试模型中的ApiResponse类型
        Class<?> apiResponseClass = ApiResponse.class;
        Log.d(TAG, "ApiResponse类型: " + apiResponseClass.getName());

        // 测试模型中的ApiResponseList类型
        Class<?> apiResponseListClass = ApiResponseList.class;
        Log.d(TAG, "ApiResponseList类型: " + apiResponseListClass.getName());

        // 测试API调用返回类型
        try {
            Class<?> returnType = ApiClientHelper.getCourseApiService().getClass()
                    .getDeclaredMethod("getCourseSchedules", int.class, int.class)
                    .getReturnType();
            Log.d(TAG, "getCourseSchedules返回类型: " + returnType.getName());
        } catch (Exception e) {
            Log.e(TAG, "获取方法返回类型失败", e);
        }

        Log.d(TAG, "API类型测试完成");
    }
}