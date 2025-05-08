package com.example.yidong222.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidong222.R;
import com.example.yidong222.models.Setting;

import java.util.ArrayList;
import java.util.List;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.SettingViewHolder> {

    private List<Setting> settings = new ArrayList<>();
    private OnSettingClickListener listener;

    public interface OnSettingClickListener {
        void onSettingClick(Setting setting);
    }

    public void setOnSettingClickListener(OnSettingClickListener listener) {
        this.listener = listener;
    }

    public void setSettings(List<Setting> settings) {
        this.settings = settings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting, parent, false);
        return new SettingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingViewHolder holder, int position) {
        Setting setting = settings.get(position);
        holder.bind(setting);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSettingClick(setting);
            }
        });
    }

    @Override
    public int getItemCount() {
        return settings.size();
    }

    static class SettingViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivSettingIcon;
        private TextView tvSettingName;

        public SettingViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSettingIcon = itemView.findViewById(R.id.ivSettingIcon);
            tvSettingName = itemView.findViewById(R.id.tvSettingName);
        }

        void bind(Setting setting) {
            ivSettingIcon.setImageResource(setting.getIconResId());
            tvSettingName.setText(setting.getName());
        }
    }
}