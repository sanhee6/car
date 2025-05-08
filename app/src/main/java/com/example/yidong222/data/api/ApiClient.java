package com.example.yidong222.data.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import java.util.concurrent.TimeUnit;
import android.util.Log;

/**
 * DataSyncManager使用的API客户端
 * 修改为从主ApiClient获取URL，确保所有请求使用同一服务器地址
 */
public class ApiClient {
    private static final String TAG = "DataApiClient";
    private static Retrofit retrofit = null;

    public static String getBaseUrl() {
        // 从主ApiClient获取URL，确保URL一致
        return com.example.yidong222.api.ApiClient.getBaseUrl();
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(20, TimeUnit.SECONDS) // 降低超时时间
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl()) // 使用getBaseUrl()方法获取动态URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            Log.d(TAG, "创建DataSyncManager的API客户端，BASE_URL: " + getBaseUrl());
        }
        return retrofit;
    }

    /**
     * 强制重置Retrofit客户端，用于URL变更时
     */
    public static void resetClient() {
        Log.d(TAG, "重置DataSyncManager的API客户端");
        retrofit = null;
    }
}