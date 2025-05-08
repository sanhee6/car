package com.example.yidong222.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.yidong222.fragments.LoginFragment;
import com.example.yidong222.fragments.RegisterFragment;

/**
 * 登录注册页面的ViewPager2适配器
 */
public class AuthPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 2;
    private static final int LOGIN_PAGE = 0;
    private static final int REGISTER_PAGE = 1;

    private final LoginFragment loginFragment;
    private final RegisterFragment registerFragment;

    public AuthPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == REGISTER_PAGE) {
            return registerFragment;
        }
        return loginFragment; // 默认返回登录页
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }

    /**
     * 获取登录Fragment实例
     */
    public LoginFragment getLoginFragment() {
        return loginFragment;
    }

    /**
     * 获取注册Fragment实例
     */
    public RegisterFragment getRegisterFragment() {
        return registerFragment;
    }

    /**
     * 重置所有表单
     */
    public void resetAllForms() {
        loginFragment.resetForm();
        registerFragment.resetForm();
    }
}