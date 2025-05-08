package com.example.yidong222.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidong222.R;
import com.example.yidong222.models.CourseDetail;

import java.util.ArrayList;
import java.util.List;

public class CourseDetailAdapter extends RecyclerView.Adapter<CourseDetailAdapter.CourseDetailViewHolder> {

    private List<CourseDetail> courses = new ArrayList<>();
    private OnCourseClickListener listener;

    public interface OnCourseClickListener {
        void onCourseClick(CourseDetail course);
    }

    public void setOnCourseClickListener(OnCourseClickListener listener) {
        this.listener = listener;
    }

    public void setCourses(List<CourseDetail> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourseDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_detail, parent, false);
        return new CourseDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseDetailViewHolder holder, int position) {
        CourseDetail course = courses.get(position);

        holder.tvCourseName.setText(course.getName());
        holder.tvCourseTime.setText(course.getTime());
        holder.tvCourseClassroom.setText(course.getClassroom());
        holder.tvCourseTeacher.setText(course.getTeacher());
        holder.tvCourseWeeks.setText(course.getWeeks());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCourseClick(course);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class CourseDetailViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName;
        TextView tvCourseTime;
        TextView tvCourseClassroom;
        TextView tvCourseTeacher;
        TextView tvCourseWeeks;

        CourseDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvCourseTime = itemView.findViewById(R.id.tvCourseTime);
            tvCourseClassroom = itemView.findViewById(R.id.tvCourseClassroom);
            tvCourseTeacher = itemView.findViewById(R.id.tvCourseTeacher);
            tvCourseWeeks = itemView.findViewById(R.id.tvCourseWeeks);
        }
    }
}