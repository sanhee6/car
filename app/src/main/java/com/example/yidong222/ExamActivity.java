package com.example.yidong222;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.yidong222.adapters.ExamAdapter;
import com.example.yidong222.data.DataManager;
import com.example.yidong222.data.MockDataProvider;
import com.example.yidong222.models.Exam;
import com.example.yidong222.models.ExamDto;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExamActivity extends AppCompatActivity implements ExamAdapter.ExamItemClickListener {

    private static final String TAG = "ExamActivity";
    private RecyclerView recyclerView;
    private ExamAdapter adapter;
    private List<Exam> examList = new ArrayList<>();
    private FloatingActionButton fabAdd;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat displayTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private View emptyView;
    private boolean isOfflineMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("考试管理");

            recyclerView = findViewById(R.id.recyclerViewExams);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            emptyView = findViewById(R.id.tvEmptyExams);

            swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this::loadExamsFromServer);

            adapter = new ExamAdapter(examList, this);
            recyclerView.setAdapter(adapter);

            fabAdd = findViewById(R.id.fabAddExam);
            fabAdd.setOnClickListener(v -> showAddExamDialog());

            // 从服务器加载考试数据
            loadExamsFromServer();
        } catch (Exception e) {
            Log.e(TAG, "初始化失败", e);
            Toast.makeText(this, "应用初始化失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadExamsFromServer() {
        swipeRefreshLayout.setRefreshing(true);

        // 检查网络连接
        if (!DataManager.isNetworkAvailable(this)) {
            Log.d(TAG, "无网络连接，使用离线模式");
            Toast.makeText(this, "无网络连接，显示本地数据", Toast.LENGTH_SHORT).show();
            isOfflineMode = true;
            loadOfflineData();
            return;
        }

        // 从服务器获取考试列表
        DataManager.getExams(new DataManager.DataCallback<Exam>() {
            @Override
            public void onSuccess(List<Exam> data) {
                runOnUiThread(() -> {
                    examList.clear();
                    examList.addAll(data);
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    updateEmptyView();
                });
            }

            @Override
            public void onFailure(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(ExamActivity.this, "获取考试信息失败，使用本地数据", Toast.LENGTH_SHORT).show();
                    isOfflineMode = true;
                    loadOfflineData();
                });
            }
        });
    }

    private void loadOfflineData() {
        // 加载本地模拟数据
        examList.clear();
        examList.addAll(MockDataProvider.getMockExams());
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (examList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showAddExamDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_exam, null);
        TextInputEditText etCourseName = dialogView.findViewById(R.id.etExamCourseName);
        TextInputEditText etExamName = dialogView.findViewById(R.id.etExamName);
        TextInputEditText etDate = dialogView.findViewById(R.id.etExamDate);
        TextInputEditText etTime = dialogView.findViewById(R.id.etExamTime);
        TextInputEditText etLocation = dialogView.findViewById(R.id.etExamLocation);
        TextInputEditText etSeatNumber = dialogView.findViewById(R.id.etExamSeatNumber);
        TextInputEditText etDuration = dialogView.findViewById(R.id.etExamDuration);

        new MaterialAlertDialogBuilder(this)
                .setTitle("添加考试")
                .setView(dialogView)
                .setPositiveButton("添加", (dialog, which) -> {
                    String courseName = etCourseName.getText().toString().trim();
                    String examName = etExamName.getText().toString().trim();
                    String date = etDate.getText().toString().trim();
                    String time = etTime.getText().toString().trim();
                    String location = etLocation.getText().toString().trim();
                    String seatNumber = etSeatNumber.getText().toString().trim();
                    String durationStr = etDuration.getText().toString().trim();

                    if (!courseName.isEmpty() && !examName.isEmpty() && !date.isEmpty() && !time.isEmpty()
                            && !location.isEmpty() && !durationStr.isEmpty()) {

                        try {
                            // 解析持续时间
                            int duration = Integer.parseInt(durationStr);

                            // 构建ISO日期时间格式
                            String dateTime = date + "T" + time + ":00.000Z";

                            // 创建新考试DTO
                            ExamDto newExam = new ExamDto(
                                    1, // 临时课程ID，实际应用中应该获取真实的课程ID
                                    examName,
                                    dateTime,
                                    duration,
                                    location,
                                    "考试描述: " + examName);

                            // 提交到服务器
                            createExamOnServer(newExam);
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "请输入有效的考试时长", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "请填写完整考试信息", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void createExamOnServer(ExamDto examDto) {
        // 检查网络连接
        if (!DataManager.isNetworkAvailable(this)) {
            Toast.makeText(this, "无网络连接，无法创建考试", Toast.LENGTH_SHORT).show();
            return;
        }

        // 提交新考试到服务器
        DataManager.createExam(examDto, new DataManager.DetailCallback<Exam>() {
            @Override
            public void onSuccess(Exam data) {
                runOnUiThread(() -> {
                    // 添加到列表顶部
                    examList.add(0, data);
                    adapter.notifyItemInserted(0);
                    recyclerView.scrollToPosition(0);
                    Toast.makeText(ExamActivity.this, "考试创建成功", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(ExamActivity.this, "创建考试失败: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showEditExamDialog(int position, Exam exam) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_exam, null);
        TextInputEditText etCourseName = dialogView.findViewById(R.id.etExamCourseName);
        TextInputEditText etExamName = dialogView.findViewById(R.id.etExamName);
        TextInputEditText etDate = dialogView.findViewById(R.id.etExamDate);
        TextInputEditText etTime = dialogView.findViewById(R.id.etExamTime);
        TextInputEditText etLocation = dialogView.findViewById(R.id.etExamLocation);
        TextInputEditText etSeatNumber = dialogView.findViewById(R.id.etExamSeatNumber);
        TextInputEditText etDuration = dialogView.findViewById(R.id.etExamDuration);

        etCourseName.setText(exam.getCourseName());
        etExamName.setText(exam.getExamName());
        etDate.setText(exam.getDate());
        etTime.setText(exam.getTime());
        etLocation.setText(exam.getLocation());
        etSeatNumber.setText(exam.getSeatNumber());
        etDuration.setText("120"); // 假设默认为120分钟，实际应从exam对象获取

        new MaterialAlertDialogBuilder(this)
                .setTitle("编辑考试")
                .setView(dialogView)
                .setPositiveButton("保存", (dialog, which) -> {
                    String courseName = etCourseName.getText().toString().trim();
                    String examName = etExamName.getText().toString().trim();
                    String date = etDate.getText().toString().trim();
                    String time = etTime.getText().toString().trim();
                    String location = etLocation.getText().toString().trim();
                    String seatNumber = etSeatNumber.getText().toString().trim();
                    String durationStr = etDuration.getText().toString().trim();

                    if (!courseName.isEmpty() && !examName.isEmpty() && !date.isEmpty() && !time.isEmpty()
                            && !location.isEmpty() && !durationStr.isEmpty()) {

                        try {
                            // 解析持续时间
                            int duration = Integer.parseInt(durationStr);

                            // 构建ISO日期时间格式
                            String dateTime = date + "T" + time + ":00.000Z";

                            // 解析考试ID
                            int examId = exam.getId();

                            // 创建更新后的考试DTO
                            ExamDto updatedExam = new ExamDto(
                                    1, // 临时课程ID，实际应用中应该获取真实的课程ID
                                    examName,
                                    dateTime,
                                    duration,
                                    location,
                                    "考试描述: " + examName);

                            // 更新服务器上的考试
                            updateExamOnServer(examId, updatedExam, position, exam);
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "请输入有效的考试时长", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "请填写完整考试信息", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void updateExamOnServer(int examId, ExamDto examDto, int position, Exam currentExam) {
        // 检查网络连接
        if (!DataManager.isNetworkAvailable(this)) {
            Toast.makeText(this, "无网络连接，无法更新考试", Toast.LENGTH_SHORT).show();
            return;
        }

        // 提交更新到服务器
        DataManager.updateExam(examId, examDto, new DataManager.DetailCallback<Exam>() {
            @Override
            public void onSuccess(Exam data) {
                runOnUiThread(() -> {
                    // 更新列表项
                    examList.set(position, data);
                    adapter.notifyItemChanged(position);
                    Toast.makeText(ExamActivity.this, "考试更新成功", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(ExamActivity.this, "更新考试失败: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void deleteExam(int position) {
        Exam exam = examList.get(position);

        // 获取考试ID
        int examId = exam.getId();

        new MaterialAlertDialogBuilder(this)
                .setTitle("删除考试")
                .setMessage("确定要删除 \"" + examList.get(position).getCourseName() + "-"
                        + examList.get(position).getExamName() + "\" 吗?")
                .setPositiveButton("删除", (dialog, which) -> {
                    deleteExamFromServer(examId, position);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void deleteExamFromServer(int examId, int position) {
        // 检查网络连接
        if (!DataManager.isNetworkAvailable(this)) {
            Toast.makeText(this, "无网络连接，无法删除考试", Toast.LENGTH_SHORT).show();
            return;
        }

        // 发送删除请求到服务器
        DataManager.deleteExam(examId, new DataManager.DetailCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                runOnUiThread(() -> {
                    examList.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(ExamActivity.this, "考试已删除", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(ExamActivity.this, "删除考试失败: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onExamClick(int position, Exam exam) {
        // 显示考试详情
        new MaterialAlertDialogBuilder(this)
                .setTitle(exam.getCourseName() + " - " + exam.getExamName())
                .setMessage("日期: " + exam.getDate() +
                        "\n时间: " + exam.getTime() +
                        "\n地点: " + exam.getLocation() +
                        "\n座位号: " + exam.getSeatNumber() +
                        "\n状态: " + exam.getStatus())
                .setPositiveButton("确定", null)
                .show();
    }

    @Override
    public void onExamEditClick(int position, Exam exam) {
        showEditExamDialog(position, exam);
    }

    @Override
    public void onExamDeleteClick(int position) {
        deleteExam(position);
    }

    @Override
    public void onExamStatusToggle(int position, Exam exam) {
        // 切换考试状态
        String newStatus = "已考".equals(exam.getStatus()) ? "未考" : "已考";
        exam.setStatus(newStatus);
        adapter.notifyItemChanged(position);

        // 实际更新服务器上的考试状态
        String message = "已考".equals(newStatus) ? "考试已标记为已考" : "考试已标记为未考";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // 如果在实际应用中，这里应该创建一个DTO并调用更新方法
        // 例如：
        /*
         * ExamDto updatedDto = new ExamDto();
         * updatedDto.setCourseId(exam.getCourseId());
         * updatedDto.setTitle(exam.getTitle());
         * updatedDto.setExamDate(exam.getDate() + "T" + exam.getTime() + ":00.000Z");
         * updatedDto.setLocation(exam.getLocation());
         * updatedDto.setDescription(exam.getDescription());
         * 
         * DataManager.updateExam(exam.getId(), updatedDto, new
         * DataManager.DetailCallback<Exam>() {
         * 
         * @Override
         * public void onSuccess(Exam data) {
         * // 成功更新
         * }
         * 
         * @Override
         * public void onFailure(String message) {
         * // 更新失败，回滚UI状态
         * String rollbackStatus = "已考".equals(newStatus) ? "未考" : "已考";
         * exam.setStatus(rollbackStatus);
         * adapter.notifyItemChanged(position);
         * Toast.makeText(ExamActivity.this, "更新状态失败: " + message,
         * Toast.LENGTH_SHORT).show();
         * }
         * });
         */
    }
}