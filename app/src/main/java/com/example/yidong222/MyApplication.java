package com.example.yidong222;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.yidong222.data.DataManager;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        // 在应用启动时初始化DataManager
        try {
            Log.d(TAG, "正在初始化DataManager...");
            DataManager.init(getApplicationContext());
            Log.d(TAG, "DataManager初始化成功");
        } catch (Exception e) {
            Log.e(TAG, "DataManager初始化失败", e);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}