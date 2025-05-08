package com.example.yidong222.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidong222.R;
import com.example.yidong222.models.Assignment;

import java.util.ArrayList;
import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private List<Assignment> assignmentList;
    private AssignmentItemClickListener listener;

    public AssignmentAdapter(List<Assignment> assignmentList, AssignmentItemClickListener listener) {
        this.assignmentList = assignmentList;
        this.listener = listener;
    }

    public AssignmentAdapter() {
        this.assignmentList = new ArrayList<>();
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignmentList = assignments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignment_management, parent,
                false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);
        holder.tvTitle.setText(assignment.getTitle());
        holder.tvCourse.setText(assignment.getCourseName());
        holder.tvDeadline.setText("截止日期: " + assignment.getDeadline());
        holder.cbCompleted.setChecked(assignment.isCompleted());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAssignmentClick(holder.getAdapterPosition(), assignment);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAssignmentEditClick(holder.getAdapterPosition(), assignment);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAssignmentDeleteClick(holder.getAdapterPosition());
            }
        });

        holder.cbCompleted.setOnClickListener(v -> {
            boolean isChecked = holder.cbCompleted.isChecked();
            if (listener != null) {
                listener.onAssignmentStatusChange(holder.getAdapterPosition(), assignment, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCourse, tvDeadline;
        ImageButton btnEdit, btnDelete;
        CheckBox cbCompleted;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvAssignmentTitle);
            tvCourse = itemView.findViewById(R.id.tvAssignmentCourse);
            tvDeadline = itemView.findViewById(R.id.tvAssignmentDeadline);
            btnEdit = itemView.findViewById(R.id.btnEditAssignment);
            btnDelete = itemView.findViewById(R.id.btnDeleteAssignment);
            cbCompleted = itemView.findViewById(R.id.cbAssignmentCompleted);
        }
    }

    public interface AssignmentItemClickListener {
        void onAssignmentClick(int position, Assignment assignment);

        void onAssignmentEditClick(int position, Assignment assignment);

        void onAssignmentDeleteClick(int position);

        void onAssignmentStatusChange(int position, Assignment assignment, boolean isCompleted);
    }
}