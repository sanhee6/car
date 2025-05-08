package com.example.yidong222;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yidong222.models.TimetableCourse;
import com.example.yidong222.models.CourseSchedule;
import com.example.yidong222.data.DataSyncManager;

import java.util.List;

public class CourseDetailActivity extends AppCompatActivity {
    private static final String TAG = "CourseDetailActivity";
    private static final String EXTRA_COURSE_NAME = "extra_course_name";
    private static final String EXTRA_TEACHER = "extra_teacher";
    private static final String EXTRA_CLASSROOM = "extra_classroom";
    private static final String EXTRA_TIME = "extra_time";
    private static final String EXTRA_WEEKS = "extra_weeks";
    private static final String EXTRA_COURSE_DATA = "course_data";
    private static final String EXTRA_COURSE_ID = "course_id";

    private TextView tvCourseName, tvTeacher, tvClassroom, tvTime, tvWeeks;
    private TextView tvNoAssignments, tvNoExams;
    private DataSyncManager dataSyncManager;
    private RecyclerView rvAssignments, rvExams;

    // 供TimetableFragment调用的启动方法
    public static Intent getStartIntent(Context context, TimetableCourse course) {
        Intent intent = new Intent(context, CourseDetailActivity.class);
        intent.putExtra(EXTRA_COURSE_DATA, course);
        return intent;
    }

    // 供CourseListFragment调用的启动方法
    public static Intent getStartIntent(Context context, String courseName, String teacher,
            String classroom, String time, String weeks) {
        Intent intent = new Intent(context, CourseDetailActivity.class);
        intent.putExtra(EXTRA_COURSE_NAME, courseName);
        intent.putExtra(EXTRA_TEACHER, teacher);
        intent.putExtra(EXTRA_CLASSROOM, classroom);
        intent.putExtra(EXTRA_TIME, time);
        intent.putExtra(EXTRA_WEEKS, weeks);
        return intent;
    }

    // 根据课程ID启动详情页
    public static Intent getStartIntent(Context context, int courseId) {
        Intent intent = new Intent(context, CourseDetailActivity.class);
        intent.putExtra(EXTRA_COURSE_ID, courseId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        // 初始化控件
        initViews();
        setupToolbar();

        // 获取数据管理器
        dataSyncManager = DataSyncManager.getInstance(this);

        // 尝试获取Intent数据
        try {
            Intent intent = getIntent();
            if (intent == null) {
                Log.e(TAG, "Intent为null");
                showError("无法加载课程数据");
                return;
            }

            Log.d(TAG, "获取到Intent，尝试解析课程数据");

            // 检查Intent中包含的数据类型
            boolean hasCourseData = intent.hasExtra(EXTRA_COURSE_DATA);
            boolean hasCourseStrings = intent.hasExtra(EXTRA_COURSE_NAME);
            boolean hasCourseId = intent.hasExtra(EXTRA_COURSE_ID);

            Log.d(TAG, "Intent数据检查: 包含课程对象=" + hasCourseData +
                    ", 包含课程字符串数据=" + hasCourseStrings +
                    ", 包含课程ID=" + hasCourseId);

            if (hasCourseData) {
                // 从TimetableFragment传递的TimetableCourse对象
                try {
                    Object obj = intent.getSerializableExtra(EXTRA_COURSE_DATA);
                    Log.d(TAG, "从Intent获取的对象类型: " + (obj != null ? obj.getClass().getName() : "null"));

                    if (obj instanceof TimetableCourse) {
                        TimetableCourse course = (TimetableCourse) obj;
                        Log.d(TAG, "成功获取TimetableCourse对象: " + course.getName());
                        displayCourseInfo(course);
                    } else {
                        Log.e(TAG, "获取的对象不是TimetableCourse类型");
                        showError("课程数据类型错误");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "反序列化TimetableCourse对象失败", e);
                    showError("课程数据解析失败: " + e.getMessage());
                }
            } else if (hasCourseStrings) {
                // 从CourseListFragment传递的字符串参数
                String courseName = intent.getStringExtra(EXTRA_COURSE_NAME);
                String teacher = intent.getStringExtra(EXTRA_TEACHER);
                String classroom = intent.getStringExtra(EXTRA_CLASSROOM);
                String time = intent.getStringExtra(EXTRA_TIME);
                String weeks = intent.getStringExtra(EXTRA_WEEKS);

                Log.d(TAG, "从Intent获取字符串数据: courseName=" + courseName +
                        ", teacher=" + teacher + ", time=" + time);

                displayCourseStrings(courseName, teacher, classroom, time, weeks);
            } else if (hasCourseId) {
                // 从其他地方传递的课程ID
                int courseId = intent.getIntExtra(EXTRA_COURSE_ID, -1);
                if (courseId != -1) {
                    Log.d(TAG, "从Intent获取课程ID: " + courseId);
                    loadCourseDetails(courseId);
                } else {
                    Log.e(TAG, "课程ID无效: -1");
                    showError("没有找到课程信息");
                }
            } else {
                Log.e(TAG, "Intent中没有找到任何课程数据");
                showError("没有课程数据");
            }
        } catch (Exception e) {
            Log.e(TAG, "处理Intent数据时发生异常", e);
            showError("加载课程信息出错: " + e.getMessage());
        }
    }

    private void initViews() {
        tvCourseName = findViewById(R.id.tvCourseName);
        tvTeacher = findViewById(R.id.tvTeacher);
        tvClassroom = findViewById(R.id.tvClassroom);
        tvTime = findViewById(R.id.tvTime);
        tvWeeks = findViewById(R.id.tvWeeks);
        rvAssignments = findViewById(R.id.rvAssignments);
        rvExams = findViewById(R.id.rvExams);

        // 找到可能的"无数据"提示
        tvNoAssignments = findViewById(R.id.tvNoAssignments);
        tvNoExams = findViewById(R.id.tvNoExams);

        if (tvNoAssignments != null) {
            tvNoAssignments.setVisibility(View.VISIBLE);
        }

        if (tvNoExams != null) {
            tvNoExams.setVisibility(View.VISIBLE);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.course_detail);
        }
    }

    private void loadCourseDetails(int courseId) {
        // 首先尝试从本地缓存获取
        List<CourseSchedule> localCourses = dataSyncManager.getLocalCourseSchedules();

        if (localCourses == null || localCourses.isEmpty()) {
            Log.e(TAG, "本地课程列表为空");
            showError("无法获取课程数据");
            return;
        }

        Log.d(TAG, "从本地缓存获取课程，共 " + localCourses.size() + " 条数据");

        for (CourseSchedule course : localCourses) {
            if (course.getId() == courseId) {
                Log.d(TAG, "在本地缓存中找到ID=" + courseId + "的课程: " + course.getCourseName());
                displayCourseInfo(course);
                return;
            }
        }

        // 如果本地缓存没有，显示错误信息
        Log.e(TAG, "在本地缓存中没有找到ID=" + courseId + "的课程");
        showError("无法加载课程信息，请确保网络连接正常");
    }

    private void displayCourseInfo(TimetableCourse course) {
        try {
            // 检查关键字段是否为null
            String courseName = course.getName();
            String teacher = course.getTeacher();
            String classroom = course.getClassroom();

            if (courseName == null) {
                Log.w(TAG, "课程名称为null，使用默认值");
                courseName = "未命名课程";
            }

            if (teacher == null) {
                Log.w(TAG, "教师为null，使用默认值");
                teacher = "未知教师";
            }

            if (classroom == null) {
                Log.w(TAG, "教室为null，使用默认值");
                classroom = "未知教室";
            }

            tvCourseName.setText(courseName);
            tvTeacher.setText(teacher);
            tvClassroom.setText(classroom);

            // 格式化上课时间
            String weekday = getWeekdayString(course.getWeekday());
            String time = String.format("%s 第%d-%d节", weekday, course.getStartSection(), course.getEndSection());
            tvTime.setText(time);

            // 格式化周数
            String weeks = String.format("第%d-%d周", course.getStartWeek(), course.getEndWeek());
            tvWeeks.setText(weeks);

            Log.d(TAG, "成功显示TimetableCourse信息: " + courseName);
        } catch (Exception e) {
            Log.e(TAG, "显示TimetableCourse信息时出错", e);
            showError("显示课程信息时出错: " + e.getMessage());
        }
    }

    private void displayCourseInfo(CourseSchedule course) {
        try {
            // 检查关键字段是否为null
            String courseName = course.getCourseName();
            String teacherName = course.getTeacherName();
            String classroom = course.getClassroom();
            String classTime = course.getClassTime();

            if (courseName == null) {
                Log.w(TAG, "课程名称为null，使用默认值");
                courseName = "未命名课程";
            }

            if (teacherName == null) {
                Log.w(TAG, "教师为null，使用默认值");
                teacherName = "未知教师";
            }

            if (classroom == null) {
                Log.w(TAG, "教室为null，使用默认值");
                classroom = "未知教室";
            }

            if (classTime == null) {
                Log.w(TAG, "上课时间为null，使用默认值");
                classTime = "未知时间";
            }

            tvCourseName.setText(courseName);
            tvTeacher.setText(teacherName);
            tvClassroom.setText(classroom);
            tvTime.setText(classTime);
            tvWeeks.setText("第1-20周"); // 假设为整学期

            Log.d(TAG, "成功显示CourseSchedule信息: " + courseName);
        } catch (Exception e) {
            Log.e(TAG, "显示CourseSchedule信息时出错", e);
            showError("显示课程信息时出错: " + e.getMessage());
        }
    }

    private void displayCourseStrings(String courseName, String teacher, String classroom, String time, String weeks) {
        try {
            if (courseName == null)
                courseName = "未命名课程";
            if (teacher == null)
                teacher = "未知教师";
            if (classroom == null)
                classroom = "未知教室";
            if (time == null)
                time = "未知时间";
            if (weeks == null)
                weeks = "未知周次";

            tvCourseName.setText(courseName);
            tvTeacher.setText(teacher);
            tvClassroom.setText(classroom);
            tvTime.setText(time);
            tvWeeks.setText(weeks);

            Log.d(TAG, "成功显示课程字符串信息: " + courseName);
        } catch (Exception e) {
            Log.e(TAG, "显示课程字符串信息时出错", e);
            showError("显示课程信息时出错: " + e.getMessage());
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
                return "未知";
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "错误: " + message);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}