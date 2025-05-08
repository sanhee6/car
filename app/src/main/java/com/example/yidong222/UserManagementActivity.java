package com.example.yidong222;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.yidong222.adapters.UserAdapter;
import com.example.yidong222.api.ApiClient;
import com.example.yidong222.models.ApiResponse;
import com.example.yidong222.models.ApiResponseList;
import com.example.yidong222.models.User;
import com.example.yidong222.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManagementActivity extends AppCompatActivity implements UserAdapter.OnUserItemClickListener {
    private static final String TAG = "UserManagementActivity";

    private Button backButton;
    private RecyclerView userRecyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar loadingProgress;
    private TextView emptyView;
    private FloatingActionButton addUserButton;

    private UserAdapter userAdapter;
    private SessionManager sessionManager;

    // 分页相关
    private int currentPage = 1;
    private int pageLimit = 20;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        // 初始化会话管理器
        sessionManager = new SessionManager(this);

        // 检查是否具有管理员权限
        if (!sessionManager.isAdmin() || !sessionManager.isLoggedIn()) {
            Toast.makeText(this, "您没有管理员权限!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // 初始化视图
        backButton = findViewById(R.id.back_button);
        userRecyclerView = findViewById(R.id.user_recycler_view);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        loadingProgress = findViewById(R.id.loading_progress);
        emptyView = findViewById(R.id.empty_view);
        addUserButton = findViewById(R.id.add_user_button);

        // 设置RecyclerView
        userAdapter = new UserAdapter();
        userAdapter.setOnUserItemClickListener(this);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.setAdapter(userAdapter);

        // 设置下拉刷新
        swipeRefresh.setOnRefreshListener(() -> {
            refreshUserList();
        });

        // 设置返回按钮
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // 设置添加用户按钮
        addUserButton.setOnClickListener(v -> {
            showEditUserDialog(null, -1);
        });

        // 加载用户列表
        refreshUserList();
    }

    /**
     * 刷新用户列表
     */
    private void refreshUserList() {
        currentPage = 1;
        loadUserList(currentPage, pageLimit);
    }

    /**
     * 加载用户列表
     */
    private void loadUserList(int page, int limit) {
        if (isLoading)
            return;

        isLoading = true;
        showLoading(true);

        String token = sessionManager.getAuthToken();
        if (token == null) {
            showLoading(false);
            Toast.makeText(this, "您需要重新登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // 发起API请求获取用户列表
        ApiClient.getUserApiService().getUserList(token, page, limit).enqueue(new Callback<ApiResponseList<User>>() {
            @Override
            public void onResponse(Call<ApiResponseList<User>> call, Response<ApiResponseList<User>> response) {
                isLoading = false;
                showLoading(false);
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseList<User> apiResponse = response.body();
                    if ("success".equals(apiResponse.getStatus()) && apiResponse.getData() != null) {
                        List<User> users = apiResponse.getData();

                        if (page == 1) {
                            userAdapter.setUserList(users);
                        } else {
                            // 对于分页加载，添加到现有列表
                            for (User user : users) {
                                userAdapter.addUser(user);
                            }
                        }

                        // 显示/隐藏空视图
                        if (users.isEmpty() && page == 1) {
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            emptyView.setVisibility(View.GONE);
                        }
                    } else {
                        // API返回失败
                        String message = apiResponse.getMessage();
                        Toast.makeText(UserManagementActivity.this,
                                message != null ? message : "获取用户列表失败",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 请求错误
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<User>> call, Throwable t) {
                Log.e(TAG, "Failed to load user list", t);
                isLoading = false;
                showLoading(false);
                swipeRefresh.setRefreshing(false);

                Toast.makeText(UserManagementActivity.this,
                        "加载用户列表失败: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 处理API错误
     */
    private void handleApiError(Response<?> response) {
        String errorMessage = "请求失败，请稍后重试";

        if (response.code() == 401) {
            // 未授权，清除用户数据并返回登录页面
            sessionManager.logout();
            Toast.makeText(this, "会话已过期，请重新登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        } else if (response.code() == 403) {
            errorMessage = "没有权限执行此操作";
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示/隐藏加载进度
     */
    private void showLoading(boolean show) {
        loadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 显示编辑用户对话框
     */
    private void showEditUserDialog(User user, int position) {
        boolean isEdit = user != null;

        // 创建对话框视图
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null);
        TextView titleText = dialogView.findViewById(R.id.dialog_title);
        titleText.setText(isEdit ? "编辑用户" : "添加用户");

        // 初始化视图
        TextInputEditText usernameInput = dialogView.findViewById(R.id.dialog_username_input);
        TextInputEditText passwordInput = dialogView.findViewById(R.id.dialog_password_input);
        SwitchMaterial adminSwitch = dialogView.findViewById(R.id.dialog_admin_switch);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel_button);
        Button saveButton = dialogView.findViewById(R.id.dialog_save_button);

        // 如果是编辑模式，设置现有数据
        if (isEdit) {
            usernameInput.setText(user.getUsername());
            adminSwitch.setChecked(user.isAdmin());
        }

        // 创建对话框
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // 设置按钮点击事件
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        saveButton.setOnClickListener(v -> {
            // 获取输入内容
            String username = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";
            String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";
            boolean isAdmin = adminSwitch.isChecked();

            // 验证输入
            if (username.isEmpty()) {
                Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isEdit && password.isEmpty()) {
                Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            // 准备数据
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", username);
            if (!password.isEmpty()) {
                userData.put("password", password);
            }
            userData.put("is_admin", isAdmin ? 1 : 0);

            // 显示加载状态
            showLoading(true);

            if (isEdit) {
                // 更新现有用户
                updateUser(user.getId(), userData, position, dialog);
            } else {
                // 创建新用户
                createUser(userData, dialog);
            }
        });

        dialog.show();
    }

    /**
     * 创建新用户
     */
    private void createUser(Map<String, Object> userData, AlertDialog dialog) {
        String token = sessionManager.getAuthToken();
        if (token == null) {
            showLoading(false);
            Toast.makeText(this, "您需要重新登录", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.getUserApiService().createUser(token, userData).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if ("success".equals(apiResponse.getStatus()) && apiResponse.getData() != null) {
                        User newUser = apiResponse.getData();
                        userAdapter.addUser(newUser);
                        emptyView.setVisibility(View.GONE);
                        Toast.makeText(UserManagementActivity.this, "用户创建成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        String message = apiResponse.getMessage();
                        Toast.makeText(UserManagementActivity.this,
                                message != null ? message : "创建用户失败",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.e(TAG, "Failed to create user", t);
                showLoading(false);

                Toast.makeText(UserManagementActivity.this,
                        "创建用户失败: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 更新用户
     */
    private void updateUser(int userId, Map<String, Object> userData, int position, AlertDialog dialog) {
        String token = sessionManager.getAuthToken();
        if (token == null) {
            showLoading(false);
            Toast.makeText(this, "您需要重新登录", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.getUserApiService().updateUser(token, userId, userData).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if ("success".equals(apiResponse.getStatus()) && apiResponse.getData() != null) {
                        User updatedUser = apiResponse.getData();
                        userAdapter.updateUser(updatedUser, position);
                        Toast.makeText(UserManagementActivity.this, "用户更新成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        String message = apiResponse.getMessage();
                        Toast.makeText(UserManagementActivity.this,
                                message != null ? message : "更新用户失败",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.e(TAG, "Failed to update user", t);
                showLoading(false);

                Toast.makeText(UserManagementActivity.this,
                        "更新用户失败: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 删除用户
     */
    private void deleteUser(User user, int position) {
        new AlertDialog.Builder(this)
                .setTitle("删除用户")
                .setMessage("确定要删除用户 \"" + user.getUsername() + "\" 吗？此操作不可撤销。")
                .setPositiveButton("删除", (dialog, which) -> {
                    // 显示加载状态
                    showLoading(true);

                    String token = sessionManager.getAuthToken();
                    if (token == null) {
                        showLoading(false);
                        Toast.makeText(this, "您需要重新登录", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ApiClient.getUserApiService().deleteUser(token, user.getId())
                            .enqueue(new Callback<ApiResponse<Void>>() {
                                @Override
                                public void onResponse(Call<ApiResponse<Void>> call,
                                        Response<ApiResponse<Void>> response) {
                                    showLoading(false);

                                    if (response.isSuccessful() && response.body() != null) {
                                        ApiResponse<Void> apiResponse = response.body();
                                        if ("success".equals(apiResponse.getStatus())) {
                                            userAdapter.removeUser(position);
                                            if (userAdapter.getItemCount() == 0) {
                                                emptyView.setVisibility(View.VISIBLE);
                                            }

                                            Toast.makeText(UserManagementActivity.this, "用户删除成功", Toast.LENGTH_SHORT)
                                                    .show();
                                        } else {
                                            String message = apiResponse.getMessage();
                                            Toast.makeText(UserManagementActivity.this,
                                                    message != null ? message : "删除用户失败",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        handleApiError(response);
                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                                    Log.e(TAG, "Failed to delete user", t);
                                    showLoading(false);

                                    Toast.makeText(UserManagementActivity.this,
                                            "删除用户失败: " + t.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // UserAdapter.OnUserItemClickListener接口实现
    @Override
    public void onEditClick(User user, int position) {
        showEditUserDialog(user, position);
    }

    @Override
    public void onDeleteClick(User user, int position) {
        deleteUser(user, position);
    }

    @Override
    public void onItemClick(User user, int position) {
        // 可以添加用户详情查看的功能
        Toast.makeText(this, "用户: " + user.getUsername(), Toast.LENGTH_SHORT).show();
    }
}