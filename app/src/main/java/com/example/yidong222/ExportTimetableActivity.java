package com.example.yidong222;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yidong222.fragments.TimetableFragment;
import com.example.yidong222.models.TimetableCourse;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExportTimetableActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvSemesterInfo, tvCourseCount;
    private LinearLayout llCoursePreview;
    private RadioGroup rgExportFormat, rgWeekRange;
    private RadioButton rbExcel, rbCsv, rbImage, rbAllWeeks, rbCurrentWeek, rbCustomWeeks;
    private EditText etCustomWeeks;
    private Button btnExport;

    private ActivityResultLauncher<Intent> directoryPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        // 处理导出文件
                        exportToSelectedDirectory(uri);
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_timetable);

        initViews();
        setupListeners();
        loadCoursePreview();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        tvSemesterInfo = findViewById(R.id.tvSemesterInfo);
        tvCourseCount = findViewById(R.id.tvCourseCount);
        llCoursePreview = findViewById(R.id.llCoursePreview);

        rgExportFormat = findViewById(R.id.rgExportFormat);
        rbExcel = findViewById(R.id.rbExcel);
        rbCsv = findViewById(R.id.rbCsv);
        rbImage = findViewById(R.id.rbImage);

        rgWeekRange = findViewById(R.id.rgWeekRange);
        rbAllWeeks = findViewById(R.id.rbAllWeeks);
        rbCurrentWeek = findViewById(R.id.rbCurrentWeek);
        rbCustomWeeks = findViewById(R.id.rbCustomWeeks);
        etCustomWeeks = findViewById(R.id.etCustomWeeks);

        btnExport = findViewById(R.id.btnExport);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());

        // 导出格式选择监听
        rgExportFormat.setOnCheckedChangeListener((group, checkedId) -> {
            // 可以根据选择的格式调整UI或验证选项
        });

        // 周次范围选择监听
        rgWeekRange.setOnCheckedChangeListener((group, checkedId) -> {
            etCustomWeeks.setEnabled(checkedId == R.id.rbCustomWeeks);
        });

        // 导出按钮点击事件
        btnExport.setOnClickListener(v -> {
            if (rbCustomWeeks.isChecked()
                    && (etCustomWeeks.getText() == null || etCustomWeeks.getText().toString().trim().isEmpty())) {
                Toast.makeText(this, "请输入自定义周次范围", Toast.LENGTH_SHORT).show();
                return;
            }

            // 选择保存位置
            selectExportDirectory();
        });
    }

    private void loadCoursePreview() {
        // 获取课程数据
        List<TimetableCourse> allCourses = TimetableFragment.getAllCourses();

        // 显示课程数量
        tvCourseCount.setText("共" + allCourses.size() + "门课程");

        // 清空预览区域
        llCoursePreview.removeAllViews();

        // 最多显示5门课程预览
        int previewCount = Math.min(allCourses.size(), 5);
        for (int i = 0; i < previewCount; i++) {
            TimetableCourse course = allCourses.get(i);

            // 创建课程预览项
            View courseItemView = LayoutInflater.from(this).inflate(R.layout.item_course_preview, llCoursePreview,
                    false);

            // 设置课程信息
            TextView tvCourseName = courseItemView.findViewById(R.id.tvCourseName);
            TextView tvCourseInfo = courseItemView.findViewById(R.id.tvCourseInfo);

            tvCourseName.setText(course.getName());

            // 格式化课程信息
            String weekday = getWeekdayString(course.getWeekday());
            String section = course.getStartSection() + "-" + course.getEndSection() + "节";
            String weeks = course.getStartWeek() + "-" + course.getEndWeek() + "周";
            String info = weekday + " " + section + " | " + course.getClassroom() + " | " + weeks;
            tvCourseInfo.setText(info);

            // 添加到预览列表
            llCoursePreview.addView(courseItemView);
        }

        // 如果课程超过5门，显示查看更多提示
        if (allCourses.size() > 5) {
            TextView tvMore = new TextView(this);
            tvMore.setText("...还有" + (allCourses.size() - 5) + "门课程");
            tvMore.setTextSize(14);
            tvMore.setPadding(0, 16, 0, 0);
            llCoursePreview.addView(tvMore);
        }
    }

    private String getWeekdayString(int weekday) {
        switch (weekday) {
            case 1:
                return "周一";
            case 2:
                return "周二";
            case 3:
                return "周三";
            case 4:
                return "周四";
            case 5:
                return "周五";
            case 6:
                return "周六";
            case 7:
                return "周日";
            default:
                return "";
        }
    }

    private void selectExportDirectory() {
        // 选择保存目录
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // 根据选择的导出格式设置文件类型和名称
        String mimeType;
        String fileName;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timestamp = dateFormat.format(new Date());

        if (rbCsv.isChecked()) {
            mimeType = "text/csv";
            fileName = "课表_" + timestamp + ".csv";
        } else if (rbImage.isChecked()) {
            mimeType = "image/png";
            fileName = "课表_" + timestamp + ".png";
        } else {
            // 默认Excel格式
            mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            fileName = "课表_" + timestamp + ".xlsx";
        }

        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);

        directoryPickerLauncher.launch(intent);
    }

    private void exportToSelectedDirectory(Uri uri) {
        try {
            // 模拟导出过程
            Toast.makeText(this, "正在导出课表...", Toast.LENGTH_SHORT).show();

            // 获取导出格式
            String format;
            if (rbCsv.isChecked()) {
                format = "CSV";
            } else if (rbImage.isChecked()) {
                format = "图片";
            } else {
                format = "Excel";
            }

            // 获取周次范围
            String weekRange;
            if (rbCurrentWeek.isChecked()) {
                weekRange = "当前周";
            } else if (rbCustomWeeks.isChecked()) {
                weekRange = etCustomWeeks.getText().toString();
            } else {
                weekRange = "全部周次";
            }

            // 模拟导出文件内容
            String fileContent = "这是导出的课表数据示例\n格式: " + format + "\n周次范围: " + weekRange;

            // 写入文件
            FileOutputStream outputStream = (FileOutputStream) getContentResolver().openOutputStream(uri);
            outputStream.write(fileContent.getBytes());
            outputStream.close();

            Toast.makeText(this, "课表导出成功!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "导出失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}