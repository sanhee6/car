package com.example.yidong222;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.yidong222.adapters.CourseAdapter;
import com.example.yidong222.api.ApiClient;
import com.example.yidong222.models.ApiResponse;
import com.example.yidong222.models.ApiResponseList;
import com.example.yidong222.models.Course;
import com.example.yidong222.models.CourseDto;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoursesActivity extends AppCompatActivity implements CourseAdapter.CourseItemClickListener {

    private static final String TAG = "CoursesActivity";
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private List<CourseDto> courseList = new ArrayList<>();
    private FloatingActionButton fabAdd;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        // 设置标题栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("课程管理");

        // 初始化视图
        recyclerView = findViewById(R.id.recyclerViewCourses);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        fabAdd = findViewById(R.id.fabAddCourse);
        emptyView = findViewById(R.id.emptyView);

        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CourseAdapter(courseList, this);
        recyclerView.setAdapter(adapter);

        // 设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener(this::loadCoursesFromServer);

        // 设置添加按钮
        fabAdd.setOnClickListener(v -> showAddCourseDialog());

        // 加载课程数据
        loadCoursesFromServer();
    }

    private void loadCoursesFromServer() {
        swipeRefreshLayout.setRefreshing(true);

        // 显示加载提示
        Toast.makeText(CoursesActivity.this, "正在加载课程数据...", Toast.LENGTH_SHORT).show();

        // 调用API获取课程列表
        ApiClient.getCourseApiService().getCourses(1, 20).enqueue(new Callback<ApiResponseList<CourseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseList<CourseDto>> call,
                    Response<ApiResponseList<CourseDto>> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    courseList.clear();
                    courseList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();

                    updateEmptyView();
                } else {
                    Log.e(TAG, "获取课程失败: " + response.message());
                    // 记录详细错误信息
                    ApiClient.logResponseError(response);

                    // 检查Activity是否已销毁，避免BadTokenException
                    if (isFinishing() || isDestroyed()) {
                        Log.w(TAG, "Activity已销毁，取消显示对话框");
                        return;
                    }

                    // 尝试切换到备用服务器
                    try {
                        if (response.code() == 404 || response.code() >= 500) {
                            new MaterialAlertDialogBuilder(CoursesActivity.this)
                                    .setTitle("连接错误")
                                    .setMessage("无法连接到当前服务器，是否尝试切换到备用服务器？")
                                    .setPositiveButton("尝试切换", (dialog, which) -> {
                                        if (ApiClient.tryNextServer()) {
                                            Toast.makeText(CoursesActivity.this, "已切换服务器，正在重试...", Toast.LENGTH_SHORT)
                                                    .show();
                                            // 延迟500毫秒后重试，确保Retrofit客户端已重建
                                            new android.os.Handler().postDelayed(() -> {
                                                if (!isFinishing() && !isDestroyed()) {
                                                    loadCoursesFromServer();
                                                }
                                            }, 500);
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .show();
                        } else {
                            Toast.makeText(CoursesActivity.this, "获取课程数据失败: " + response.code(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } catch (Exception e) {
                        // 处理显示对话框时可能出现的异常
                        Log.e(TAG, "显示错误对话框失败", e);
                        Toast.makeText(CoursesActivity.this, "获取课程数据失败: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<CourseDto>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "获取课程错误", t);

                // 检查Activity是否已销毁，避免BadTokenException
                if (isFinishing() || isDestroyed()) {
                    Log.w(TAG, "Activity已销毁，取消显示对话框");
                    return;
                }

                // 提供更具体的错误信息和重试选项
                try {
                    new MaterialAlertDialogBuilder(CoursesActivity.this)
                            .setTitle("网络错误")
                            .setMessage("连接服务器失败: " + t.getMessage() + "\n是否尝试切换服务器或重试？")
                            .setPositiveButton("切换服务器", (dialog, which) -> {
                                if (ApiClient.tryNextServer()) {
                                    Toast.makeText(CoursesActivity.this, "已切换服务器，正在重试...", Toast.LENGTH_SHORT).show();
                                    // 延迟500毫秒后重试，确保Retrofit客户端已重建
                                    new android.os.Handler().postDelayed(() -> {
                                        if (!isFinishing() && !isDestroyed()) {
                                            loadCoursesFromServer();
                                        }
                                    }, 500);
                                }
                            })
                            .setNeutralButton("重试", (dialog, which) -> {
                                Toast.makeText(CoursesActivity.this, "正在重试...", Toast.LENGTH_SHORT).show();
                                loadCoursesFromServer();
                            })
                            .setNegativeButton("取消", null)
                            .show();
                } catch (Exception e) {
                    // 处理显示对话框时可能出现的异常
                    Log.e(TAG, "显示错误对话框失败", e);
                    // 使用Toast作为备选方案显示错误
                    Toast.makeText(CoursesActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateEmptyView() {
        if (courseList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showAddCourseDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_course, null);
        TextInputEditText etName = dialogView.findViewById(R.id.etCourseName);
        TextInputEditText etTeacher = dialogView.findViewById(R.id.etCourseTeacher);
        TextInputEditText etClassroom = dialogView.findViewById(R.id.etCourseClassroom);
        TextInputEditText etWeekday = dialogView.findViewById(R.id.etCourseWeekday);
        TextInputEditText etStartSection = dialogView.findViewById(R.id.etCourseStartSection);
        TextInputEditText etEndSection = dialogView.findViewById(R.id.etCourseEndSection);
        TextInputEditText etStartWeek = dialogView.findViewById(R.id.etCourseStartWeek);
        TextInputEditText etEndWeek = dialogView.findViewById(R.id.etCourseEndWeek);
        TextInputEditText etSemester = dialogView.findViewById(R.id.etCourseSemester);

        new MaterialAlertDialogBuilder(this)
                .setTitle("添加课程")
                .setView(dialogView)
                .setPositiveButton("添加", (dialog, which) -> {
                    // 从输入框获取数据
                    String name = etName.getText().toString().trim();
                    String teacher = etTeacher.getText().toString().trim();
                    String classroom = etClassroom.getText().toString().trim();
                    String weekdayStr = etWeekday.getText().toString().trim();
                    String startSectionStr = etStartSection.getText().toString().trim();
                    String endSectionStr = etEndSection.getText().toString().trim();
                    String startWeekStr = etStartWeek.getText().toString().trim();
                    String endWeekStr = etEndWeek.getText().toString().trim();
                    String semester = etSemester.getText().toString().trim();

                    // 数据验证
                    if (name.isEmpty() || teacher.isEmpty() || classroom.isEmpty() || weekdayStr.isEmpty() ||
                            startSectionStr.isEmpty() || endSectionStr.isEmpty() ||
                            startWeekStr.isEmpty() || endWeekStr.isEmpty() || semester.isEmpty()) {
                        Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        int weekday = Integer.parseInt(weekdayStr);
                        int startSection = Integer.parseInt(startSectionStr);
                        int endSection = Integer.parseInt(endSectionStr);
                        int startWeek = Integer.parseInt(startWeekStr);
                        int endWeek = Integer.parseInt(endWeekStr);

                        if (weekday < 1 || weekday > 7) {
                            Toast.makeText(this, "星期应为1-7之间的整数", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        createCourse(name, teacher, classroom, weekday, startSection, endSection, startWeek, endWeek,
                                semester);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void createCourse(String name, String teacher, String classroom, int weekday,
            int startSection, int endSection, int startWeek, int endWeek, String semester) {
        CourseDto courseDto = new CourseDto(name, teacher, classroom, weekday,
                startSection, endSection, startWeek, endWeek, semester);

        swipeRefreshLayout.setRefreshing(true);

        ApiClient.getCourseApiService().createCourse(courseDto).enqueue(new Callback<ApiResponse<CourseDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CourseDto>> call, Response<ApiResponse<CourseDto>> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Toast.makeText(CoursesActivity.this, "课程创建成功", Toast.LENGTH_SHORT).show();

                    // 刷新课程列表
                    loadCoursesFromServer();
                } else {
                    Log.e(TAG, "创建课程失败: " + response.message());
                    Toast.makeText(CoursesActivity.this, "创建课程失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CourseDto>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "创建课程错误", t);
                Toast.makeText(CoursesActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditCourseDialog(CourseDto course) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_course, null);
        TextInputEditText etName = dialogView.findViewById(R.id.etCourseName);
        TextInputEditText etTeacher = dialogView.findViewById(R.id.etCourseTeacher);
        TextInputEditText etClassroom = dialogView.findViewById(R.id.etCourseClassroom);
        TextInputEditText etWeekday = dialogView.findViewById(R.id.etCourseWeekday);
        TextInputEditText etStartSection = dialogView.findViewById(R.id.etCourseStartSection);
        TextInputEditText etEndSection = dialogView.findViewById(R.id.etCourseEndSection);
        TextInputEditText etStartWeek = dialogView.findViewById(R.id.etCourseStartWeek);
        TextInputEditText etEndWeek = dialogView.findViewById(R.id.etCourseEndWeek);
        TextInputEditText etSemester = dialogView.findViewById(R.id.etCourseSemester);

        // 填充当前数据
        etName.setText(course.getName());
        etTeacher.setText(course.getTeacher());
        etClassroom.setText(course.getClassroom());
        etWeekday.setText(String.valueOf(course.getWeekday()));
        etStartSection.setText(String.valueOf(course.getStartSection()));
        etEndSection.setText(String.valueOf(course.getEndSection()));
        etStartWeek.setText(String.valueOf(course.getStartWeek()));
        etEndWeek.setText(String.valueOf(course.getEndWeek()));
        etSemester.setText(course.getSemesterId());

        new MaterialAlertDialogBuilder(this)
                .setTitle("编辑课程")
                .setView(dialogView)
                .setPositiveButton("保存", (dialog, which) -> {
                    // 从输入框获取数据
                    String name = etName.getText().toString().trim();
                    String teacher = etTeacher.getText().toString().trim();
                    String classroom = etClassroom.getText().toString().trim();
                    String weekdayStr = etWeekday.getText().toString().trim();
                    String startSectionStr = etStartSection.getText().toString().trim();
                    String endSectionStr = etEndSection.getText().toString().trim();
                    String startWeekStr = etStartWeek.getText().toString().trim();
                    String endWeekStr = etEndWeek.getText().toString().trim();
                    String semester = etSemester.getText().toString().trim();

                    // 数据验证
                    if (name.isEmpty() || teacher.isEmpty() || classroom.isEmpty() || weekdayStr.isEmpty() ||
                            startSectionStr.isEmpty() || endSectionStr.isEmpty() ||
                            startWeekStr.isEmpty() || endWeekStr.isEmpty() || semester.isEmpty()) {
                        Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        int weekday = Integer.parseInt(weekdayStr);
                        int startSection = Integer.parseInt(startSectionStr);
                        int endSection = Integer.parseInt(endSectionStr);
                        int startWeek = Integer.parseInt(startWeekStr);
                        int endWeek = Integer.parseInt(endWeekStr);

                        if (weekday < 1 || weekday > 7) {
                            Toast.makeText(this, "星期应为1-7之间的整数", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        updateCourse(course.getId(), name, teacher, classroom, weekday,
                                startSection, endSection, startWeek, endWeek, semester);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void updateCourse(int courseId, String name, String teacher, String classroom, int weekday,
            int startSection, int endSection, int startWeek, int endWeek, String semester) {
        CourseDto courseDto = new CourseDto(name, teacher, classroom, weekday,
                startSection, endSection, startWeek, endWeek, semester);

        swipeRefreshLayout.setRefreshing(true);

        ApiClient.getCourseApiService().updateCourse(courseId, courseDto)
                .enqueue(new Callback<ApiResponse<CourseDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<CourseDto>> call,
                            Response<ApiResponse<CourseDto>> response) {
                        swipeRefreshLayout.setRefreshing(false);

                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            Toast.makeText(CoursesActivity.this, "课程更新成功", Toast.LENGTH_SHORT).show();

                            // 刷新课程列表
                            loadCoursesFromServer();
                        } else {
                            Log.e(TAG, "更新课程失败: " + response.message());
                            Toast.makeText(CoursesActivity.this, "更新课程失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<CourseDto>> call, Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        Log.e(TAG, "更新课程错误", t);
                        Toast.makeText(CoursesActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDeleteConfirmDialog(CourseDto course) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("删除课程")
                .setMessage("确定要删除课程 \"" + course.getName() + "\" 吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    deleteCourse(course.getId());
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void deleteCourse(int courseId) {
        swipeRefreshLayout.setRefreshing(true);

        ApiClient.getCourseApiService().deleteCourse(courseId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful()) {
                    Toast.makeText(CoursesActivity.this, "课程删除成功", Toast.LENGTH_SHORT).show();

                    // 刷新课程列表
                    loadCoursesFromServer();
                } else {
                    Log.e(TAG, "删除课程失败: " + response.message());
                    Toast.makeText(CoursesActivity.this, "删除课程失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "删除课程错误", t);
                Toast.makeText(CoursesActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCourseClick(int position, CourseDto course) {
        // 显示课程详情
        new MaterialAlertDialogBuilder(this)
                .setTitle(course.getName())
                .setMessage(
                        "教师: " + course.getTeacher() + "\n" +
                                "教室: " + course.getClassroom() + "\n" +
                                "星期: " + getWeekdayString(course.getWeekday()) + "\n" +
                                "节次: 第" + course.getStartSection() + "-" + course.getEndSection() + "节\n" +
                                "周次: 第" + course.getStartWeek() + "-" + course.getEndWeek() + "周\n" +
                                "学期: " + course.getSemesterId())
                .setPositiveButton("确定", null)
                .show();
    }

    @Override
    public void onCourseEditClick(int position, CourseDto course) {
        showEditCourseDialog(course);
    }

    @Override
    public void onCourseDeleteClick(int position, CourseDto course) {
        showDeleteConfirmDialog(course);
    }

    // 获取星期几的文字表示
    private String getWeekdayString(int weekday) {
        switch (weekday) {
            case 1:
                return "周一";
            case 2:
                return "周二";
            case 3:
                return "周三";
            case 4:
                return "周四";
            case 5:
                return "周五";
            case 6:
                return "周六";
            case 7:
                return "周日";
            default:
                return "未知";
        }
    }
}