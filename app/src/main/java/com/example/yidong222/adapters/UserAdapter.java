package com.example.yidong222.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidong222.R;
import com.example.yidong222.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserItemClickListener listener;

    public interface OnUserItemClickListener {
        void onEditClick(User user, int position);

        void onDeleteClick(User user, int position);

        void onItemClick(User user, int position);
    }

    public UserAdapter() {
        this.userList = new ArrayList<>();
    }

    public void setOnUserItemClickListener(OnUserItemClickListener listener) {
        this.listener = listener;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    public void addUser(User user) {
        userList.add(user);
        notifyItemInserted(userList.size() - 1);
    }

    public void updateUser(User user, int position) {
        if (position >= 0 && position < userList.size()) {
            userList.set(position, user);
            notifyItemChanged(position);
        }
    }

    public void removeUser(int position) {
        if (position >= 0 && position < userList.size()) {
            userList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public User getUser(int position) {
        if (position >= 0 && position < userList.size()) {
            return userList.get(position);
        }
        return null;
    }

    public List<User> getUserList() {
        return userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user, position);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView userIdText;
        private final TextView usernameText;
        private final TextView userTypeText;
        private final TextView createdTimeText;
        private final Button editButton;
        private final Button deleteButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userIdText = itemView.findViewById(R.id.user_id_text);
            usernameText = itemView.findViewById(R.id.username_text);
            userTypeText = itemView.findViewById(R.id.user_type_text);
            createdTimeText = itemView.findViewById(R.id.created_time_text);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }

        public void bind(User user, int position) {
            userIdText.setText("ID: " + user.getId());
            usernameText.setText(user.getUsername());

            if (user.isAdmin()) {
                userTypeText.setText("管理员");
                userTypeText.setBackgroundResource(android.R.color.holo_green_light);
            } else {
                userTypeText.setText("普通用户");
                userTypeText.setBackgroundResource(android.R.color.holo_blue_light);
            }

            String createdTime = user.getCreatedAt() != null ? user.getCreatedAt() : "未知";
            createdTimeText.setText("创建时间：" + createdTime);

            // 设置点击事件
            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(user, getAdapterPosition());
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(user, getAdapterPosition());
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(user, getAdapterPosition());
                }
            });
        }
    }
}