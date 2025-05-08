package com.example.yidong222.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidong222.R;
import com.example.yidong222.models.Semester;

import java.util.ArrayList;
import java.util.List;

public class SemesterAdapter extends RecyclerView.Adapter<SemesterAdapter.SemesterViewHolder> {

    private List<Semester> semesters = new ArrayList<>();
    private OnSemesterClickListener listener;

    public interface OnSemesterClickListener {
        void onSemesterClick(Semester semester);
    }

    public void setOnSemesterClickListener(OnSemesterClickListener listener) {
        this.listener = listener;
    }

    public void setSemesters(List<Semester> semesters) {
        this.semesters = semesters;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SemesterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_semester, parent, false);
        return new SemesterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SemesterViewHolder holder, int position) {
        Semester semester = semesters.get(position);
        holder.bind(semester);

        holder.itemView.setOnClickListener(v -> {
            // 更新选中状态
            for (Semester s : semesters) {
                s.setSelected(false);
            }
            semester.setSelected(true);
            notifyDataSetChanged();

            if (listener != null) {
                listener.onSemesterClick(semester);
            }
        });
    }

    @Override
    public int getItemCount() {
        return semesters.size();
    }

    static class SemesterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSemester;

        public SemesterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSemester = itemView.findViewById(R.id.tvSemester);
        }

        void bind(Semester semester) {
            tvSemester.setText(semester.getName());
            tvSemester.setSelected(semester.isSelected());
        }
    }
}