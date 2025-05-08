package com.example.yidong222;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.yidong222.adapters.CourseManagementAdapter;
import com.example.yidong222.api.ApiClient;
import com.example.yidong222.api.CourseApiService;
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

public class CourseManagementActivity extends AppCompatActivity
        implements CourseManagementAdapter.CourseItemClickListener {

    private RecyclerView recyclerView;
    private CourseManagementAdapter adapter;
    private List<Course> courseList = new ArrayList<>();
    private FloatingActionButton fabAdd;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CourseApiService apiService;
    private List<CourseDto> courseDtoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_management);

        // 初始化API服务
        apiService = ApiClient.getClient().create(CourseApiService.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("课程管理");

        recyclerView = findViewById(R.id.recyclerViewCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化下拉刷新
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadCourses);

        // 检查网络连接
        if (!isNetworkAvailable()) {
            TextView tvEmptyCourses = findViewById(R.id.tvEmptyCourses);
            tvEmptyCourses.setVisibility(View.VISIBLE);
            tvEmptyCourses.setText("无网络连接，请检查网络后下拉刷新");
            Toast.makeText(this, "无网络连接，请检查网络设置", Toast.LENGTH_LONG).show();
        } else {
            // 加载API数据
            loadCourses();
        }

        adapter = new CourseManagementAdapter(courseList, this);
        recyclerView.setAdapter(adapter);

        fabAdd = findViewById(R.id.fabAddCourse);
        fabAdd.setOnClickListener(v -> showAddCourseDialog());
    }

    private void loadCourses() {
        swipeRefreshLayout.setRefreshing(true);
        TextView tvEmptyCourses = findViewById(R.id.tvEmptyCourses);
        tvEmptyCourses.setVisibility(View.GONE);

        // 添加调试日志
        Log.d("CourseManagement", "开始加载课程数据，BASE_URL: " + ApiClient.getBaseUrl());

        apiService.getCourses(1, 50).enqueue(new Callback<ApiResponseList<CourseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseList<CourseDto>> call,
                    Response<ApiResponseList<CourseDto>> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseList<CourseDto> apiResponse = response.body();

                    Log.d("CourseManagement", "获取课程响应: " + response.code() + ", 状态: " + apiResponse.getStatus());

                    if ("success".equals(apiResponse.getStatus()) && apiResponse.getData() != null) {
                        courseDtoList = apiResponse.getData();
                        courseList.clear();

                        // 将API返回的课程DTO转换为UI使用的Course对象
                        for (CourseDto courseDto : courseDtoList) {
                            courseList.add(courseDto.toCourse());
                        }

                        adapter.notifyDataSetChanged();

                        // 显示或隐藏空数据提示
                        if (courseList.isEmpty()) {
                            tvEmptyCourses.setVisibility(View.VISIBLE);
                            tvEmptyCourses.setText("暂无课程数据");
                        } else {
                            tvEmptyCourses.setVisibility(View.GONE);
                        }
                    } else {
                        // API返回失败
                        tvEmptyCourses.setVisibility(View.VISIBLE);
                        tvEmptyCourses.setText("获取课程失败: " +
                                (apiResponse.getMessage() != null ? apiResponse.getMessage() : "未知错误"));
                        Toast.makeText(CourseManagementActivity.this, "获取课程失败: " +
                                (apiResponse.getMessage() != null ? apiResponse.getMessage() : "未知错误"),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // HTTP请求失败
                    tvEmptyCourses.setVisibility(View.VISIBLE);
                    String errorMsg = "获取课程失败: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("CourseManagement", "读取错误响应失败", e);
                    }
                    tvEmptyCourses.setText(errorMsg);
                    Log.e("CourseManagement", errorMsg);
                    Toast.makeText(CourseManagementActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<CourseDto>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                tvEmptyCourses.setVisibility(View.VISIBLE);
                String errorMsg = "网络请求失败: " + t.getMessage();
                tvEmptyCourses.setText(errorMsg);
                Log.e("CourseManagement", "网络请求失败", t);
                Toast.makeText(CourseManagementActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddCourseDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_course, null);
        TextInputEditText etCourseName = dialogView.findViewById(R.id.etCourseName);
        TextInputEditText etTeacher = dialogView.findViewById(R.id.etTeacherName);
        TextInputEditText etClassroom = dialogView.findViewById(R.id.etClassroom);
        Spinner spWeekday = dialogView.findViewById(R.id.spWeekday);
        Spinner spStartSection = dialogView.findViewById(R.id.spStartSection);
        Spinner spEndSection = dialogView.findViewById(R.id.spEndSection);

        new MaterialAlertDialogBuilder(this)
                .setTitle("添加课程")
                .setView(dialogView)
                .setPositiveButton("添加", (dialog, which) -> {
                    String name = etCourseName.getText().toString().trim();
                    String teacher = etTeacher.getText().toString().trim();
                    String classroom = etClassroom.getText().toString().trim();
                    int weekday = spWeekday.getSelectedItemPosition() + 1;
                    int startSection = spStartSection.getSelectedItemPosition() + 1;
                    int endSection = spEndSection.getSelectedItemPosition() + 1;
                    int startWeek = 1;
                    int endWeek = 16;
                    String semesterId = "2023-1";

                    if (!name.isEmpty() && !teacher.isEmpty()) {
                        CourseDto newCourse = new CourseDto(
                                name, teacher, classroom, weekday,
                                startSection, endSection, startWeek,
                                endWeek, semesterId);

                        createCourse(newCourse);
                    } else {
                        Toast.makeText(this, "请填写必填的课程信息", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void createCourse(CourseDto courseDto) {
        apiService.createCourse(courseDto).enqueue(new Callback<ApiResponse<CourseDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CourseDto>> call, Response<ApiResponse<CourseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CourseDto> apiResponse = response.body();

                    if ("success".equals(apiResponse.getStatus()) && apiResponse.getData() != null) {
                        CourseDto newCourseDto = apiResponse.getData();
                        courseDtoList.add(newCourseDto);
                        courseList.add(newCourseDto.toCourse());
                        adapter.notifyItemInserted(courseList.size() - 1);
                        Toast.makeText(CourseManagementActivity.this, "课程添加成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CourseManagementActivity.this,
                                apiResponse.getMessage() != null ? apiResponse.getMessage() : "课程添加失败",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CourseManagementActivity.this, "课程添加失败: " + response.message(), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CourseDto>> call, Throwable t) {
                Toast.makeText(CourseManagementActivity.this, "网络请求失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditCourseDialog(int position, Course course) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_course, null);
        TextInputEditText etCourseName = dialogView.findViewById(R.id.etCourseName);
        TextInputEditText etTeacher = dialogView.findViewById(R.id.etTeacherName);
        TextInputEditText etClassroom = dialogView.findViewById(R.id.etClassroom);
        Spinner spWeekday = dialogView.findViewById(R.id.spWeekday);
        Spinner spStartSection = dialogView.findViewById(R.id.spStartSection);
        Spinner spEndSection = dialogView.findViewById(R.id.spEndSection);

        etCourseName.setText(course.getName());
        etTeacher.setText(course.getTeacher());
        etClassroom.setText(course.getRoom());
        spWeekday.setSelection(course.getDay() - 1);
        spStartSection.setSelection(course.getStartSection() - 1);
        spEndSection.setSelection(course.getEndSection() - 1);

        CourseDto originalCourse = getCourseDtoById(course.getId());
        if (originalCourse == null) {
            Toast.makeText(this, "无法找到原始课程数据", Toast.LENGTH_SHORT).show();
            return;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("编辑课程")
                .setView(dialogView)
                .setPositiveButton("保存", (dialog, which) -> {
                    String name = etCourseName.getText().toString().trim();
                    String teacher = etTeacher.getText().toString().trim();
                    String classroom = etClassroom.getText().toString().trim();
                    int weekday = spWeekday.getSelectedItemPosition() + 1;
                    int startSection = spStartSection.getSelectedItemPosition() + 1;
                    int endSection = spEndSection.getSelectedItemPosition() + 1;
                    int startWeek = originalCourse.getStartWeek();
                    int endWeek = originalCourse.getEndWeek();
                    String semesterId = originalCourse.getSemesterId();

                    if (!name.isEmpty() && !teacher.isEmpty()) {
                        CourseDto updatedCourse = new CourseDto(
                                course.getId(), name, teacher, classroom, weekday,
                                startSection, endSection, startWeek, endWeek, semesterId);
                        updateCourse(position, course.getId(), updatedCourse);
                    } else {
                        Toast.makeText(this, "请填写必填的课程信息", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void updateCourse(int position, int courseId, CourseDto courseDto) {
        apiService.updateCourse(courseId, courseDto).enqueue(new Callback<ApiResponse<CourseDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CourseDto>> call, Response<ApiResponse<CourseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CourseDto> apiResponse = response.body();

                    if ("success".equals(apiResponse.getStatus()) && apiResponse.getData() != null) {
                        CourseDto updatedCourseDto = apiResponse.getData();
                        courseDtoList.set(position, updatedCourseDto);
                        courseList.set(position, updatedCourseDto.toCourse());
                        adapter.notifyItemChanged(position);
                        Toast.makeText(CourseManagementActivity.this, "课程更新成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CourseManagementActivity.this,
                                apiResponse.getMessage() != null ? apiResponse.getMessage() : "课程更新失败",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CourseManagementActivity.this, "课程更新失败: " + response.message(), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CourseDto>> call, Throwable t) {
                Toast.makeText(CourseManagementActivity.this, "网络请求失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCourse(int position) {
        CourseDto courseDto = courseDtoList.get(position);

        new MaterialAlertDialogBuilder(this)
                .setTitle("删除课程")
                .setMessage("确定要删除 \"" + courseDto.getName() + "\" 吗?")
                .setPositiveButton("删除", (dialog, which) -> {
                    apiService.deleteCourse(courseDto.getId()).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<Void> apiResponse = response.body();

                                if ("success".equals(apiResponse.getStatus())) {
                                    courseDtoList.remove(position);
                                    courseList.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    Toast.makeText(CourseManagementActivity.this, "课程已删除", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CourseManagementActivity.this,
                                            apiResponse.getMessage() != null ? apiResponse.getMessage() : "课程删除失败",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(CourseManagementActivity.this, "课程删除失败: " + response.message(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                            Toast.makeText(CourseManagementActivity.this, "网络请求失败: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_management, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_refresh) {
            loadCourses();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCourseClick(int position, Course course) {
        CourseDto courseDto = courseDtoList.get(position);

        // 显示课程详情
        new MaterialAlertDialogBuilder(this)
                .setTitle(courseDto.getName())
                .setMessage("教师: " + courseDto.getTeacher() +
                        "\n教室: " + courseDto.getClassroom() +
                        "\n时间: 周" + courseDto.getWeekday() +
                        " 第" + courseDto.getStartSection() + "-" + courseDto.getEndSection() + "节" +
                        "\n周次: 第" + courseDto.getStartWeek() + "-" + courseDto.getEndWeek() + "周" +
                        "\n学期: " + courseDto.getSemesterId())
                .setPositiveButton("确定", null)
                .show();
    }

    @Override
    public void onCourseEditClick(int position, Course course) {
        showEditCourseDialog(position, course);
    }

    @Override
    public void onCourseDeleteClick(int position) {
        deleteCourse(position);
    }

    // 检查网络连接
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
            Log.d("CourseManagement", "网络连接状态: " + (isConnected ? "已连接" : "未连接"));
            return isConnected;
        }
        return false;
    }

    private CourseDto getCourseDtoById(int id) {
        for (CourseDto dto : courseDtoList) {
            if (dto.getId() == id) {
                return dto;
            }
        }
        return null;
    }
}