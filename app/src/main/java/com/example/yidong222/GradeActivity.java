package com.example.yidong222;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.yidong222.adapters.GradeAdapter;
import com.example.yidong222.data.DataManager;
import com.example.yidong222.data.DataSyncManager;
import com.example.yidong222.models.Grade;
import com.example.yidong222.api.ApiClientHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GradeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerViewGrades;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvEmptyGrades;
    private TextView tvGpaInfo;
    private GradeAdapter adapter;
    private List<Map<String, Object>> gradeDataList;
    private DataSyncManager dataSyncManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);

        Log.d("GradeActivity", "正在初始化GradeActivity...");

        // 初始化控件
        toolbar = findViewById(R.id.toolbar);
        recyclerViewGrades = findViewById(R.id.recyclerViewGrades);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tvEmptyGrades = findViewById(R.id.tvEmptyGrades);
        tvGpaInfo = findViewById(R.id.tvGpaInfo);

        // 设置Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("成绩查询");

        // 初始化数据
        dataSyncManager = DataSyncManager.getInstance(this);
        gradeDataList = new ArrayList<>();
        adapter = new GradeAdapter(this, gradeDataList);

        // 设置RecyclerView
        recyclerViewGrades.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewGrades.setAdapter(adapter);

        // 设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadGradeData();
        });

        // 加载数据
        loadGradeData();
    }

    private void loadGradeData() {
        swipeRefreshLayout.setRefreshing(true);

        if (!dataSyncManager.isNetworkAvailable()) {
            Toast.makeText(this, "网络不可用，请检查网络连接", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            updateEmptyView(true);
            return;
        }

        // 显示正在加载提示
        Toast.makeText(this, "正在加载成绩数据...", Toast.LENGTH_SHORT).show();

        // 强制更新data/api/ApiClient，确保使用最新的URL
        com.example.yidong222.data.api.ApiClient.resetClient();

        // 检查并同步主ApiClient和DataSyncManager的ApiClient URL
        Log.d("GradeActivity", "主ApiClient URL: " + com.example.yidong222.api.ApiClient.getBaseUrl());
        Log.d("GradeActivity",
                "DataSyncManager ApiClient URL: " + com.example.yidong222.data.api.ApiClient.getBaseUrl());

        DataManager.getGrades(new DataManager.DataCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> data) {
                gradeDataList.clear();

                // 第一个元素是汇总信息
                if (!data.isEmpty()) {
                    Map<String, Object> summaryMap = data.get(0);
                    if (summaryMap.containsKey("type") && "summary".equals(summaryMap.get("type"))) {
                        // 显示GPA信息
                        double avgGpa = (double) summaryMap.getOrDefault("avgGpa", 0.0);
                        int totalCredits = (int) summaryMap.getOrDefault("totalCredits", 0);

                        String gpaInfo = String.format("平均绩点: %.2f  总学分: %d", avgGpa, totalCredits);
                        tvGpaInfo.setText(gpaInfo);
                        tvGpaInfo.setVisibility(View.VISIBLE);

                        // 只添加非汇总信息到列表
                        for (int i = 1; i < data.size(); i++) {
                            gradeDataList.add(data.get(i));
                        }
                    } else {
                        // 如果第一个元素不是汇总信息，就添加所有数据
                        gradeDataList.addAll(data);
                        tvGpaInfo.setVisibility(View.GONE);
                    }
                }

                adapter.notifyDataSetChanged();
                updateEmptyView(gradeDataList.isEmpty());
                swipeRefreshLayout.setRefreshing(false);

                if (!gradeDataList.isEmpty()) {
                    Toast.makeText(GradeActivity.this, "成功加载 " + gradeDataList.size() + " 条成绩记录", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(GradeActivity.this, "没有成绩记录", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String message) {
                swipeRefreshLayout.setRefreshing(false);

                // 如果Activity已销毁，则不处理错误
                if (isFinishing() || isDestroyed()) {
                    Log.w("GradeActivity", "Activity已销毁，不显示错误信息");
                    return;
                }

                // 显示更友好的错误信息
                if (message != null) {
                    if (message.contains("API路径错误") || message.contains("找不到路径")) {
                        Log.e("GradeActivity", "API路径错误: " + message);
                        Toast.makeText(GradeActivity.this, "服务器配置错误，暂时无法获取成绩信息", Toast.LENGTH_LONG).show();

                        // 尝试切换服务器并刷新API客户端
                        new androidx.appcompat.app.AlertDialog.Builder(GradeActivity.this)
                                .setTitle("服务器错误")
                                .setMessage("连接当前服务器失败，是否尝试切换服务器？")
                                .setPositiveButton("切换服务器", (dialog, which) -> {
                                    com.example.yidong222.api.ApiClient.tryNextServer();
                                    com.example.yidong222.data.api.ApiClient.resetClient();

                                    // 延迟后重试
                                    new android.os.Handler().postDelayed(() -> {
                                        if (!isFinishing() && !isDestroyed()) {
                                            loadGradeData();
                                        }
                                    }, 500);
                                })
                                .setNegativeButton("取消", null)
                                .show();
                    } else if (message.contains("网络不可用")) {
                        Toast.makeText(GradeActivity.this, "网络不可用，请检查网络连接", Toast.LENGTH_LONG).show();
                    } else if (message.contains("timeout")) {
                        Toast.makeText(GradeActivity.this, "服务器连接超时，请稍后再试", Toast.LENGTH_LONG).show();

                        // 提供重试选项
                        new androidx.appcompat.app.AlertDialog.Builder(GradeActivity.this)
                                .setTitle("连接超时")
                                .setMessage("服务器连接超时，是否尝试切换服务器？")
                                .setPositiveButton("切换服务器", (dialog, which) -> {
                                    com.example.yidong222.api.ApiClient.tryNextServer();
                                    com.example.yidong222.data.api.ApiClient.resetClient();

                                    // 延迟后重试
                                    new android.os.Handler().postDelayed(() -> {
                                        if (!isFinishing() && !isDestroyed()) {
                                            loadGradeData();
                                        }
                                    }, 500);
                                })
                                .setNeutralButton("重试", (dialog, which) -> {
                                    loadGradeData();
                                })
                                .setNegativeButton("取消", null)
                                .show();
                    } else {
                        Toast.makeText(GradeActivity.this, "加载失败: " + message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(GradeActivity.this, "加载失败: 未知错误", Toast.LENGTH_SHORT).show();
                }

                updateEmptyView(true);
            }
        });
    }

    private void updateEmptyView(boolean isEmpty) {
        if (isEmpty) {
            tvEmptyGrades.setVisibility(View.VISIBLE);
            recyclerViewGrades.setVisibility(View.GONE);
        } else {
            tvEmptyGrades.setVisibility(View.GONE);
            recyclerViewGrades.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}