package com.example.yidong222.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.yidong222.CourseDetailActivity;
import com.example.yidong222.R;
import com.example.yidong222.data.DataSyncManager;
import com.example.yidong222.models.CourseSchedule;
import com.example.yidong222.models.TimetableCourse;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class TimetableFragment extends Fragment {

    private static final String TAG = "TimetableFragment";
    private ViewGroup timetableGrid;
    private TextView tvNoTimetable;
    private LinearLayout headerLayout;
    private LinearLayout sectionLayout;
    private FloatingActionButton fabAddCourse;
    private int currentWeek = 2; // 默认当前为第2周
    private TextView tvCurrentWeek;
    private ImageButton btnPrevWeek, btnNextWeek;

    // 定义课程数据变更监听器接口
    public interface OnCourseDataChangeListener {
        void onCourseDataChanged(int week);
    }

    // 监听器列表
    private static final List<OnCourseDataChangeListener> listeners = new ArrayList<>();

    // 添加监听器
    public static void addOnCourseDataChangeListener(OnCourseDataChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    // 移除监听器
    public static void removeOnCourseDataChangeListener(OnCourseDataChangeListener listener) {
        listeners.remove(listener);
    }

    // 通知所有监听器数据已变更
    private static void notifyDataChanged(int week) {
        for (OnCourseDataChangeListener listener : listeners) {
            listener.onCourseDataChanged(week);
        }
    }

    // 获取所有课程数据（供其他Fragment使用）
    public static List<TimetableCourse> getAllCourses() {
        return new ArrayList<>(allCourses);
    }

    // 静态课程数据，可以被其他Fragment访问
    private static List<TimetableCourse> allCourses = new ArrayList<>();

    // 动态计算的布局常量
    private int columnWidth = 0; // 根据屏幕宽度动态计算
    private static final int ROW_HEIGHT = 50; // 每节课的高度
    private static final int LEFT_COLUMN_WIDTH = 30; // 左侧课节列宽度
    private static final int TOP_ROW_HEIGHT = 40; // 顶部行高度
    private static final int COURSE_MARGIN = 1; // 课程卡片间距
    private static final int MAX_SECTIONS = 12; // 最大课节数
    private static final int DAYS_IN_WEEK = 7; // 一周7天

    // 课程背景颜色
    private int[] courseColors = {
            R.color.course_red,
            R.color.course_purple,
            R.color.course_blue,
            R.color.course_green,
            R.color.course_orange,
            R.color.course_deep_orange,
            R.color.course_deep_purple,
            R.color.course_light_blue
    };

    // 课程时间对照表
    private String[] sectionTimeMap = {
            "8:00-8:45", "8:55-9:40",
            "10:00-10:45", "10:55-11:40",
            "14:00-14:45", "14:55-15:40",
            "16:00-16:45", "16:55-17:40",
            "19:00-19:45", "19:55-20:40",
            "20:50-21:35", "21:45-22:30"
    };

    private DataSyncManager dataSyncManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSyncManager = DataSyncManager.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);

        // 初始化视图
        timetableGrid = view.findViewById(R.id.timetableGrid);
        tvNoTimetable = view.findViewById(R.id.tvNoTimetable);
        headerLayout = view.findViewById(R.id.headerLayout);
        sectionLayout = view.findViewById(R.id.sectionLayout);
        fabAddCourse = view.findViewById(R.id.fabAddCourse);
        tvCurrentWeek = view.findViewById(R.id.tvCurrentWeek);
        btnPrevWeek = view.findViewById(R.id.btnPrevWeek);
        btnNextWeek = view.findViewById(R.id.btnNextWeek);

        // 计算列宽
        calculateColumnWidth();

        // 初始化头部和侧边栏
        initHeaderAndSections();

        // 设置添加课程按钮点击事件
        fabAddCourse.setOnClickListener(v -> showAddCourseDialog());

        // 设置周数选择器
        tvCurrentWeek.setText("第" + currentWeek + "周");
        btnPrevWeek.setOnClickListener(v -> {
            if (currentWeek > 1) {
                currentWeek--;
                tvCurrentWeek.setText("第" + currentWeek + "周");
                updateTimetable(currentWeek);
            }
        });
        btnNextWeek.setOnClickListener(v -> {
            if (currentWeek < 20) {
                currentWeek++;
                tvCurrentWeek.setText("第" + currentWeek + "周");
                updateTimetable(currentWeek);
            }
        });

        // 从服务器同步课程数据
        syncCoursesFromServer();

        return view;
    }

    private void calculateColumnWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        // 列宽 = (屏幕宽度 - 左侧课节列宽度) / 7天
        columnWidth = (screenWidth - LEFT_COLUMN_WIDTH) / DAYS_IN_WEEK;
    }

    private void initHeaderAndSections() {
        // 添加头部日期行
        headerLayout.removeAllViews();

        // 先添加左上角空白
        TextView cornerView = new TextView(requireContext());
        cornerView.setWidth(LEFT_COLUMN_WIDTH);
        cornerView.setHeight(TOP_ROW_HEIGHT);
        cornerView.setText("05\n月");
        cornerView.setTextSize(10);
        cornerView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_gray));
        cornerView.setGravity(android.view.Gravity.CENTER);
        headerLayout.addView(cornerView);

        // 添加周一到周日
        String[] weekdays = { "周一", "周二", "周三", "周四", "周五", "周六", "周日" };
        String[] dates = { "29日", "30日", "01日", "02日", "03日", "04日", "05日" };

        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            LinearLayout dayLayout = new LinearLayout(requireContext());
            dayLayout.setOrientation(LinearLayout.VERTICAL);
            dayLayout.setLayoutParams(new LinearLayout.LayoutParams(columnWidth, TOP_ROW_HEIGHT));
            dayLayout.setGravity(android.view.Gravity.CENTER);

            TextView tvWeekday = new TextView(requireContext());
            tvWeekday.setText(weekdays[i]);
            tvWeekday.setTextSize(12);
            tvWeekday.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_dark));
            tvWeekday.setGravity(android.view.Gravity.CENTER);

            TextView tvDate = new TextView(requireContext());
            tvDate.setText(dates[i]);
            tvDate.setTextSize(10);
            tvDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_gray));
            tvDate.setGravity(android.view.Gravity.CENTER);

            dayLayout.addView(tvWeekday);
            dayLayout.addView(tvDate);
            headerLayout.addView(dayLayout);
        }

        // 添加左侧课节栏
        sectionLayout.removeAllViews();
        for (int i = 1; i <= MAX_SECTIONS; i++) {
            TextView sectionView = new TextView(requireContext());
            sectionView.setLayoutParams(new LinearLayout.LayoutParams(
                    LEFT_COLUMN_WIDTH, ROW_HEIGHT));
            sectionView.setText(String.valueOf(i));
            sectionView.setTextSize(10);
            sectionView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_gray));
            sectionView.setGravity(android.view.Gravity.CENTER);
            sectionLayout.addView(sectionView);
        }
    }

    private void syncCoursesFromServer() {
        // 显示加载中
        tvNoTimetable.setVisibility(View.VISIBLE);
        tvNoTimetable.setText(R.string.loading);

        // 记录重试次数
        int maxRetries = 3;
        int[] retryCount = { 0 };

        // 从服务器同步课程数据
        loadDataWithRetry(maxRetries, retryCount);
    }

    private void loadDataWithRetry(int maxRetries, int[] retryCount) {
        dataSyncManager.syncCourseSchedules(new DataSyncManager.SyncCallback<List<CourseSchedule>>() {
            @Override
            public void onSuccess(List<CourseSchedule> courseSchedules) {
                if (courseSchedules != null && !courseSchedules.isEmpty()) {
                    // 转换为TimetableCourse
                    convertAndSaveCourses(courseSchedules);

                    // 渲染课表
                    updateTimetable(currentWeek);

                    // 隐藏加载提示
                    tvNoTimetable.setVisibility(View.GONE);
                } else {
                    // 显示无数据提示
                    tvNoTimetable.setText(R.string.no_data);
                    tvNoTimetable.setVisibility(View.VISIBLE);

                    // 尝试从本地加载数据
                    List<CourseSchedule> localCourses = dataSyncManager.getLocalCourseSchedules();
                    if (localCourses != null && !localCourses.isEmpty()) {
                        convertAndSaveCourses(localCourses);
                        updateTimetable(currentWeek);
                        tvNoTimetable.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "显示本地缓存数据", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "同步课程表出错", error);

                // 记录详细错误信息
                String errorMessage = error.getMessage();
                Log.e(TAG, "错误信息: " + errorMessage);

                if (errorMessage != null && errorMessage.contains("Failed to connect")) {
                    Log.e(TAG, "连接服务器失败，请检查网络连接和服务器状态");
                    Toast.makeText(requireContext(), "连接服务器失败，请检查网络连接和服务器状态", Toast.LENGTH_LONG).show();
                } else if (errorMessage != null && errorMessage.contains("timeout")) {
                    Log.e(TAG, "连接超时，服务器响应时间过长");
                    Toast.makeText(requireContext(), "连接超时，服务器响应时间过长", Toast.LENGTH_LONG).show();
                }

                // 如果还可以重试，则重试
                if (retryCount[0] < maxRetries) {
                    retryCount[0]++;
                    Log.d(TAG, "重试获取课程表，第 " + retryCount[0] + " 次");
                    tvNoTimetable.setText("正在重试 (" + retryCount[0] + "/" + maxRetries + ")...");

                    // 延迟1秒后重试
                    new Handler().postDelayed(() -> {
                        loadDataWithRetry(maxRetries, retryCount);
                    }, 1000);
                    return;
                }

                // 显示错误提示
                tvNoTimetable.setText(R.string.network_error);
                tvNoTimetable.setVisibility(View.VISIBLE);

                // 添加重试按钮
                tvNoTimetable.setOnClickListener(v -> {
                    tvNoTimetable.setText(R.string.loading);
                    retryCount[0] = 0;
                    loadDataWithRetry(maxRetries, retryCount);
                });

                // 尝试从本地加载数据
                List<CourseSchedule> localCourses = dataSyncManager.getLocalCourseSchedules();
                if (localCourses != null && !localCourses.isEmpty()) {
                    convertAndSaveCourses(localCourses);
                    updateTimetable(currentWeek);
                    tvNoTimetable.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "显示本地缓存数据", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "网络错误：" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 将API返回的CourseSchedule转换为TimetableCourse
    private void convertAndSaveCourses(List<CourseSchedule> courseSchedules) {
        allCourses.clear();

        if (courseSchedules == null) {
            Log.e(TAG, "收到的课程列表为null");
            return;
        }

        for (CourseSchedule schedule : courseSchedules) {
            if (schedule == null) {
                Log.e(TAG, "课程列表中包含null的课程项");
                continue;
            }

            String courseName = schedule.getCourseName();
            String classroom = schedule.getClassroom();
            String teacherName = schedule.getTeacherName();
            String classTime = schedule.getClassTime();

            // 检查并记录关键字段缺失情况
            if (courseName == null) {
                Log.w(TAG, "课程名称为null, 课程ID: " + schedule.getId());
                courseName = "未命名课程";
            }
            if (classroom == null) {
                Log.w(TAG, "教室为null, 课程ID: " + schedule.getId() + ", 课程名称: " + courseName);
                classroom = "未知教室";
            }
            if (teacherName == null) {
                Log.w(TAG, "教师名称为null, 课程ID: " + schedule.getId() + ", 课程名称: " + courseName);
                teacherName = "未知教师";
            }
            if (classTime == null) {
                Log.w(TAG, "上课时间为null, 课程ID: " + schedule.getId() + ", 课程名称: " + courseName);
                classTime = "周一 1-2节"; // 默认值
            }

            String weekday = extractWeekday(classTime);
            String sectionRange = extractSection(classTime);
            int[] sections = parseSectionRange(sectionRange);

            if (sections != null && sections.length == 2) {
                TimetableCourse course = new TimetableCourse(
                        courseName,
                        classroom,
                        teacherName,
                        sections[0], // 开始节次
                        sections[1], // 结束节次
                        convertWeekdayToNumber(weekday), // 星期几
                        1, // 开始周，假设为第1周
                        20 // 结束周，假设为第20周
                );
                course.setDatabaseId(schedule.getId());
                allCourses.add(course);
            }
        }

        Log.d(TAG, "成功转换 " + allCourses.size() + " 条课程数据");
    }

    // 从classTime中提取星期几，例如："周一 1-2节" -> "周一"
    private String extractWeekday(String classTime) {
        if (classTime == null || classTime.isEmpty()) {
            Log.w(TAG, "classTime为空，使用默认值：周一");
            return "周一"; // 默认周一
        }

        String[] parts = classTime.split(" ");
        if (parts.length > 0) {
            // 处理"星期X"格式，转换为"周X"格式
            String weekday = parts[0];
            if (weekday.startsWith("星期")) {
                weekday = "周" + weekday.substring(2);
                Log.d(TAG, "将'星期'格式转换为'周'格式: " + parts[0] + " -> " + weekday);
            }
            return weekday;
        }

        return "周一"; // 默认周一
    }

    // 从classTime中提取节次范围，例如："周一 1-2节" -> "1-2"
    private String extractSection(String classTime) {
        if (classTime == null || classTime.isEmpty()) {
            Log.w(TAG, "classTime为空，使用默认值：1-2");
            return "1-2"; // 默认1-2节
        }

        String[] parts = classTime.split(" ");
        if (parts.length > 1) {
            String section = parts[1].replace("节", "");
            // 处理"第X-Y节"格式，提取X-Y
            if (section.startsWith("第")) {
                section = section.substring(1);
                Log.d(TAG, "处理'第X-Y节'格式: " + parts[1] + " -> " + section);
            }
            return section;
        }

        Log.w(TAG, "classTime格式错误：" + classTime + "，使用默认值：1-2");
        return "1-2"; // 默认1-2节
    }

    // 解析节次范围，例如："1-2" -> [1, 2]
    private int[] parseSectionRange(String sectionRange) {
        try {
            String[] parts = sectionRange.split("-");
            if (parts.length == 2) {
                int start = Integer.parseInt(parts[0]);
                int end = Integer.parseInt(parts[1]);

                // 确保范围合法
                if (start > 0 && end > 0 && start <= end && end <= MAX_SECTIONS) {
                    return new int[] { start, end };
                }
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "解析节次范围出错: " + sectionRange, e);
        }

        // 默认返回[1, 2]
        return new int[] { 1, 2 };
    }

    // 将星期几转换为数字，例如："周一" -> 1, "周二" -> 2, ...
    private int convertWeekdayToNumber(String weekday) {
        switch (weekday) {
            case "周一":
                return 1;
            case "周二":
                return 2;
            case "周三":
                return 3;
            case "周四":
                return 4;
            case "周五":
                return 5;
            case "周六":
                return 6;
            case "周日":
                return 7;
            default:
                return 1; // 默认周一
        }
    }

    // 更新课表显示
    public void updateTimetable(int week) {
        this.currentWeek = week;
        renderTimetable();
        // 通知其他监听器周数已变更
        notifyDataChanged(week);
    }

    // 渲染课表
    private void renderTimetable() {
        // 清空课表
        timetableGrid.removeAllViews();

        // 添加网格线
        addGridLines();

        // 过滤出当前周的课程并添加到课表中
        for (TimetableCourse course : allCourses) {
            if (course.getStartWeek() <= currentWeek && course.getEndWeek() >= currentWeek) {
                addCourseToTimetable(course);
            }
        }

        // 如果没有课程，显示提示
        if (allCourses.isEmpty()) {
            tvNoTimetable.setVisibility(View.VISIBLE);
        } else {
            tvNoTimetable.setVisibility(View.GONE);
        }
    }

    // 添加网格线
    private void addGridLines() {
        // 横向网格线（水平线）
        for (int i = 0; i <= MAX_SECTIONS; i++) {
            View lineView = new View(requireContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    columnWidth * DAYS_IN_WEEK, 1);
            params.setMargins(0, i * ROW_HEIGHT, 0, 0);
            lineView.setLayoutParams(params);
            lineView.setBackgroundColor(Color.parseColor("#EEEEEE"));
            timetableGrid.addView(lineView);
        }

        // 纵向网格线（垂直线）
        for (int i = 0; i <= DAYS_IN_WEEK; i++) {
            View lineView = new View(requireContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    1, ROW_HEIGHT * MAX_SECTIONS);
            params.setMargins(i * columnWidth, 0, 0, 0);
            lineView.setLayoutParams(params);
            lineView.setBackgroundColor(Color.parseColor("#EEEEEE"));
            timetableGrid.addView(lineView);
        }
    }

    // 添加课程到课表
    private void addCourseToTimetable(TimetableCourse course) {
        CardView courseCard = new CardView(requireContext());
        int startOffset = (course.getStartSection() - 1) * ROW_HEIGHT;
        int height = (course.getEndSection() - course.getStartSection() + 1) * ROW_HEIGHT - 2 * COURSE_MARGIN;
        int leftOffset = (course.getWeekday() - 1) * columnWidth + COURSE_MARGIN;
        int width = columnWidth - 2 * COURSE_MARGIN;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.setMargins(leftOffset, startOffset, 0, 0);
        courseCard.setLayoutParams(params);

        // 设置卡片样式
        courseCard.setRadius(8);
        courseCard.setCardElevation(4);

        // 处理名称可能为null的情况
        String courseName = course.getName();
        if (courseName == null) {
            courseName = "未命名课程";
            // 设置回去，避免后续再次出现null
            course.setName(courseName);
        }

        courseCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(),
                courseColors[Math.abs(courseName.hashCode()) % courseColors.length]));

        // 添加课程信息
        LinearLayout infoLayout = new LinearLayout(requireContext());
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        infoLayout.setPadding(8, 4, 8, 4);
        infoLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        // 课程名称
        TextView tvName = new TextView(requireContext());
        tvName.setText(courseName);
        tvName.setTextColor(Color.WHITE);
        tvName.setTextSize(12);
        tvName.setMaxLines(2);
        tvName.setEllipsize(android.text.TextUtils.TruncateAt.END);

        // 教室
        TextView tvRoom = new TextView(requireContext());
        String classroom = course.getClassroom();
        tvRoom.setText(classroom != null ? classroom : "未知教室");
        tvRoom.setTextColor(Color.WHITE);
        tvRoom.setTextSize(10);
        tvRoom.setMaxLines(1);

        infoLayout.addView(tvName);
        infoLayout.addView(tvRoom);
        courseCard.addView(infoLayout);

        // 点击打开详情
        courseCard.setOnClickListener(v -> openCourseDetail(course));

        // 长按显示菜单
        courseCard.setOnLongClickListener(v -> {
            showCourseOptionsDialog(course);
            return true;
        });

        timetableGrid.addView(courseCard);
    }

    // 打开课程详情页
    private void openCourseDetail(TimetableCourse course) {
        try {
            // 使用CourseDetailActivity提供的静态方法创建Intent
            Intent intent = CourseDetailActivity.getStartIntent(requireContext(), course);
            Log.d(TAG, "启动课程详情页，课程名称: " + course.getName() + ", 教师: " + course.getTeacher());
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "打开课程详情页失败", e);
            Toast.makeText(requireContext(), "无法打开课程详情: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // 添加新课程
    public void addCourse(TimetableCourse newCourse) {
        // 验证课程信息完整性
        if (newCourse.getName().isEmpty() || newCourse.getClassroom().isEmpty() ||
                newCourse.getTeacher().isEmpty()) {
            Toast.makeText(requireContext(), "课程信息不完整", Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查是否与已有课程冲突
        for (TimetableCourse course : allCourses) {
            if (course.getWeekday() == newCourse.getWeekday() &&
                    course.getStartSection() <= newCourse.getEndSection() &&
                    course.getEndSection() >= newCourse.getStartSection()) {
                Toast.makeText(requireContext(), "与已有课程时间冲突", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // 添加到服务器
        String classTime = getWeekdayString(newCourse.getWeekday()) + " " +
                newCourse.getStartSection() + "-" + newCourse.getEndSection() + "节";

        dataSyncManager.addCourseSchedule(
                newCourse.getName(),
                newCourse.getTeacher(),
                classTime,
                newCourse.getClassroom(),
                new DataSyncManager.SyncCallback<CourseSchedule>() {
                    @Override
                    public void onSuccess(CourseSchedule result) {
                        // 设置数据库ID
                        newCourse.setDatabaseId(result.getId());
                        // 添加到本地列表
                        allCourses.add(newCourse);
                        // 更新UI
                        renderTimetable();
                        Toast.makeText(requireContext(), R.string.add_success, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(requireContext(), "添加失败: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 获取星期几的字符串表示
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
                return "周一";
        }
    }

    // 显示添加课程对话框
    private void showAddCourseDialog() {
        // 创建对话框
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_course);
        dialog.setCancelable(true);

        // 获取对话框中的控件
        TextInputEditText etCourseName = dialog.findViewById(R.id.etCourseName);
        TextInputEditText etTeacherName = dialog.findViewById(R.id.etTeacherName);
        TextInputEditText etClassroom = dialog.findViewById(R.id.etClassroom);
        Spinner spWeekday = dialog.findViewById(R.id.spWeekday);
        Spinner spStartSection = dialog.findViewById(R.id.spStartSection);
        Spinner spEndSection = dialog.findViewById(R.id.spEndSection);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnSave = dialog.findViewById(R.id.btnSave);

        // 设置星期几下拉框
        String[] weekdays = { "周一", "周二", "周三", "周四", "周五", "周六", "周日" };
        ArrayAdapter<String> weekdayAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, weekdays);
        weekdayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWeekday.setAdapter(weekdayAdapter);

        // 设置节次下拉框
        String[] sections = new String[MAX_SECTIONS];
        for (int i = 0; i < MAX_SECTIONS; i++) {
            sections[i] = "第" + (i + 1) + "节 " + sectionTimeMap[i];
        }

        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, sections);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStartSection.setAdapter(sectionAdapter);
        spEndSection.setAdapter(sectionAdapter);

        // 设置开始节次变化监听器，确保结束节次不小于开始节次
        spStartSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spEndSection.getSelectedItemPosition() < position) {
                    spEndSection.setSelection(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 设置按钮点击事件
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            // 获取输入内容
            String courseName = etCourseName.getText().toString().trim();
            String teacherName = etTeacherName.getText().toString().trim();
            String classroom = etClassroom.getText().toString().trim();
            int weekday = spWeekday.getSelectedItemPosition() + 1;
            int startSection = spStartSection.getSelectedItemPosition() + 1;
            int endSection = spEndSection.getSelectedItemPosition() + 1;

            // 验证输入
            if (courseName.isEmpty() || teacherName.isEmpty() || classroom.isEmpty()) {
                Toast.makeText(requireContext(), R.string.cannot_be_empty, Toast.LENGTH_SHORT).show();
                return;
            }

            // 创建新课程
            TimetableCourse newCourse = new TimetableCourse(
                    courseName,
                    classroom,
                    teacherName,
                    startSection,
                    endSection,
                    weekday,
                    1, // 开始周
                    20 // 结束周
            );

            // 添加课程
            addCourse(newCourse);

            // 关闭对话框
            dialog.dismiss();
        });

        // 显示对话框
        dialog.show();
    }

    // 获取节次的时间字符串
    private String getSectionTimeString(int startSection, int endSection) {
        if (startSection > 0 && startSection <= MAX_SECTIONS &&
                endSection > 0 && endSection <= MAX_SECTIONS) {
            return "第" + startSection + "-" + endSection + "节 " +
                    sectionTimeMap[startSection - 1].split("-")[0] + "-" +
                    sectionTimeMap[endSection - 1].split("-")[1];
        }
        return "";
    }

    // 显示课程选项对话框
    private void showCourseOptionsDialog(TimetableCourse course) {
        String[] options = { "查看详情", "编辑课程", "删除课程" };

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(course.getName())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            openCourseDetail(course);
                            break;
                        case 1:
                            // 编辑课程
                            // 这里可以添加编辑课程的对话框
                            break;
                        case 2:
                            confirmDeleteCourse(course);
                            break;
                    }
                })
                .show();
    }

    // 确认删除课程
    private void confirmDeleteCourse(TimetableCourse course) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirm_delete)
                .setMessage("确定要删除 " + course.getName() + " 课程吗？")
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    // 删除课程
                    deleteCourse(course);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    // 删除课程
    private void deleteCourse(TimetableCourse course) {
        dataSyncManager.deleteCourseSchedule(course.getDatabaseId(), new DataSyncManager.SyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // 从本地列表中移除
                allCourses.remove(course);
                // 更新UI
                renderTimetable();
                Toast.makeText(requireContext(), R.string.delete_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable error) {
                Toast.makeText(requireContext(), "删除失败: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}