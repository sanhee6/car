package com.example.yidong222.api;

import android.util.Log;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 数据库列名修正工具类
 * 用于修正服务器与客户端之间的列名不匹配问题
 */
public class DatabaseColumnHelper {
    private static final String TAG = "DatabaseColumnHelper";
    private static int assignmentFieldsFixed = 0;
    private static int gradeFieldsFixed = 0;

    /**
     * 修正Assignment相关JSON数据中的列名
     * 将due_date改为deadline
     * 
     * @param jsonString 原始JSON字符串
     * @return 修正后的JSON字符串
     */
    public static String fixAssignmentColumns(String jsonString) {
        try {
            if (jsonString == null || jsonString.isEmpty()) {
                return jsonString;
            }

            // 重置字段修复计数
            assignmentFieldsFixed = 0;

            // 解析JSON
            JsonElement jsonElement = JsonParser.parseString(jsonString);

            // 检查是否为JSON对象
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                // 检查是否有data字段
                if (jsonObject.has("data")) {
                    JsonElement dataElement = jsonObject.get("data");

                    // 如果data是对象数组
                    if (dataElement.isJsonArray()) {
                        for (JsonElement item : dataElement.getAsJsonArray()) {
                            if (item.isJsonObject()) {
                                JsonObject itemObject = item.getAsJsonObject();

                                // 将due_date改为deadline
                                if (itemObject.has("due_date") && !itemObject.has("deadline")) {
                                    JsonElement dueDateElement = itemObject.get("due_date");
                                    itemObject.remove("due_date");
                                    itemObject.add("deadline", dueDateElement);
                                    assignmentFieldsFixed++;
                                    Log.d(TAG, "已将due_date字段改为deadline");
                                }
                            }
                        }
                    }
                    // 如果data是单个对象
                    else if (dataElement.isJsonObject()) {
                        JsonObject dataObject = dataElement.getAsJsonObject();

                        // 将due_date改为deadline
                        if (dataObject.has("due_date") && !dataObject.has("deadline")) {
                            JsonElement dueDateElement = dataObject.get("due_date");
                            dataObject.remove("due_date");
                            dataObject.add("deadline", dueDateElement);
                            assignmentFieldsFixed++;
                            Log.d(TAG, "已将due_date字段改为deadline");
                        }
                    }
                }

                // 输出修复状态信息
                if (assignmentFieldsFixed > 0) {
                    Log.w(TAG, "警告：服务器返回的数据仍需修正，修复了" + assignmentFieldsFixed + "个due_date字段");
                } else {
                    Log.d(TAG, "数据字段正常，无需修正due_date字段");
                }

                return jsonObject.toString();
            }

            return jsonString;
        } catch (Exception e) {
            Log.e(TAG, "修正Assignment列名时出错", e);
            return jsonString;
        }
    }

    /**
     * 修正Grade相关JSON数据中的列名
     * 移除feedback字段
     * 
     * @param jsonString 原始JSON字符串
     * @return 修正后的JSON字符串
     */
    public static String fixGradeColumns(String jsonString) {
        try {
            if (jsonString == null || jsonString.isEmpty()) {
                return jsonString;
            }

            // 重置字段修复计数
            gradeFieldsFixed = 0;

            // 解析JSON
            JsonElement jsonElement = JsonParser.parseString(jsonString);

            // 检查是否为JSON对象
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                // 检查是否有data字段
                if (jsonObject.has("data")) {
                    JsonElement dataElement = jsonObject.get("data");

                    // 如果data是对象数组
                    if (dataElement.isJsonArray()) {
                        for (JsonElement item : dataElement.getAsJsonArray()) {
                            if (item.isJsonObject()) {
                                JsonObject itemObject = item.getAsJsonObject();

                                // 移除feedback字段
                                if (itemObject.has("feedback")) {
                                    itemObject.remove("feedback");
                                    gradeFieldsFixed++;
                                    Log.d(TAG, "已移除feedback字段");
                                }
                            }
                        }
                    }
                    // 如果data是单个对象
                    else if (dataElement.isJsonObject()) {
                        JsonObject dataObject = dataElement.getAsJsonObject();

                        // 移除feedback字段
                        if (dataObject.has("feedback")) {
                            dataObject.remove("feedback");
                            gradeFieldsFixed++;
                            Log.d(TAG, "已移除feedback字段");
                        }
                    }
                }

                // 输出修复状态信息
                if (gradeFieldsFixed > 0) {
                    Log.w(TAG, "警告：服务器返回的数据仍需修正，移除了" + gradeFieldsFixed + "个feedback字段");
                } else {
                    Log.d(TAG, "数据字段正常，无需移除feedback字段");
                }

                return jsonObject.toString();
            }

            return jsonString;
        } catch (Exception e) {
            Log.e(TAG, "修正Grade列名时出错", e);
            return jsonString;
        }
    }

    /**
     * 获取Assignment修正状态
     * 
     * @return 是否需要修正Assignment字段
     */
    public static boolean isAssignmentFieldsFixed() {
        return assignmentFieldsFixed > 0;
    }

    /**
     * 获取Grade修正状态
     * 
     * @return 是否需要修正Grade字段
     */
    public static boolean isGradeFieldsFixed() {
        return gradeFieldsFixed > 0;
    }

    /**
     * 重置修正计数器
     */
    public static void resetFixCounters() {
        assignmentFieldsFixed = 0;
        gradeFieldsFixed = 0;
    }
}