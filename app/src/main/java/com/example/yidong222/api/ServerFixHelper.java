package com.example.yidong222.api;

import android.util.Log;
import com.example.yidong222.models.AssignmentDto;
import com.example.yidong222.models.GradeDto;
import com.google.gson.Gson;
import retrofit2.Response;

/**
 * 服务器修复帮助类
 * 用于处理服务器端数据库列名不匹配的问题
 */
public class ServerFixHelper {
    private static final String TAG = "ServerFixHelper";

    /**
     * 检查是否是Assignment表的列名问题
     * 
     * @param errorMessage 错误信息
     * @return 是否是列名问题
     */
    public static boolean isAssignmentColumnIssue(String errorMessage) {
        return errorMessage != null && errorMessage.contains("Unknown column 'a.due_date'");
    }

    /**
     * 检查是否是Grade表的列名问题
     * 
     * @param errorMessage 错误信息
     * @return 是否是列名问题
     */
    public static boolean isGradeColumnIssue(String errorMessage) {
        return errorMessage != null && errorMessage.contains("Unknown column 'g.feedback'");
    }

    /**
     * 记录服务器列名问题的详细信息
     * 
     * @param <T>      响应类型
     * @param response API响应
     */
    public static <T> void logColumnIssues(Response<T> response) {
        try {
            if (response.errorBody() != null) {
                String errorContent = response.errorBody().string();
                if (isAssignmentColumnIssue(errorContent)) {
                    Log.e(TAG, "服务器数据库列名问题: 作业表的 'due_date' 字段不存在，" +
                            "客户端使用的是 'deadline'");
                    Log.e(TAG, "建议修复: 修改服务器SQL查询，将 a.due_date 改为 a.deadline");
                } else if (isGradeColumnIssue(errorContent)) {
                    Log.e(TAG, "服务器数据库列名问题: 成绩表的 'feedback' 字段不存在");
                    Log.e(TAG, "建议修复: 修改服务器SQL查询，移除 g.feedback 字段");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "解析服务器错误信息失败", e);
        }
    }

    /**
     * 打印AssignmentDto和GradeDto的字段信息，以帮助调试
     */
    public static void logDtoFields() {
        try {
            AssignmentDto assignmentDto = new AssignmentDto(1, "测试", "测试描述", "2023-01-01T00:00:00.000Z", 100);
            GradeDto gradeDto = new GradeDto("1", 1, null, null, 90.0, "test");

            Gson gson = new Gson();
            Log.d(TAG, "AssignmentDto字段: " + gson.toJson(assignmentDto));
            Log.d(TAG, "GradeDto字段: " + gson.toJson(gradeDto));
        } catch (Exception e) {
            Log.e(TAG, "日志记录失败", e);
        }
    }
}