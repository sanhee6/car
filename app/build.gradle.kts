plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.yidong222"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.yidong222"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("de.hdodenhof:circleimageview:3.1.0")
    
    // SwipeRefreshLayout (下拉刷新)
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    
    // Room 数据库
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    
    // MySQL JDBC (用于直接连接MySQL)
    implementation("mysql:mysql-connector-java:8.0.28")
    
    // Retrofit (用于API请求)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Gson (JSON解析)
    implementation("com.google.code.gson:gson:2.10.1")
    
    // OkHttp (网络请求)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}