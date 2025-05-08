package com.example.yidong222;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.yidong222.adapters.CourseScheduleAdapter;
import com.example.yidong222.data.DataSyncManager;
import com.example.yidong222.data.repository.CourseScheduleRepository;
import com.example.yidong222.models.CourseSchedule;
import com.example.yidong222.api.ApiClientHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class CourseScheduleActivity extends AppCompatActivity {

    private static final int PAGE_SIZE = 20;
    private DataSyncManager dataSyncManager;
    private RecyclerView recyclerView;
    private CourseScheduleAdapter adapter;
    private List<CourseSchedule> courseList;
    private FloatingActionButton fabAddCourse;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvEmptyView;

    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_schedule);

        // 初始化视图组件
        recyclerView = findViewById(R.id.recycler_view);
        fabAddCourse = findViewById(R.id.fab_add_course);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        tvEmptyView = findViewById(R.id.tv_empty_view);

        // 初始化数据
        dataSyncManager = DataSyncManager.getInstance(this);
        courseList = new ArrayList<>();
        adapter = new CourseScheduleAdapter(this, courseList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 设置删除按钮点击监听器
        adapter.setOnDeleteClickListener((course, position) -> {
            showDeleteConfirmDialog(course);
        });

        // 设置下拉刷新监听器
        swipeRefreshLayout.setOnRefreshListener(this::loadData);

        // 设置添加按钮点击监听器
        fabAddCourse.setOnClickListener(v -> showAddDialog());

        // 设置列表项点击监听器
        adapter.setOnItemClickListener((course, position) -> {
            showEditDialog(course);
        });

        // 加载课程表数据
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次恢复页面时刷新数据，确保与服务器同步
        loadData();
    }

    /**
     * 刷新课程表数据
     */
    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);

        if (!dataSyncManager.isNetworkAvailable()) {
            Toast.makeText(this, "网络不可用，显示本地数据", Toast.LENGTH_SHORT).show();
            courseList.clear();
            courseList.addAll(dataSyncManager.getLocalCourseSchedules());
            adapter.notifyDataSetChanged();
            updateEmptyView();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        dataSyncManager.syncCourseSchedules(new DataSyncManager.SyncCallback<List<CourseSchedule>>() {
            @Override
            public void onSuccess(List<CourseSchedule> result) {
                courseList.clear();
                courseList.addAll(result);

                // 确保RecyclerView设置了适配器
                if (recyclerView.getAdapter() == null) {
                    recyclerView.setAdapter(adapter);
                }

                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                updateEmptyView();
            }

            @Override
            public void onError(Throwable error) {
                swipeRefreshLayout.setRefreshing(false);

                // 显示更友好的错误信息
                String errorMessage = error.getMessage();
                if (errorMessage != null && errorMessage.contains("API路径错误")) {
                    Log.e("CourseScheduleActivity", "API路径错误: " + errorMessage);
                    Toast.makeText(CourseScheduleActivity.this, "服务器配置错误，暂时无法获取课程表", Toast.LENGTH_LONG).show();

                    // 尝试刷新API客户端
                    ApiClientHelper.refreshClient();
                } else if (errorMessage != null && errorMessage.contains("API路径格式错误")) {
                    Log.e("CourseScheduleActivity", "API路径格式错误: " + errorMessage);
                    Toast.makeText(CourseScheduleActivity.this, "应用程序配置错误，请更新应用", Toast.LENGTH_LONG).show();
                } else if (errorMessage != null && errorMessage.contains("网络不可用")) {
                    Log.e("CourseScheduleActivity", "网络不可用: " + errorMessage);
                    Toast.makeText(CourseScheduleActivity.this, "网络不可用，请检查网络连接", Toast.LENGTH_LONG).show();
                } else {
                    Log.e("CourseScheduleActivity", "加载失败: " + errorMessage);
                    Toast.makeText(CourseScheduleActivity.this, "加载失败: " + errorMessage, Toast.LENGTH_SHORT).show();
                }

                // 加载失败时尝试显示本地数据
                courseList.clear();
                courseList.addAll(dataSyncManager.getLocalCourseSchedules());

                // 确保RecyclerView设置了适配器
                if (recyclerView.getAdapter() == null) {
                    recyclerView.setAdapter(adapter);
                }

                adapter.notifyDataSetChanged();
                updateEmptyView();
            }
        });
    }

    private void updateEmptyView() {
        // 显示或隐藏空视图
        if (courseList.isEmpty()) {
            tvEmptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示添加课程对话框
     */
    private void showAddDialog() {
        if (!dataSyncManager.isNetworkAvailable()) {
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        // 显示添加课程的对话框
        // TODO: 实现添加课程的对话框
    }

    /**
     * 显示编辑课程对话框
     * 
     * @param course 要编辑的课程
     */
    private void showEditDialog(CourseSchedule course) {
        if (!dataSyncManager.isNetworkAvailable()) {
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        // 显示编辑课程的对话框
        // TODO: 实现编辑课程的对话框
    }

    /**
     * 显示删除确认对话框
     * 
     * @param course 要删除的课程
     */
    private void showDeleteConfirmDialog(CourseSchedule course) {
        new AlertDialog.Builder(this)
                .setTitle("删除课程")
                .setMessage("确定要删除课程 " + course.getCourseName() + " 吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    if (!dataSyncManager.isNetworkAvailable()) {
                        Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    swipeRefreshLayout.setRefreshing(true);
                    dataSyncManager.deleteCourseSchedule(course.getId(), new DataSyncManager.SyncCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            courseList.clear();
                            courseList.addAll(dataSyncManager.getLocalCourseSchedules());
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(CourseScheduleActivity.this, "课程删除成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable error) {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(CourseScheduleActivity.this, "删除失败: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}