package com.example.yidong222;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.yidong222.api.ApiClient;
import com.example.yidong222.api.ServerFixHelper;
import com.example.yidong222.api.DatabaseColumnHelper;
import com.example.yidong222.data.DataManager;
import com.example.yidong222.models.Assignment;
import com.example.yidong222.models.Grade;
import android.content.SharedPreferences;

import java.util.List;
import java.util.Map;

/**
 * 服务器调试活动
 * 用于测试与服务器的连接和检查数据库字段一致性
 */
public class ServerDebugActivity extends AppCompatActivity {

    private static final String TAG = "ServerDebugActivity";

    private TextView tvDebugInfo;
    private Button btnTestAssignment;
    private Button btnTestGrade;
    private Button btnFixIssues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_debug);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("服务器调试");

        tvDebugInfo = findViewById(R.id.tvDebugInfo);
        btnTestAssignment = findViewById(R.id.btnTestAssignment);
        btnTestGrade = findViewById(R.id.btnTestGrade);
        btnFixIssues = findViewById(R.id.btnFixIssues);

        // 初始化界面
        setupListeners();

        // 记录API基础信息
        DataManager.logNetworkConfiguration();
        ServerFixHelper.logDtoFields();
        appendDebugInfo("API基础URL: " + ApiClient.getBaseUrl());
    }

    private void setupListeners() {
        btnTestAssignment.setOnClickListener(v -> testAssignmentApi());
        btnTestGrade.setOnClickListener(v -> testGradeApi());
        btnFixIssues.setOnClickListener(v -> fixDatabaseIssues());
    }

    private void testAssignmentApi() {
        appendDebugInfo("正在测试作业API...");
        // 重置修正计数器
        DatabaseColumnHelper.resetFixCounters();

        DataManager.getAssignments(new DataManager.DataCallback<Assignment>() {
            @Override
            public void onSuccess(List<Assignment> data) {
                runOnUiThread(() -> {
                    String info = "作业API测试成功!\n获取到 " + data.size() + " 条记录\n";

                    // 检查是否需要客户端修正
                    boolean needsClientFix = DatabaseColumnHelper.isAssignmentFieldsFixed();
                    if (needsClientFix) {
                        info += "⚠️ 服务器响应数据仍存在问题：\n";
                        info += "- 'due_date' 字段未修改为 'deadline'\n";
                        info += "- 客户端进行了自动修正\n";
                    } else {
                        info += "✓ 服务器数据已修复：\n";
                        info += "- 'deadline' 字段名称正确\n";
                    }

                    if (data.size() > 0) {
                        Assignment first = data.get(0);
                        info += "\n示例数据(第一条):\n";
                        info += "ID: " + first.getId() + "\n";
                        info += "标题: " + first.getTitle() + "\n";
                        info += "截止日期: " + first.getDeadline();
                    }

                    appendDebugInfo(info);
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    appendDebugInfo("作业API测试失败: " + errorMessage);
                });
            }
        });
    }

    private void testGradeApi() {
        appendDebugInfo("正在测试成绩API...");
        // 重置修正计数器
        DatabaseColumnHelper.resetFixCounters();

        DataManager.getGrades(new DataManager.DataCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> data) {
                runOnUiThread(() -> {
                    String info = "成绩API测试成功!\n获取到 " + data.size() + " 条记录\n";

                    // 检查是否需要客户端修正
                    boolean needsClientFix = DatabaseColumnHelper.isGradeFieldsFixed();
                    if (needsClientFix) {
                        info += "⚠️ 服务器响应数据仍存在问题：\n";
                        info += "- 包含不存在的 'feedback' 字段\n";
                        info += "- 客户端进行了自动移除\n";
                    } else {
                        info += "✓ 服务器数据已修复：\n";
                        info += "- 不再包含 'feedback' 字段\n";
                    }

                    if (data.size() > 0) {
                        Map<String, Object> first = data.get(0);
                        info += "\n示例数据(第一条):\n";
                        info += "ID: " + first.get("id") + "\n";
                        info += "课程: " + first.get("courseName") + "\n";
                        info += "分数: " + first.get("score");
                    }

                    appendDebugInfo(info);
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    appendDebugInfo("成绩API测试失败: " + errorMessage);
                });
            }
        });
    }

    private void fixDatabaseIssues() {
        appendDebugInfo("正在尝试修复数据库列名问题...");

        // 重置API客户端，确保新的拦截器生效
        ApiClient.resetClient();

        appendDebugInfo("已重置API客户端，新的拦截器已生效");
        appendDebugInfo("现在可以重新测试API功能");

        // 添加具体的数据库修正建议
        appendDebugInfo("\n数据库修正建议：");
        appendDebugInfo("1. 作业表问题：将SQL查询中的 a.due_date 改为 a.deadline");
        appendDebugInfo("2. 成绩表问题：从SQL查询中移除 g.feedback 字段");
        appendDebugInfo("\n这些问题需要后端开发人员在服务器端修复");

        // 添加清除模拟数据的方法
        appendDebugInfo("\n尝试清除所有本地模拟数据...");
        cleanAllMockData();
    }

    // 清除所有模拟数据的方法
    private void cleanAllMockData() {
        try {
            // 清除共享首选项中可能存储的模拟数据
            SharedPreferences preferences = getSharedPreferences("mock_data_prefs", MODE_PRIVATE);
            preferences.edit().clear().apply();
            appendDebugInfo("✓ 已清除共享首选项中的模拟数据");

            // 清除TimetableFragment中可能存储的课程数据
            SharedPreferences coursePrefs = getSharedPreferences("course_data", MODE_PRIVATE);
            coursePrefs.edit().clear().apply();
            appendDebugInfo("✓ 已清除课表相关的模拟数据");

            // 清除其他可能的模拟数据存储
            getSharedPreferences("exam_data", MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences("assignment_data", MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences("grade_data", MODE_PRIVATE).edit().clear().apply();
            appendDebugInfo("✓ 已清除所有本地模拟数据");
            appendDebugInfo("✓ 应用程序现在将直接从服务器加载数据");

        } catch (Exception e) {
            appendDebugInfo("× 清除模拟数据时出错: " + e.getMessage());
        }
    }

    private void appendDebugInfo(String info) {
        String currentText = tvDebugInfo.getText().toString();
        tvDebugInfo.setText(currentText + "\n\n" + info);

        // 自动滚动到底部
        final int scrollAmount = tvDebugInfo.getLayout().getLineTop(tvDebugInfo.getLineCount())
                - tvDebugInfo.getHeight();
        if (scrollAmount > 0) {
            tvDebugInfo.scrollTo(0, scrollAmount);
        } else {
            tvDebugInfo.scrollTo(0, 0);
        }
    }
}