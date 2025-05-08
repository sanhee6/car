package com.example.yidong222.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidong222.R;
import com.example.yidong222.models.Course;

import java.util.List;

public class CourseManagementAdapter extends RecyclerView.Adapter<CourseManagementAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private CourseItemClickListener listener;

    public CourseManagementAdapter(List<Course> courseList, CourseItemClickListener listener) {
        this.courseList = courseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_management, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.tvCourseName.setText(course.getName());
        holder.tvCourseLocation.setText(course.getLocation());
        holder.tvCourseTime.setText(course.getTime());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCourseClick(holder.getAdapterPosition(), course);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCourseEditClick(holder.getAdapterPosition(), course);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCourseDeleteClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName, tvCourseLocation, tvCourseTime;
        ImageButton btnEdit, btnDelete;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvManagementCourseName);
            tvCourseLocation = itemView.findViewById(R.id.tvManagementCourseLocation);
            tvCourseTime = itemView.findViewById(R.id.tvManagementCourseTime);
            btnEdit = itemView.findViewById(R.id.btnEditCourse);
            btnDelete = itemView.findViewById(R.id.btnDeleteCourse);
        }
    }

    public interface CourseItemClickListener {
        void onCourseClick(int position, Course course);

        void onCourseEditClick(int position, Course course);

        void onCourseDeleteClick(int position);
    }
}