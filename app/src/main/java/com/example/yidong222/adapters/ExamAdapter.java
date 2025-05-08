package com.example.yidong222.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidong222.R;
import com.example.yidong222.models.Exam;

import java.util.ArrayList;
import java.util.List;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ExamViewHolder> {

    private List<Exam> examList;
    private ExamItemClickListener listener;

    public ExamAdapter(List<Exam> examList, ExamItemClickListener listener) {
        this.examList = examList;
        this.listener = listener;
    }

    public ExamAdapter() {
        this.examList = new ArrayList<>();
    }

    public void setExams(List<Exam> exams) {
        this.examList = exams;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam_management, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        Exam exam = examList.get(position);
        holder.tvCourseName.setText(exam.getCourseName());
        holder.tvExamName.setText(exam.getExamName());
        holder.tvDateTime.setText(exam.getDate() + " " + exam.getTime());
        holder.tvLocation.setText(exam.getLocation());

        holder.btnStatus.setText(exam.getStatus());
        // 设置按钮颜色
        if ("已考".equals(exam.getStatus())) {
            holder.btnStatus.setBackgroundResource(R.drawable.bg_status_completed);
        } else {
            holder.btnStatus.setBackgroundResource(R.drawable.bg_status_pending);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExamClick(holder.getAdapterPosition(), exam);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExamEditClick(holder.getAdapterPosition(), exam);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExamDeleteClick(holder.getAdapterPosition());
            }
        });

        holder.btnStatus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExamStatusToggle(holder.getAdapterPosition(), exam);
            }
        });
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    public static class ExamViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName, tvExamName, tvDateTime, tvLocation;
        ImageButton btnEdit, btnDelete;
        Button btnStatus;

        public ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvExamCourseName);
            tvExamName = itemView.findViewById(R.id.tvExamName);
            tvDateTime = itemView.findViewById(R.id.tvExamDateTime);
            tvLocation = itemView.findViewById(R.id.tvExamLocation);
            btnEdit = itemView.findViewById(R.id.btnEditExam);
            btnDelete = itemView.findViewById(R.id.btnDeleteExam);
            btnStatus = itemView.findViewById(R.id.btnExamStatus);
        }
    }

    public interface ExamItemClickListener {
        void onExamClick(int position, Exam exam);

        void onExamEditClick(int position, Exam exam);

        void onExamDeleteClick(int position);

        void onExamStatusToggle(int position, Exam exam);
    }
}