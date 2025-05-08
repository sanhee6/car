package com.example.yidong222.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidong222.R;
import com.example.yidong222.models.Week;

import java.util.ArrayList;
import java.util.List;

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.WeekViewHolder> {

    private List<Week> weeks = new ArrayList<>();
    private OnWeekClickListener listener;

    public interface OnWeekClickListener {
        void onWeekClick(Week week, int position);
    }

    public void setOnWeekClickListener(OnWeekClickListener listener) {
        this.listener = listener;
    }

    public void setWeeks(List<Week> weeks) {
        this.weeks = weeks;
        notifyDataSetChanged();
    }

    public void updateWeekSelection(int position) {
        // 先将所有项设为未选中
        for (int i = 0; i < weeks.size(); i++) {
            weeks.get(i).setSelected(i == position);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WeekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_week, parent, false);
        return new WeekViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekViewHolder holder, int position) {
        Week week = weeks.get(position);
        Context context = holder.itemView.getContext();

        holder.tvWeek.setText(context.getString(R.string.week_format, week.getWeekNumber()));
        holder.tvWeek.setSelected(week.isSelected());

        // 选中项文字颜色为白色，未选中为深色
        holder.tvWeek.setTextColor(
                week.isSelected() ? context.getColor(R.color.white) : context.getColor(R.color.text_dark));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onWeekClick(week, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return weeks.size();
    }

    static class WeekViewHolder extends RecyclerView.ViewHolder {
        TextView tvWeek;

        WeekViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWeek = itemView.findViewById(R.id.tvWeek);
        }
    }
}