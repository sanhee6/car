package com.example.yidong222.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidong222.R;
import com.example.yidong222.models.CourseSchedule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TodayCourseAdapter extends RecyclerView.Adapter<TodayCourseAdapter.ViewHolder> {
    private List<CourseSchedule> courseList;

    public TodayCourseAdapter() {
        this.courseList = new ArrayList<>();
    }

    public void setCourseList(List<CourseSchedule> courseList) {
        // 去重处理
        this.courseList = removeDuplicateCourses(courseList);
        notifyDataSetChanged();
    }

    /**
     * 去除重复的课程
     * 
     * @param courses 原始课程列表
     * @return 去重后的课程列表
     */
    private List<CourseSchedule> removeDuplicateCourses(List<CourseSchedule> courses) {
        List<CourseSchedule> uniqueCourses = new ArrayList<>();
        Set<String> uniqueCourseNames = new HashSet<>();

        for (CourseSchedule course : courses) {
            // 使用课程名称作为唯一标识
            if (!uniqueCourseNames.contains(course.getCourseName())) {
                uniqueCourseNames.add(course.getCourseName());
                uniqueCourses.add(course);
            }
        }

        return uniqueCourses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_today_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseSchedule course = courseList.get(position);
        holder.tvCourseName.setText(course.getCourseName());
        holder.tvClassroom.setText(course.getClassroom());
        holder.tvClassTime.setText(course.getClassTime());
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName;
        TextView tvClassroom;
        TextView tvClassTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tv_course_name);
            tvClassroom = itemView.findViewById(R.id.tv_classroom);
            tvClassTime = itemView.findViewById(R.id.tv_class_time);
        }
    }
}