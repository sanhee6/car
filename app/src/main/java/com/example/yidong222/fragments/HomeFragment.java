package com.example.yidong222.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidong222.R;
import com.example.yidong222.CoursesActivity;
import com.example.yidong222.AssignmentActivity;
import com.example.yidong222.ExamActivity;
import com.example.yidong222.GradeActivity;
import com.example.yidong222.adapters.TodayCourseAdapter;
import com.example.yidong222.api.ApiClient;
import com.example.yidong222.models.ApiResponse;
import com.example.yidong222.models.CourseSchedule;
import com.example.yidong222.models.TimetableCourse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private TextView tvMoreCourses;
    private LinearLayout llCourse, llAssignment, llExam, llGrade;
    private RecyclerView rvTodayCourses;
    private TextView tvNoCourses;
    private TextView tvCurrentDate;
    private TextView tvMotivationQuote;
    private TodayCourseAdapter todayCourseAdapter;

    // 日期选择器
    private TextView tvMonday, tvTuesday, tvWednesday, tvThursday, tvFriday, tvSaturday, tvSunday;
    private List<TextView> daySelectors = new ArrayList<>();

    // 当前选中的星期几（1-7 表示周一到周日）
    private int selectedDayOfWeek;

    // 鼓励语列表
    private final String[] motivationQuotes = {
            "行动是治愈恐惧的良药，而犹豫、拖延将不断滋养恐惧。",
            "一个人的成功不是因为他拥有什么，而是因为他给予了什么。",
            "每一个成功者都有一个开始。勇于开始，才能找到成功的路。",
            "教育的根是苦的，但其果实是甜的。",
            "没有人能回到过去重新开始，但谁都可以从今天开始创造全新的明天。",
            "知识是从刻苦钻研中得来的，任何成就都坚持不懈的结果。",
            "梦想不会自动实现，必须配合行动与坚持，这就是成功的秘诀。",
            "每天努力一点点，每天进步一点点，这就是最大的成就。",
            "把困难踩在脚下，才能更快地接近成功的顶峰。",
            "学习是一种态度，学习是一种习惯，更是一种乐趣。"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        initDateSelector();
        setRandomMotivationQuote();
        loadCoursesByDay(selectedDayOfWeek);
        setupListeners();
    }

    private void initViews(View view) {
        tvMoreCourses = view.findViewById(R.id.tvMoreCourses);
        rvTodayCourses = view.findViewById(R.id.rv_today_courses);
        tvNoCourses = view.findViewById(R.id.tv_no_courses);
        tvCurrentDate = view.findViewById(R.id.tv_current_date);

        // 初始化日期选择器
        tvMonday = view.findViewById(R.id.tv_monday);
        tvTuesday = view.findViewById(R.id.tv_tuesday);
        tvWednesday = view.findViewById(R.id.tv_wednesday);
        tvThursday = view.findViewById(R.id.tv_thursday);
        tvFriday = view.findViewById(R.id.tv_friday);
        tvSaturday = view.findViewById(R.id.tv_saturday);
        tvSunday = view.findViewById(R.id.tv_sunday);

        // 添加到列表以便批量处理
        daySelectors.add(tvMonday);
        daySelectors.add(tvTuesday);
        daySelectors.add(tvWednesday);
        daySelectors.add(tvThursday);
        daySelectors.add(tvFriday);
        daySelectors.add(tvSaturday);
        daySelectors.add(tvSunday);

        // 初始化鼓励语
        tvMotivationQuote = view.findViewById(R.id.tv_motivation_quote);

        // 初始化功能按钮
        llCourse = view.findViewById(R.id.llCourse);
        llAssignment = view.findViewById(R.id.llAssignment);
        llExam = view.findViewById(R.id.llExam);
        llGrade = view.findViewById(R.id.llGrade);
    }

    private void setupRecyclerView() {
        todayCourseAdapter = new TodayCourseAdapter();
        rvTodayCourses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTodayCourses.setAdapter(todayCourseAdapter);
    }

    /**
     * 初始化日期选择器，默认选中当天
     */
    private void initDateSelector() {
        // 获取今天是星期几
        Calendar calendar = Calendar.getInstance();
        int todayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // 转换为中国习惯（周一为1，周日为7）
        todayOfWeek = todayOfWeek == Calendar.SUNDAY ? 7 : todayOfWeek - 1;
        selectedDayOfWeek = todayOfWeek;

        Log.d(TAG, "今天是周" + getDayOfWeekChinese(selectedDayOfWeek));

        // 更新日期标题
        updateDateTitle();

        // 设置默认选中效果
        updateDaySelectors();

        // 设置日期选择器点击事件
        for (int i = 0; i < daySelectors.size(); i++) {
            final int dayIndex = i + 1; // 1-7 表示周一到周日
            daySelectors.get(i).setOnClickListener(v -> {
                selectedDayOfWeek = dayIndex;
                Log.d(TAG, "选择了周" + getDayOfWeekChinese(selectedDayOfWeek));
                updateDaySelectors();
                updateDateTitle();
                loadCoursesByDay(selectedDayOfWeek);
            });
        }
    }

    /**
     * 更新日期标题
     */
    private void updateDateTitle() {
        if (isTodaySelected()) {
            tvCurrentDate.setText("今日");
        } else {
            tvCurrentDate.setText("周" + getDayOfWeekChinese(selectedDayOfWeek));
        }
    }

    /**
     * 检查是否选择了今天
     */
    private boolean isTodaySelected() {
        Calendar calendar = Calendar.getInstance();
        int todayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        todayOfWeek = todayOfWeek == Calendar.SUNDAY ? 7 : todayOfWeek - 1;
        return selectedDayOfWeek == todayOfWeek;
    }

    /**
     * 更新日期选择器的选中状态
     */
    private void updateDaySelectors() {
        for (int i = 0; i < daySelectors.size(); i++) {
            TextView daySelector = daySelectors.get(i);
            int dayIndex = i + 1; // 1-7 表示周一到周日

            if (dayIndex == selectedDayOfWeek) {
                // 选中状态
                daySelector.setBackgroundResource(R.drawable.bg_date_selected);
                daySelector.setTextColor(getResources().getColor(R.color.white));
            } else {
                // 未选中状态
                daySelector.setBackgroundResource(R.drawable.bg_date_normal);
                daySelector.setTextColor(getResources().getColor(R.color.text_secondary));
            }
        }
    }

    /**
     * 设置随机鼓励语
     */
    private void setRandomMotivationQuote() {
        if (tvMotivationQuote != null) {
            // 随机选择鼓励语
            int randomQuoteIndex = new Random().nextInt(motivationQuotes.length);
            tvMotivationQuote.setText(motivationQuotes[randomQuoteIndex]);

            // 随机选择背景图片
            int[] backgroundResources = {
                    R.drawable.motivation_bg_1,
                    R.drawable.motivation_bg_2,
                    R.drawable.motivation_bg_3,
                    R.drawable.motivation_bg_4,
                    R.drawable.motivation_bg_5
            };

            int randomBgIndex = new Random().nextInt(backgroundResources.length);

            // 查找背景视图并设置背景
            View backgroundView = getView().findViewById(R.id.background_view);
            if (backgroundView != null) {
                backgroundView.setBackgroundResource(backgroundResources[randomBgIndex]);
            }
        }
    }

    /**
     * 按指定天加载课程
     * 
     * @param dayOfWeek 星期几（1-7表示周一到周日）
     */
    private void loadCoursesByDay(int dayOfWeek) {
        // 显示加载状态
        rvTodayCourses.setVisibility(View.GONE);
        tvNoCourses.setVisibility(View.VISIBLE);
        tvNoCourses.setText("正在加载课程数据...");

        Log.d(TAG, "正在加载周" + getDayOfWeekChinese(dayOfWeek) + "的课程");

        // 确保使用正确的URL
        Log.d(TAG, "当前使用的API URL: " + ApiClient.getBaseUrl());

        // 尝试优先从课程表视图获取课程数据 - 如果可用的话
        try {
            List<TimetableCourse> allViewCourses = TimetableFragment.getAllCourses();

            if (allViewCourses != null && !allViewCourses.isEmpty()) {
                Log.d(TAG, "从课程表视图获取到 " + allViewCourses.size() + " 门课程");

                // 筛选特定星期几的课程
                List<CourseSchedule> filteredCourses = new ArrayList<>();

                // 获取当前日期代表的周数
                Calendar calendar = Calendar.getInstance();
                int currentWeek = getCurrentWeekNumber(); // 当前教学周数

                for (TimetableCourse course : allViewCourses) {
                    if (course.getWeekday() == dayOfWeek &&
                            course.getStartWeek() <= currentWeek &&
                            course.getEndWeek() >= currentWeek) {

                        // 将 TimetableCourse 转换为 CourseSchedule
                        CourseSchedule scheduleItem = new CourseSchedule(
                                course.getDatabaseId(),
                                course.getName(),
                                course.getTeacher(),
                                getClassTimeString(course.getWeekday(), course.getStartSection(),
                                        course.getEndSection()),
                                course.getClassroom(),
                                null,
                                null);

                        filteredCourses.add(scheduleItem);
                        Log.d(TAG, "从视图中匹配到课程: " + course.getName() + ", 星期" + dayOfWeek);
                    }
                }

                if (!filteredCourses.isEmpty()) {
                    // 显示筛选后的课程
                    todayCourseAdapter.setCourseList(filteredCourses);
                    rvTodayCourses.setVisibility(View.VISIBLE);
                    tvNoCourses.setVisibility(View.GONE);
                    return; // 成功找到课程数据，结束方法
                } else {
                    Log.d(TAG, "课程表视图中没有找到周" + getDayOfWeekChinese(dayOfWeek) + "的课程");
                }
            } else {
                Log.d(TAG, "课程表视图中没有可用数据");
            }
        } catch (Exception e) {
            Log.e(TAG, "从课程表视图获取数据失败: " + e.getMessage());
        }

        // 如果从课程表视图获取数据失败或没有找到匹配的课程，则从API获取
        Log.d(TAG, "从API获取课程数据");
        Call<ApiResponse<List<CourseSchedule>>> call = ApiClient.getCourseApiService().getCourseSchedules(1, 100);

        call.enqueue(new Callback<ApiResponse<List<CourseSchedule>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CourseSchedule>>> call,
                    Response<ApiResponse<List<CourseSchedule>>> response) {
                if (getContext() == null)
                    return; // Fragment已销毁

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<CourseSchedule>> apiResponse = response.body();
                    List<CourseSchedule> allCourses = apiResponse.getData();

                    if (allCourses != null && !allCourses.isEmpty()) {
                        Log.d(TAG, "成功从API获取课程，总数: " + allCourses.size());

                        // 打印所有课程时间字段以便调试
                        for (CourseSchedule course : allCourses) {
                            Log.d(TAG, "课程: " + course.getCourseName() + ", 时间: " + course.getClassTime());
                        }

                        // 筛选指定日期的课程
                        List<CourseSchedule> filteredCourses = filterCoursesByDay(allCourses, dayOfWeek);

                        Log.d(TAG, "筛选后课程数量: " + filteredCourses.size());

                        if (!filteredCourses.isEmpty()) {
                            todayCourseAdapter.setCourseList(filteredCourses);
                            rvTodayCourses.setVisibility(View.VISIBLE);
                            tvNoCourses.setVisibility(View.GONE);
                        } else {
                            // 当天无课
                            rvTodayCourses.setVisibility(View.GONE);
                            tvNoCourses.setVisibility(View.VISIBLE);
                            if (isTodaySelected()) {
                                tvNoCourses.setText("今日暂无课程");
                            } else {
                                tvNoCourses.setText("所选日期暂无课程");
                            }
                        }
                    } else {
                        // 没有课程数据
                        Log.d(TAG, "API返回空课程列表");
                        rvTodayCourses.setVisibility(View.GONE);
                        tvNoCourses.setVisibility(View.VISIBLE);
                        tvNoCourses.setText("没有课程数据");
                    }
                } else {
                    // API请求失败
                    Log.e(TAG, "API请求失败: " + response.code());
                    handleApiError(response);

                    // 尝试切换到正确的服务器地址
                    if (getActivity() != null) {
                        new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                                .setTitle("服务器连接错误")
                                .setMessage("无法连接到当前服务器，是否尝试切换服务器？")
                                .setPositiveButton("切换服务器", (dialog, which) -> {
                                    ApiClient.tryNextServer();
                                    new android.os.Handler().postDelayed(() -> {
                                        if (getActivity() != null && isAdded() && !getActivity().isFinishing()) {
                                            loadCoursesByDay(dayOfWeek);
                                        }
                                    }, 500);
                                })
                                .setNegativeButton("取消", null)
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CourseSchedule>>> call, Throwable t) {
                if (getContext() == null)
                    return; // Fragment已销毁

                // 网络请求失败
                Log.e(TAG, "网络请求失败: " + t.getMessage());

                // 给用户选择切换服务器或重试的选项
                if (getActivity() != null) {
                    new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                            .setTitle("网络连接错误")
                            .setMessage("连接服务器失败: " + t.getMessage() + "\n是否尝试切换服务器或重试？")
                            .setPositiveButton("切换服务器", (dialog, which) -> {
                                ApiClient.tryNextServer();
                                Toast.makeText(getContext(), "已切换服务器，正在重试...", Toast.LENGTH_SHORT).show();
                                new android.os.Handler().postDelayed(() -> {
                                    if (getActivity() != null && isAdded() && !getActivity().isFinishing()) {
                                        loadCoursesByDay(dayOfWeek);
                                    }
                                }, 500);
                            })
                            .setNeutralButton("重试", (dialog, which) -> {
                                Toast.makeText(getContext(), "正在重试...", Toast.LENGTH_SHORT).show();
                                loadCoursesByDay(dayOfWeek);
                            })
                            .setNegativeButton("取消", (dialog, which) -> {
                                Toast.makeText(getContext(), "加载课程失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                rvTodayCourses.setVisibility(View.GONE);
                                tvNoCourses.setVisibility(View.VISIBLE);
                                tvNoCourses.setText("加载失败，请检查网络连接");
                            })
                            .show();
                } else {
                    Toast.makeText(getContext(), "加载课程失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    rvTodayCourses.setVisibility(View.GONE);
                    tvNoCourses.setVisibility(View.VISIBLE);
                    tvNoCourses.setText("加载失败，请检查网络连接");
                }
            }
        });
    }

    /**
     * 获取当前教学周数
     * 模拟实现，实际应用中应该从教务系统或设置中获取
     */
    private int getCurrentWeekNumber() {
        // 这里先返回固定值，实际应用中应该从配置或缓存中获取
        return 2; // 假设当前是第2教学周
    }

    /**
     * 根据课程的星期和节次生成时间描述字符串
     */
    private String getClassTimeString(int weekday, int startSection, int endSection) {
        return "周" + getDayOfWeekChinese(weekday) + " 第" + startSection + "-" + endSection + "节";
    }

    /**
     * 筛选指定星期几的课程
     * 
     * @param allCourses 所有课程列表
     * @param dayOfWeek  星期几（1-7表示周一到周日）
     * @return 筛选后的课程列表
     */
    private List<CourseSchedule> filterCoursesByDay(List<CourseSchedule> allCourses, int dayOfWeek) {
        List<CourseSchedule> filteredCourses = new ArrayList<>();
        String dayPattern = "周" + getDayOfWeekChinese(dayOfWeek);
        String[] otherPatterns = { "星期" + getDayOfWeekChinese(dayOfWeek), "礼拜" + getDayOfWeekChinese(dayOfWeek) };

        Log.d(TAG, "搜索模式: " + dayPattern + " 或 " + otherPatterns[0] + " 或 " + otherPatterns[1]);

        // 筛选包含指定上课时间的课程
        for (CourseSchedule course : allCourses) {
            String classTime = course.getClassTime();
            if (classTime != null) {
                boolean matches = false;
                // 检查主匹配模式
                if (classTime.contains(dayPattern)) {
                    matches = true;
                    Log.d(TAG, "匹配课程: " + course.getCourseName() + " (匹配模式: " + dayPattern + ")");
                }

                // 检查替代匹配模式
                for (String pattern : otherPatterns) {
                    if (classTime.contains(pattern)) {
                        matches = true;
                        Log.d(TAG, "匹配课程: " + course.getCourseName() + " (匹配模式: " + pattern + ")");
                    }
                }

                // 检查数字匹配模式 (例如: "1-2" 表示周一第1-2节)
                String dayNumber = String.valueOf(dayOfWeek);
                if (classTime.matches(".*\\b" + dayNumber + "-\\d+.*") ||
                        classTime.matches(".*\\b" + dayNumber + ",\\d+.*") ||
                        classTime.matches(".*\\b" + dayNumber + "节.*")) {
                    matches = true;
                    Log.d(TAG, "匹配课程: " + course.getCourseName() + " (数字匹配: " + dayNumber + ")");
                }

                if (matches) {
                    filteredCourses.add(course);
                }
            }
        }

        // 如果没有筛选到课程，作为应急方案，我们将添加包含对应日期数字的课程 (如果有的话)
        if (filteredCourses.isEmpty()) {
            Log.d(TAG, "使用应急方案: 搜索包含数字 " + dayOfWeek + " 的课程");
            for (CourseSchedule course : allCourses) {
                String classTime = course.getClassTime();
                if (classTime != null && classTime.contains(String.valueOf(dayOfWeek))) {
                    Log.d(TAG, "应急匹配课程: " + course.getCourseName() + " (包含数字: " + dayOfWeek + ")");
                    filteredCourses.add(course);
                }
            }
        }

        return filteredCourses;
    }

    /**
     * 获取中文星期几表示
     * 
     * @param dayOfWeek 星期几，1-7表示周一到周日
     * @return 中文表示，如"一"、"二"等
     */
    private String getDayOfWeekChinese(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            case 6:
                return "六";
            case 7:
                return "日";
            default:
                return "";
        }
    }

    /**
     * 处理API错误
     * 
     * @param response API响应
     */
    private void handleApiError(Response<ApiResponse<List<CourseSchedule>>> response) {
        // 记录API错误
        ApiClient.logResponseError(response);

        // 显示错误信息
        String errorMsg = "加载课程失败，请稍后重试";
        try {
            if (response.errorBody() != null) {
                errorMsg += ": " + response.errorBody().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
        rvTodayCourses.setVisibility(View.GONE);
        tvNoCourses.setVisibility(View.VISIBLE);
        tvNoCourses.setText("加载失败，请稍后重试");
    }

    private void setupListeners() {
        tvMoreCourses.setOnClickListener(v -> {
            // 切换到课表页面，使用MainActivity的底部导航
            if (getActivity() != null) {
                try {
                    BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
                    if (bottomNav != null) {
                        bottomNav.setSelectedItemId(R.id.nav_schedule);
                    }
                } catch (Exception e) {
                    // 处理可能的异常，防止崩溃
                    Toast.makeText(getContext(), "切换页面失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 课程功能点击事件
        llCourse.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(getActivity(), CoursesActivity.class);
                // 添加标志，确保Activity正确启动
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Log.d(TAG, "正在启动CoursesActivity");
            } catch (Exception e) {
                Log.e(TAG, "打开课程页面失败", e);
                // 显示更详细的错误信息
                String errorMessage = "打开课程页面失败: " + e.getMessage();
                // 检查是否是ActivityNotFoundException，这通常表示清单文件中未注册Activity
                if (e instanceof android.content.ActivityNotFoundException) {
                    errorMessage += "\n请确保CoursesActivity已在AndroidManifest.xml中注册";
                }
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();

                // 记录错误到控制台，帮助调试
                e.printStackTrace();
            }
        });

        // 作业功能点击事件
        llAssignment.setOnClickListener(v -> {
            try {
                startActivity(new Intent(getActivity(), AssignmentActivity.class));
            } catch (Exception e) {
                Toast.makeText(getContext(), "打开作业页面失败", Toast.LENGTH_SHORT).show();
            }
        });

        // 考试功能点击事件
        llExam.setOnClickListener(v -> {
            try {
                startActivity(new Intent(getActivity(), ExamActivity.class));
            } catch (Exception e) {
                Toast.makeText(getContext(), "打开考试页面失败", Toast.LENGTH_SHORT).show();
            }
        });

        // 成绩功能点击事件
        llGrade.setOnClickListener(v -> {
            try {
                startActivity(new Intent(getActivity(), GradeActivity.class));
            } catch (Exception e) {
                Toast.makeText(getContext(), "打开成绩页面失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}