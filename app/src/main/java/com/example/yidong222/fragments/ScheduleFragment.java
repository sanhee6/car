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
import androidx.viewpager2.widget.ViewPager2;

import com.example.yidong222.CourseScheduleActivity;
import com.example.yidong222.R;
import com.example.yidong222.adapters.ScheduleViewPagerAdapter;
import com.example.yidong222.adapters.SemesterAdapter;
import com.example.yidong222.adapters.WeekAdapter;
import com.example.yidong222.models.Semester;
import com.example.yidong222.models.Week;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    private TextView tvScheduleTitle;
    private RecyclerView rvWeeks;
    private RecyclerView rvSemester;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private MaterialButton btnCourseSchedule;

    private WeekAdapter weekAdapter;
    private SemesterAdapter semesterAdapter;

    private int currentWeek = 1; // 默认当前为第1周
    private int totalWeeks = 20; // 假设一个学期有20周

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupWeekRecyclerView();
        setupSemesterRecyclerView();
        setupViewPager();
        loadWeeks();
        loadSemesters();
        setupListeners();
    }

    private void initViews(View view) {
        tvScheduleTitle = view.findViewById(R.id.tvScheduleTitle);
        rvWeeks = view.findViewById(R.id.rvWeeks);
        rvSemester = view.findViewById(R.id.rvSemester);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        btnCourseSchedule = view.findViewById(R.id.btnCourseSchedule);

        // 设置标题为当前周
        tvScheduleTitle.setText(getString(R.string.week_format, currentWeek));
    }

    private void setupListeners() {
        // 设置课程表按钮点击监听器
        btnCourseSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CourseScheduleActivity.class);
            startActivity(intent);
        });
    }

    private void setupWeekRecyclerView() {
        weekAdapter = new WeekAdapter();
        rvWeeks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvWeeks.setAdapter(weekAdapter);

        weekAdapter.setOnWeekClickListener((week, position) -> {
            weekAdapter.updateWeekSelection(position);
            currentWeek = week.getWeekNumber();
            tvScheduleTitle.setText(getString(R.string.week_format, currentWeek));
            // 更新课表数据
            updateTimetableData();
        });
    }

    private void setupSemesterRecyclerView() {
        semesterAdapter = new SemesterAdapter();
        rvSemester.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvSemester.setAdapter(semesterAdapter);

        semesterAdapter.setOnSemesterClickListener(semester -> {
            // 处理学期选择
            // 这里可以重新加载该学期的课表数据
            loadWeeks(); // 更新周数
            updateTimetableData();
        });
    }

    private void setupViewPager() {
        ScheduleViewPagerAdapter pagerAdapter = new ScheduleViewPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // 将TabLayout与ViewPager2关联
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(R.string.timetable_view);
            } else {
                tab.setText(R.string.course_list);
            }
        }).attach();
    }

    private void loadWeeks() {
        List<Week> weeks = new ArrayList<>();

        // 生成周次数据，假设当前为第3周
        for (int i = 1; i <= totalWeeks; i++) {
            weeks.add(new Week(i, i == currentWeek));
        }

        weekAdapter.setWeeks(weeks);

        // 滚动到当前周的位置
        rvWeeks.post(() -> {
            int position = currentWeek - 1;
            if (position >= 0 && position < totalWeeks) {
                rvWeeks.smoothScrollToPosition(position);
            }
        });
    }

    private void loadSemesters() {
        // 模拟获取学期数据
        // 实际应用中应该从API获取数据
        List<Semester> semesters = new ArrayList<>();

        semesters.add(new Semester("2022-2023学年 第2学期", true));
        semesters.add(new Semester("2022-2023学年 第1学期", false));
        semesters.add(new Semester("2021-2022学年 第2学期", false));
        semesters.add(new Semester("2021-2022学年 第1学期", false));
        semesters.add(new Semester("2020-2021学年 第2学期", false));

        semesterAdapter.setSemesters(semesters);
    }

    private void updateTimetableData() {
        // 通知课程表页面和课程列表页面更新数据
        // 当切换周次时，只需更新TimetableFragment，CourseListFragment会通过监听器自动更新
        Fragment timetableFragment = getChildFragmentManager().getFragments().get(0);
        if (timetableFragment instanceof TimetableFragment) {
            ((TimetableFragment) timetableFragment).updateTimetable(currentWeek);
        }

        // 不需要直接更新CourseListFragment，它会通过监听器接收通知
        // 但如果CourseListFragment未实现监听器，作为备选方案也可以直接更新
        if (getChildFragmentManager().getFragments().size() > 1) {
            Fragment courseListFragment = getChildFragmentManager().getFragments().get(1);
            if (courseListFragment instanceof CourseListFragment) {
                ((CourseListFragment) courseListFragment).updateCourses(currentWeek);
            }
        }
    }
}