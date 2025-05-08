package com.example.yidong222;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.yidong222.api.ApiClient;
import com.example.yidong222.api.ApiClientHelper;
import com.example.yidong222.data.DataManager;
import com.example.yidong222.fragments.HomeFragment;
import com.example.yidong222.fragments.ProfileFragment;
import com.example.yidong222.fragments.TimetableFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化DataManager
        DataManager.init(this);
        Log.d(TAG, "DataManager已初始化");

        // 记录API相关信息
        Log.d(TAG, "API基础URL: " + ApiClientHelper.getBaseUrl());
        DataManager.logNetworkConfiguration();

        // 强制使用模拟器地址
        Log.d(TAG, "检测到运行环境为:" + (isRunningOnEmulator() ? "模拟器" : "真实设备"));
        if (isRunningOnEmulator()) {
            // 在模拟器上强制使用10.0.2.2
            ApiClient.forceEmulatorUrl();
            Log.d(TAG, "在模拟器上强制使用地址: " + ApiClient.getBaseUrl());
        }

        // 刷新API客户端，尝试修复潜在的API连接问题
        ApiClientHelper.refreshClient();

        // 测试API类型是否统一
        ApiTypeTest.testApiTypes();

        // 初始化底部导航
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_schedule) {
                selectedFragment = new TimetableFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // 默认显示首页Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    /**
     * 判断当前是否运行在模拟器上
     */
    private boolean isRunningOnEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT)
                || Build.PRODUCT.contains("sdk")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu");
    }
}