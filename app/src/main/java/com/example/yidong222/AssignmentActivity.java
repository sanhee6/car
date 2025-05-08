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

import com.example.yidong222.adapters.AssignmentAdapter;
import com.example.yidong222.data.DataManager;
import com.example.yidong222.data.MockDataProvider;
import com.example.yidong222.models.Assignment;
import com.example.yidong222.models.AssignmentDto;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssignmentActivity extends AppCompatActivity implements AssignmentAdapter.AssignmentItemClickListener {

    private static final String TAG = "AssignmentActivity";
    private RecyclerView recyclerView;
    private AssignmentAdapter adapter;
    private List<Assignment> assignmentList = new ArrayList<>();
    private FloatingActionButton fabAdd;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    private View emptyView;
    private boolean isOfflineMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("作业管理");

            recyclerView = findViewById(R.id.recyclerViewAssignments);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            emptyView = findViewById(R.id.tvEmptyAssignments);

            swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this::loadAssignmentsFromServer);

            adapter = new AssignmentAdapter(assignmentList, this);
            recyclerView.setAdapter(adapter);

            fabAdd = findViewById(R.id.fabAddAssignment);
            fabAdd.setOnClickListener(v -> showAddAssignmentDialog());

            // 从服务器加载作业数据
            loadAssignmentsFromServer();
        } catch (Exception e) {
            Log.e(TAG, "初始化失败", e);
            Toast.makeText(this, "应用初始化失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAssignmentsFromServer() {
        swipeRefreshLayout.setRefreshing(true);
        Log.d(TAG, "开始从服务器加载作业数据");

        // 检查网络连接
        if (!DataManager.isNetworkAvailable(this)) {
            Log.d(TAG, "无网络连接，使用离线模式");
            Toast.makeText(this, "无网络连接，显示本地数据", Toast.LENGTH_SHORT).show();
            isOfflineMode = true;
            loadOfflineData();
            return;
        }

        // 确保DataManager已初始化
        try {
            DataManager.init(getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, "DataManager初始化失败", e);
        }

        // 从服务器获取作业列表
        DataManager.getAssignments(new DataManager.DataCallback<Assignment>() {
            @Override
            public void onSuccess(List<Assignment> data) {
                Log.d(TAG, "成功获取作业列表: " + data.size() + " 条记录");
                runOnUiThread(() -> {
                    assignmentList.clear();
                    assignmentList.addAll(data);
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    updateEmptyView();
                });
            }

            @Override
            public void onFailure(String message) {
                Log.e(TAG, "获取作业列表失败: " + message);
                runOnUiThread(() -> {
                    Toast.makeText(AssignmentActivity.this, "获取作业信息失败，使用本地数据", Toast.LENGTH_SHORT).show();
                    isOfflineMode = true;
                    loadOfflineData();
                });
            }
        });
    }

    private void loadOfflineData() {
        // 加载本地模拟数据
        assignmentList.clear();
        assignmentList.addAll(MockDataProvider.getMockAssignments());
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (assignmentList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showAddAssignmentDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_assignment, null);
        TextInputEditText etTitle = dialogView.findViewById(R.id.etAssignmentTitle);
        TextInputEditText etCourse = dialogView.findViewById(R.id.etAssignmentCourse);
        TextInputEditText etDeadline = dialogView.findViewById(R.id.etAssignmentDeadline);
        TextInputEditText etDescription = dialogView.findViewById(R.id.etAssignmentDescription);
        TextInputEditText etMaxScore = dialogView.findViewById(R.id.etAssignmentMaxScore);

        new MaterialAlertDialogBuilder(this)
                .setTitle("添加作业")
                .setView(dialogView)
                .setPositiveButton("添加", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String course = etCourse.getText().toString().trim();
                    String deadline = etDeadline.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String maxScoreStr = etMaxScore.getText().toString().trim();

                    if (!title.isEmpty() && !course.isEmpty() && !deadline.isEmpty() && !maxScoreStr.isEmpty()) {
                        try {
                            // 解析最高分数
                            int maxScore = Integer.parseInt(maxScoreStr);

                            // 构建ISO日期时间格式
                            if (!deadline.contains("T")) {
                                deadline = deadline + "T23:59:59.000Z";
                            }

                            // 创建新作业DTO
                            AssignmentDto newAssignment = new AssignmentDto(
                                    1, // 临时课程ID，实际应用中应该获取真实的课程ID
                                    title,
                                    description,
                                    deadline,
                                    maxScore);

                            // 提交到服务器
                            createAssignmentOnServer(newAssignment);
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "请输入有效的分数", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "请填写完整作业信息", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void createAssignmentOnServer(AssignmentDto assignmentDto) {
        // 检查网络连接
        if (!DataManager.isNetworkAvailable(this)) {
            Toast.makeText(this, "无网络连接，无法创建作业", Toast.LENGTH_SHORT).show();
            return;
        }

        // 提交新作业到服务器
        DataManager.createAssignment(assignmentDto, new DataManager.DetailCallback<Assignment>() {
            @Override
            public void onSuccess(Assignment data) {
                runOnUiThread(() -> {
                    // 添加到列表顶部
                    assignmentList.add(0, data);
                    adapter.notifyItemInserted(0);
                    recyclerView.scrollToPosition(0);
                    Toast.makeText(AssignmentActivity.this, "作业创建成功", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(AssignmentActivity.this, "创建作业失败: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showEditAssignmentDialog(int position, Assignment assignment) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_assignment, null);
        TextInputEditText etTitle = dialogView.findViewById(R.id.etAssignmentTitle);
        TextInputEditText etCourse = dialogView.findViewById(R.id.etAssignmentCourse);
        TextInputEditText etDeadline = dialogView.findViewById(R.id.etAssignmentDeadline);
        TextInputEditText etDescription = dialogView.findViewById(R.id.etAssignmentDescription);
        TextInputEditText etMaxScore = dialogView.findViewById(R.id.etAssignmentMaxScore);

        etTitle.setText(assignment.getTitle());
        etCourse.setText(assignment.getCourseName());
        etDeadline.setText(assignment.getDeadline());
        etDescription.setText(assignment.getDescription());
        etMaxScore.setText("100"); // 默认为100分，实际应从assignment对象获取

        new MaterialAlertDialogBuilder(this)
                .setTitle("编辑作业")
                .setView(dialogView)
                .setPositiveButton("保存", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String course = etCourse.getText().toString().trim();
                    String deadline = etDeadline.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String maxScoreStr = etMaxScore.getText().toString().trim();

                    if (!title.isEmpty() && !course.isEmpty() && !deadline.isEmpty() && !maxScoreStr.isEmpty()) {
                        try {
                            // 解析最高分数
                            int maxScore = Integer.parseInt(maxScoreStr);

                            // 构建ISO日期时间格式
                            if (!deadline.contains("T")) {
                                deadline = deadline + "T23:59:59.000Z";
                            }

                            // 解析作业ID
                            int assignmentId = assignment.getId();

                            // 创建更新后的作业DTO
                            AssignmentDto updatedAssignment = new AssignmentDto(
                                    1, // 临时课程ID，实际应用中应该获取真实的课程ID
                                    title,
                                    description,
                                    deadline,
                                    maxScore);

                            // 更新服务器上的作业
                            updateAssignmentOnServer(assignmentId, updatedAssignment, position, assignment);
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "请输入有效的分数", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "请填写完整作业信息", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void updateAssignmentOnServer(int assignmentId, AssignmentDto assignmentDto, int position,
            Assignment currentAssignment) {
        // 检查网络连接
        if (!DataManager.isNetworkAvailable(this)) {
            Toast.makeText(this, "无网络连接，无法更新作业", Toast.LENGTH_SHORT).show();
            return;
        }

        // 提交更新到服务器
        DataManager.updateAssignment(assignmentId, assignmentDto, new DataManager.DetailCallback<Assignment>() {
            @Override
            public void onSuccess(Assignment data) {
                runOnUiThread(() -> {
                    // 更新列表项
                    assignmentList.set(position, data);
                    adapter.notifyItemChanged(position);
                    Toast.makeText(AssignmentActivity.this, "作业更新成功", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(AssignmentActivity.this, "更新作业失败: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void deleteAssignment(int position) {
        Assignment assignment = assignmentList.get(position);

        // 尝试解析作业ID
        int assignmentId = assignment.getId();

        new MaterialAlertDialogBuilder(this)
                .setTitle("删除作业")
                .setMessage("确定要删除 \"" + assignmentList.get(position).getTitle() + "\" 吗?")
                .setPositiveButton("删除", (dialog, which) -> {
                    deleteAssignmentFromServer(assignmentId, position);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void deleteAssignmentFromServer(int assignmentId, int position) {
        // 检查网络连接
        if (!DataManager.isNetworkAvailable(this)) {
            Toast.makeText(this, "无网络连接，无法删除作业", Toast.LENGTH_SHORT).show();
            return;
        }

        // 发送删除请求到服务器
        DataManager.deleteAssignment(assignmentId, new DataManager.DetailCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                runOnUiThread(() -> {
                    assignmentList.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(AssignmentActivity.this, "作业已删除", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(AssignmentActivity.this, "删除作业失败: " + message, Toast.LENGTH_SHORT).show();
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
    public void onAssignmentClick(int position, Assignment assignment) {
        // 显示作业详情
        new MaterialAlertDialogBuilder(this)
                .setTitle(assignment.getTitle())
                .setMessage("课程: " + assignment.getCourseName() +
                        "\n截止日期: " + assignment.getDeadline() +
                        "\n详细描述: " + assignment.getDescription() +
                        "\n完成状态: " + (assignment.isCompleted() ? "已完成" : "未完成"))
                .setPositiveButton("确定", null)
                .show();
    }

    @Override
    public void onAssignmentEditClick(int position, Assignment assignment) {
        showEditAssignmentDialog(position, assignment);
    }

    @Override
    public void onAssignmentDeleteClick(int position) {
        deleteAssignment(position);
    }

    @Override
    public void onAssignmentStatusChange(int position, Assignment assignment, boolean isCompleted) {
        assignment.setCompleted(isCompleted);
        adapter.notifyItemChanged(position);

        // 实际更新服务器上的作业状态
        String message = isCompleted ? "作业已标记为完成" : "作业已标记为未完成";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // 如果在实际应用中，这里应该创建一个DTO并调用更新方法
        // 例如：
        /*
         * AssignmentDto updatedDto = new AssignmentDto();
         * updatedDto.setCourseId(assignment.getCourseId());
         * updatedDto.setTitle(assignment.getTitle());
         * updatedDto.setDescription(assignment.getDescription());
         * updatedDto.setDeadline(assignment.getDeadline());
         * 
         * DataManager.updateAssignment(assignment.getId(), updatedDto, new
         * DataManager.DetailCallback<Assignment>() {
         * 
         * @Override
         * public void onSuccess(Assignment data) {
         * // 成功更新
         * }
         * 
         * @Override
         * public void onFailure(String message) {
         * // 更新失败，回滚UI状态
         * assignment.setCompleted(!isCompleted);
         * adapter.notifyItemChanged(position);
         * Toast.makeText(AssignmentActivity.this, "更新状态失败: " + message,
         * Toast.LENGTH_SHORT).show();
         * }
         * });
         */
    }
}