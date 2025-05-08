package com.example.yidong222.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.yidong222.R;
import com.example.yidong222.api.ApiClient;
import com.example.yidong222.models.ApiResponse;
import com.example.yidong222.models.User;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";

    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private Button registerButton;
    private ProgressBar registerProgress;
    private TextView registerMessage;

    // 注册结果回调接口
    private RegisterResultCallback callback;

    // 获取父视图的ViewPager2和TabLayout
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    public interface RegisterResultCallback {
        void onRegisterSuccess(User user);

        void onRegisterFailure(String message);
    }

    public RegisterFragment() {
        // 必需的空构造函数
    }

    public void setRegisterResultCallback(RegisterResultCallback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // 初始化视图
        usernameInput = view.findViewById(R.id.reg_username_input);
        passwordInput = view.findViewById(R.id.reg_password_input);
        confirmPasswordInput = view.findViewById(R.id.reg_confirm_password_input);
        registerButton = view.findViewById(R.id.register_button);
        registerProgress = view.findViewById(R.id.register_progress);
        registerMessage = view.findViewById(R.id.register_message);

        // 设置注册按钮的点击事件
        registerButton.setOnClickListener(v -> attemptRegister());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 获取父视图中的ViewPager2
        if (getActivity() != null) {
            viewPager = getActivity().findViewById(R.id.view_pager);
            tabLayout = getActivity().findViewById(R.id.tab_layout);
        }
    }

    private void attemptRegister() {
        // 获取输入
        String username = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";
        String confirmPassword = confirmPasswordInput.getText() != null
                ? confirmPasswordInput.getText().toString().trim()
                : "";

        // 验证输入
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("所有字段都不能为空");
            return;
        }

        if (username.length() < 3 || username.length() > 50) {
            showMessage("用户名长度必须在3到50个字符之间");
            return;
        }

        if (password.length() < 6) {
            showMessage("密码长度必须至少为6个字符");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("两次输入的密码不一致");
            return;
        }

        // 显示进度条
        showLoading(true);

        // 准备注册数据
        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("password", password);

        // 发起注册请求
        ApiClient.getUserApiService().register(userData).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<User>> call,
                    @NonNull Response<ApiResponse<User>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if ("success".equals(apiResponse.getStatus()) && apiResponse.getData() != null) {
                        // 注册成功
                        User user = apiResponse.getData();

                        // 显示注册成功提示
                        Toast.makeText(getContext(), "注册成功！即将返回登录页面", Toast.LENGTH_SHORT).show();

                        // 重置表单
                        resetForm();

                        // 切换回登录页面
                        if (viewPager != null && tabLayout != null) {
                            viewPager.post(() -> {
                                viewPager.setCurrentItem(0); // 切换到登录页
                            });
                        }

                        if (callback != null) {
                            callback.onRegisterSuccess(user);
                        }
                    } else {
                        // 注册失败，展示错误信息
                        String message = apiResponse.getMessage();
                        showMessage(message != null ? message : "注册失败");
                        if (callback != null) {
                            callback.onRegisterFailure(message);
                        }
                    }
                } else {
                    // API响应错误
                    String errorMessage = "注册失败，请检查网络连接";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }

                    showMessage(errorMessage);
                    if (callback != null) {
                        callback.onRegisterFailure(errorMessage);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<User>> call, @NonNull Throwable t) {
                Log.e(TAG, "Register request failed", t);
                showLoading(false);

                String errorMessage = "注册请求失败: " + t.getMessage();
                showMessage(errorMessage);

                if (callback != null) {
                    callback.onRegisterFailure(errorMessage);
                }
            }
        });
    }

    private void showLoading(boolean isLoading) {
        registerButton.setEnabled(!isLoading);
        registerProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showMessage(String message) {
        if (message != null && !message.isEmpty()) {
            registerMessage.setText(message);
            registerMessage.setVisibility(View.VISIBLE);
        } else {
            registerMessage.setVisibility(View.GONE);
        }
    }

    // 重置表单
    public void resetForm() {
        if (usernameInput != null) {
            usernameInput.setText("");
        }
        if (passwordInput != null) {
            passwordInput.setText("");
        }
        if (confirmPasswordInput != null) {
            confirmPasswordInput.setText("");
        }
        if (registerMessage != null) {
            registerMessage.setVisibility(View.GONE);
        }
    }
}