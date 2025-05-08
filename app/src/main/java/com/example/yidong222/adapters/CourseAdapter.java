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
import com.example.yidong222.models.CourseDto;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<CourseDto> courseList;
    private CourseItemClickListener listener;
    private boolean isSimpleView = false;

    // 添加无参数构造函数，兼容HomeFragment
    public CourseAdapter() {
        this.courseList = new ArrayList<>();
        this.isSimpleView = true;
    }

    public CourseAdapter(List<CourseDto> courseList, CourseItemClickListener listener) {
        this.courseList = courseList;
        this.listener = listener;
    }

    // 添加设置课程方法，兼容HomeFragment
    public void setCourses(List<Course> courses) {
        this.courseList = new ArrayList<>();
        for (Course course : courses) {
            // 将简单Course对象转换为CourseDto
            CourseDto dto = new CourseDto();
            dto.setName(course.getName());
            dto.setClassroom(course.getLocation());
            // 简单设置时间
            dto.setWeekday(1); // 假设是周一
            dto.setStartSection(1);
            dto.setEndSection(2);
            this.courseList.add(dto);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        CourseDto course = courseList.get(position);

        holder.tvCourseName.setText(course.getName());

        if (!isSimpleView) {
            holder.tvTeacher.setText("教师: " + course.getTeacher());

            // 构建时间和地点信息
            String weekday = getWeekdayString(course.getWeekday());
            String time = weekday + " 第" + course.getStartSection() + "-" + course.getEndSection() + "节";
            holder.tvTime.setText(time);

            holder.tvLocation.setText("地点: " + course.getClassroom());

            // 设置点击事件
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCourseClick(position, course);
                }
            });

            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCourseEditClick(position, course);
                }
            });

            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCourseDeleteClick(position, course);
                }
            });
        } else {
            // 简化版视图，用于首页展示
            holder.tvTeacher.setVisibility(View.GONE);
            holder.tvTime.setText(course.getClassroom()); // 使用教室作为时间显示
            holder.tvLocation.setVisibility(View.GONE);

            // 隐藏编辑和删除按钮
            if (holder.btnEdit != null)
                holder.btnEdit.setVisibility(View.GONE);
            if (holder.btnDelete != null)
                holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName;
        TextView tvTeacher;
        TextView tvTime;
        TextView tvLocation;
        ImageButton btnEdit;
        ImageButton btnDelete;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvTeacher = itemView.findViewById(R.id.tvTeacher);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
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

    // 定义点击事件接口
    public interface CourseItemClickListener {
        void onCourseClick(int position, CourseDto course);

        void onCourseEditClick(int position, CourseDto course);

        void onCourseDeleteClick(int position, CourseDto course);
    }
}