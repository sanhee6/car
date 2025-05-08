package com.example.yidong222.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidong222.R;

import java.util.List;
import java.util.Map;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradeViewHolder> {

    private final Context context;
    private final List<Map<String, Object>> gradeList;

    public GradeAdapter(Context context, List<Map<String, Object>> gradeList) {
        this.context = context;
        this.gradeList = gradeList;
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade_management, parent, false);
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        Map<String, Object> grade = gradeList.get(position);

        // 使用grade的Map数据
        holder.tvCourseName.setText(getStringValue(grade, "courseName", "未知课程"));
        holder.tvCourseType.setText(getStringValue(grade, "courseType", "未知类型"));

        double score = getDoubleValue(grade, "score", 0.0);
        holder.tvScore.setText(String.format("%.1f", score));

        double credit = getDoubleValue(grade, "credit", 0.0);
        holder.tvCredit.setText(String.format("%.1f学分", credit));

        holder.tvGpa.setText("绩点: " + getStringValue(grade, "gpa", "0.0"));
        holder.tvSemester.setText(getStringValue(grade, "semester", "未知学期"));

        // 根据分数设置颜色
        int scoreColor;
        if (score >= 90) {
            scoreColor = Color.parseColor("#4CAF50"); // 绿色
        } else if (score >= 80) {
            scoreColor = Color.parseColor("#2196F3"); // 蓝色
        } else if (score >= 70) {
            scoreColor = Color.parseColor("#FF9800"); // 橙色
        } else if (score >= 60) {
            scoreColor = Color.parseColor("#FFC107"); // 黄色
        } else {
            scoreColor = Color.parseColor("#F44336"); // 红色
        }
        holder.tvScore.setTextColor(scoreColor);
    }

    // 安全获取String值
    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        if (map.containsKey(key) && map.get(key) != null) {
            return map.get(key).toString();
        }
        return defaultValue;
    }

    // 安全获取Double值
    private double getDoubleValue(Map<String, Object> map, String key, double defaultValue) {
        if (map.containsKey(key) && map.get(key) != null) {
            try {
                if (map.get(key) instanceof Double) {
                    return (Double) map.get(key);
                } else if (map.get(key) instanceof Integer) {
                    return ((Integer) map.get(key)).doubleValue();
                } else {
                    return Double.parseDouble(map.get(key).toString());
                }
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    @Override
    public int getItemCount() {
        return gradeList.size();
    }

    public static class GradeViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName, tvCourseType, tvScore, tvCredit, tvGpa, tvSemester;

        public GradeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvGradeCourseName);
            tvCourseType = itemView.findViewById(R.id.tvGradeCourseType);
            tvScore = itemView.findViewById(R.id.tvGradeScore);
            tvCredit = itemView.findViewById(R.id.tvGradeCredit);
            tvGpa = itemView.findViewById(R.id.tvGradeGpa);
            tvSemester = itemView.findViewById(R.id.tvGradeSemester);
        }
    }
}