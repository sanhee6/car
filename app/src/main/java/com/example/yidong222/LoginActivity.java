package com.example.yidong222;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.yidong222.adapters.AuthPagerAdapter;
import com.example.yidong222.fragments.LoginFragment;
import com.example.yidong222.fragments.RegisterFragment;
import com.example.yidong222.models.User;
import com.example.yidong222.utils.SessionManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LoginActivity extends AppCompatActivity
        implements LoginFragment.LoginResultCallback, RegisterFragment.RegisterResultCallback {

    private static final String TAG = "LoginActivity";

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private AuthPagerAdapter pagerAdapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化会话管理器
        sessionManager = new SessionManager(this);

        // 检查用户是否已登录
        if (sessionManager.isLoggedIn()) {
            navigateToAppropriateScreen();
            return;
        }

        // 初始化视图
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        // 设置ViewPager和Adapter
        pagerAdapter = new AuthPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // 设置TabLayout和ViewPager2的联动
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("登录");
            } else {
                tab.setText("注册");
            }
        }).attach();

        // 设置Tab切换监听器，清除表单
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 切换时不做任何操作
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 切换时重置表单
                pagerAdapter.resetAllForms();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 不做任何操作
            }
        });

        // 设置登录和注册回调
        pagerAdapter.getLoginFragment().setLoginResultCallback(this);
        pagerAdapter.getRegisterFragment().setRegisterResultCallback(this);
    }

    /**
     * 登录成功回调方法
     */
    @Override
    public void onLoginSuccess(User user) {
        // 保存用户信息
        sessionManager.saveUserLoginInfo(user);

        Log.d(TAG, "用户登录成功：" + user.getUsername() + ", 是否为管理员：" + user.isAdmin());
        Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();

        // 根据用户类型导航到合适的页面
        navigateToAppropriateScreen();
    }

    /**
     * 登录失败回调方法
     */
    @Override
    public void onLoginFailure(String message) {
        Log.e(TAG, "登录失败：" + message);
    }

    /**
     * 注册成功回调方法
     */
    @Override
    public void onRegisterSuccess(User user) {
        // 保存用户信息
        sessionManager.saveUserLoginInfo(user);
        Log.d(TAG, "用户注册成功：" + user.getUsername());

        // 注意：不再自动跳转到MainActivity
        // 界面切换由RegisterFragment自己处理
    }

    /**
     * 注册失败回调方法
     */
    @Override
    public void onRegisterFailure(String message) {
        Log.e(TAG, "注册失败：" + message);
    }

    /**
     * 根据用户类型导航到合适的页面
     */
    private void navigateToAppropriateScreen() {
        if (sessionManager.isAdmin()) {
            // 管理员跳转到用户管理页面
            Intent intent = new Intent(this, UserManagementActivity.class);
            startActivity(intent);
        } else {
            // 普通用户跳转到主页面
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }
}