package com.example.yidong222.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.yidong222.fragments.CourseListFragment;
import com.example.yidong222.fragments.TimetableFragment;

public class ScheduleViewPagerAdapter extends FragmentStateAdapter {

    private final TimetableFragment timetableFragment;
    private final CourseListFragment courseListFragment;

    public ScheduleViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
        // 预初始化Fragment实例，避免每次切换都创建新实例
        timetableFragment = new TimetableFragment();
        courseListFragment = new CourseListFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return timetableFragment;
        } else {
            return courseListFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2; // 两个页面：课表视图和课程列表
    }
}