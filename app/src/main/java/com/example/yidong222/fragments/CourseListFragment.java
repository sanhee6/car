package com.example.yidong222.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidong222.CourseDetailActivity;
import com.example.yidong222.R;
import com.example.yidong222.adapters.CourseDetailAdapter;
import com.example.yidong222.models.CourseDetail;
import com.example.yidong222.models.TimetableCourse;

import java.util.ArrayList;
import java.util.List;

public class CourseListFragment extends Fragment implements TimetableFragment.OnCourseDataChangeListener {

    private RecyclerView rvCourses;
    private TextView tvNoCourses;
    private CourseDetailAdapter courseAdapter;
    private int currentWeek = 2; // 与TimetableFragment保持一致

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        updateCourseList();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 注册数据变更监听器
        TimetableFragment.addOnCourseDataChangeListener(this);
        // 刷新数据以防在其他地方更改
        updateCourseList();
    }

    @Override
    public void onPause() {
        super.onPause();
        // 取消注册监听器
        TimetableFragment.removeOnCourseDataChangeListener(this);
    }

    @Override
    public void onCourseDataChanged(int week) {
        // 当课程数据变化时更新课程列表
        currentWeek = week;
        updateCourseList();
    }

    private void initViews(View view) {
        rvCourses = view.findViewById(R.id.rvCourses);
        tvNoCourses = view.findViewById(R.id.tvNoCourses);
    }

    private void setupRecyclerView() {
        courseAdapter = new CourseDetailAdapter();
        rvCourses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCourses.setAdapter(courseAdapter);

        // 设置点击事件
        courseAdapter.setOnCourseClickListener(course -> {
            Intent intent = CourseDetailActivity.getStartIntent(
                    getContext(),
                    course.getName(),
                    course.getTeacher(),
                    course.getClassroom(),
                    course.getTime(),
                    course.getWeeks());
            startActivity(intent);
        });
    }

    // 外部调用，更新课程列表
    public void updateCourses(int week) {
        currentWeek = week;
        updateCourseList();
    }

    private void updateCourseList() {
        // 从TimetableFragment获取共享的课程数据
        List<TimetableCourse> allCourses = TimetableFragment.getAllCourses();

        // 筛选当前周的课程
        List<CourseDetail> currentWeekCourses = new ArrayList<>();
        for (TimetableCourse course : allCourses) {
            if (course.getStartWeek() <= currentWeek && course.getEndWeek() >= currentWeek) {
                // 转换为CourseDetail类型
                String time = getTimeString(course.getWeekday(), course.getStartSection(), course.getEndSection());
                String weeks = "第" + course.getStartWeek() + "-" + course.getEndWeek() + "周";
                currentWeekCourses.add(new CourseDetail(
                        course.getName(),
                        course.getTeacher(),
                        course.getClassroom(),
                        time,
                        weeks));
            }
        }

        // 显示或隐藏无课程提示
        if (currentWeekCourses.isEmpty()) {
            tvNoCourses.setVisibility(View.VISIBLE);
            rvCourses.setVisibility(View.GONE);
        } else {
            tvNoCourses.setVisibility(View.GONE);
            rvCourses.setVisibility(View.VISIBLE);
            courseAdapter.setCourses(currentWeekCourses);
        }
    }

    private String getTimeString(int weekday, int startSection, int endSection) {
        String[] weekdays = { "一", "二", "三", "四", "五", "六", "日" };
        String[] timeSlots = { "8:00-9:40", "10:00-11:40", "14:00-15:40", "16:00-17:40", "19:00-20:40", "20:50-22:30" };

        int timeIndex = (startSection - 1) / 2;
        String timeStr = timeSlots[timeIndex];

        return "周" + weekdays[weekday - 1] + " " + timeStr;
    }
}