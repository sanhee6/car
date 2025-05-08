package com.example.yidong222.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.yidong222.models.User;
import com.google.gson.Gson;

/**
 * 会话管理类，用于处理用户登录信息
 */
public class SessionManager {
    private static final String TAG = "SessionManager";

    // 共享首选项配置
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER = "user";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private final Gson gson;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }

    /**
     * 保存用户登录信息
     */
    public void saveUserLoginInfo(User user) {
        if (user == null || user.getToken() == null) {
            Log.e(TAG, "保存用户登录信息失败：用户信息不完整");
            return;
        }

        editor.putString(KEY_TOKEN, user.getToken());
        editor.putString(KEY_USER, gson.toJson(user));
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();

        Log.d(TAG, "用户登录信息已保存：" + user.getUsername());
    }

    /**
     * 获取用户信息
     */
    public User getUser() {
        String userJson = sharedPreferences.getString(KEY_USER, null);
        if (userJson == null) {
            return null;
        }
        return gson.fromJson(userJson, User.class);
    }

    /**
     * 获取用户认证令牌
     */
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    /**
     * 获取带Bearer的认证令牌，用于API请求头
     */
    public String getAuthToken() {
        String token = getToken();
        if (token == null) {
            return null;
        }
        return "Bearer " + token;
    }

    /**
     * 检查用户是否已登录
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * 用户是否是管理员
     */
    public boolean isAdmin() {
        User user = getUser();
        return user != null && user.isAdmin();
    }

    /**
     * 清除用户登录信息（退出登录）
     */
    public void logout() {
        editor.clear();
        editor.apply();
        Log.d(TAG, "用户已退出登录");
    }
}