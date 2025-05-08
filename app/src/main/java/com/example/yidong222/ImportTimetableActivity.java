package com.example.yidong222;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yidong222.fragments.TimetableFragment;
import com.example.yidong222.models.TimetableCourse;
import com.google.android.material.textfield.TextInputEditText;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImportTimetableActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextInputEditText etSchool, etStudentId, etPassword;
    private Button btnImportFromSystem, btnSelectFile, btnImportFromExcel;
    private TextView tvSelectedFile, tvDownloadTemplate;

    private Uri selectedFileUri;

    private ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedFileUri = result.getData().getData();
                    if (selectedFileUri != null) {
                        String fileName = getFileNameFromUri(selectedFileUri);
                        tvSelectedFile.setText("已选择: " + fileName);
                        tvSelectedFile.setVisibility(View.VISIBLE);
                        btnImportFromExcel.setEnabled(true);
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_timetable);

        initViews();
        setupListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        etSchool = findViewById(R.id.etSchool);
        etStudentId = findViewById(R.id.etStudentId);
        etPassword = findViewById(R.id.etPassword);
        btnImportFromSystem = findViewById(R.id.btnImportFromSystem);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnImportFromExcel = findViewById(R.id.btnImportFromExcel);
        tvSelectedFile = findViewById(R.id.tvSelectedFile);
        tvDownloadTemplate = findViewById(R.id.tvDownloadTemplate);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());

        // 教务系统导入
        btnImportFromSystem.setOnClickListener(v -> {
            String school = etSchool.getText() != null ? etSchool.getText().toString().trim() : "";
            String studentId = etStudentId.getText() != null ? etStudentId.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

            if (school.isEmpty() || studentId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请填写完整的登录信息", Toast.LENGTH_SHORT).show();
                return;
            }

            // 实际从教务系统导入
            importFromEducationSystem(school, studentId, password);
        });

        // 选择Excel文件
        btnSelectFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            String[] mimeTypes = { "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" };
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            filePickerLauncher.launch(intent);
        });

        // 从Excel导入
        btnImportFromExcel.setOnClickListener(v -> {
            if (selectedFileUri != null) {
                importFromExcel(selectedFileUri);
            }
        });

        // 下载模板
        tvDownloadTemplate.setOnClickListener(v -> {
            // 模拟下载模板
            Toast.makeText(this, "模板下载中...", Toast.LENGTH_SHORT).show();
            // 实际应用中这里应该触发模板下载，可以使用DownloadManager等
        });
    }

    // 从教务系统导入课表
    private void importFromEducationSystem(String school, String studentId, String password) {
        // 实际从教务系统导入
        Toast.makeText(this, "正在从教务系统导入课表...", Toast.LENGTH_SHORT).show();

        // 提示用户功能尚未实现
        new android.os.Handler().postDelayed(() -> {
            Toast.makeText(this, "教务系统导入功能尚未实现，请等待后续更新", Toast.LENGTH_LONG).show();
            finish();
        }, 2000);
    }

    // 从Excel导入课表
    private void importFromExcel(Uri fileUri) {
        // 实际解析Excel文件
        Toast.makeText(this, "正在解析Excel文件...", Toast.LENGTH_SHORT).show();

        // 提示用户功能尚未实现
        new android.os.Handler().postDelayed(() -> {
            try {
                // 获取Excel文件内容
                InputStream inputStream = getContentResolver().openInputStream(fileUri);
                if (inputStream != null) {
                    inputStream.close();
                    Toast.makeText(this, "Excel导入功能尚未实现，请等待后续更新", Toast.LENGTH_LONG).show();
                    finish();
                }
            } catch (Exception e) {
                Toast.makeText(this, "导入失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

    // 将导入的课程添加到课表
    private void importCoursesToTimetable(List<TimetableCourse> courses) {
        if (courses != null && !courses.isEmpty()) {
            // 实际应用中应该调用API保存课程
            Toast.makeText(this, "成功导入" + courses.size() + "门课程", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "没有导入任何课程", Toast.LENGTH_SHORT).show();
        }
    }

    // 从Uri获取文件名
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }
}