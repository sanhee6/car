package com.example.yidong222.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.yidong222.LoginActivity;
import com.example.yidong222.R;
import com.example.yidong222.models.User;
import com.example.yidong222.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private SessionManager sessionManager;
    private TextView usernameText;
    private TextView userTypeText;
    private LinearLayout logoutItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 初始化会话管理器
        sessionManager = new SessionManager(requireContext());

        // 初始化视图
        usernameText = view.findViewById(R.id.username_text);
        userTypeText = view.findViewById(R.id.user_type_text);
        logoutItem = view.findViewById(R.id.logout_item);

        // 设置用户信息
        setupUserInfo();

        // 设置退出登录点击事件
        setupLogoutAction();

        return view;
    }

    /**
     * 设置用户信息
     */
    private void setupUserInfo() {
        User user = sessionManager.getUser();

        if (user != null) {
            usernameText.setText(user.getUsername());

            if (user.isAdmin()) {
                userTypeText.setText("管理员");
            } else {
                userTypeText.setText("普通用户");
            }
        }
    }

    /**
     * 设置退出登录按钮点击事件
     */
    private void setupLogoutAction() {
        logoutItem.setOnClickListener(v -> showLogoutConfirmDialog());
    }

    /**
     * 显示退出登录确认对话框
     */
    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 执行退出登录操作
     */
    private void performLogout() {
        // 清除用户会话信息
        sessionManager.logout();

        // 显示退出成功提示
        Toast.makeText(requireContext(), "已退出登录", Toast.LENGTH_SHORT).show();

        // 跳转到登录页面
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        // 清除返回栈中的所有Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}