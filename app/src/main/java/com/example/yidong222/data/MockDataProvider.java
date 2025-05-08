package com.example.yidong222.data;

import com.example.yidong222.models.Assignment;
import com.example.yidong222.models.Exam;
import com.example.yidong222.models.Grade;
import com.example.yidong222.models.Course;
import com.example.yidong222.models.CourseSchedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 提供模拟数据，用于在网络不可用时展示
 */
public class MockDataProvider {

    /**
     * 获取模拟课程数据
     * 
     * @return 课程列表
     */
    public static List<Course> getMockCourses() {
        List<Course> courses = new ArrayList<>();
        courses.add(new Course("Java程序设计", "教学楼A101", "周二 1-2节"));
        courses.add(new Course("数据结构", "教学楼B203", "周三 3-4节"));
        courses.add(new Course("计算机网络", "教学楼C305", "周四 5-6节"));
        courses.add(new Course("操作系统", "教学楼D407", "周五 7-8节"));
        return courses;
    }

    /**
     * 获取模拟作业数据
     * 
     * @return 作业列表
     */
    public static List<Assignment> getMockAssignments() {
        List<Assignment> assignments = new ArrayList<>();
        assignments.add(new Assignment("Java实验一", "Java程序设计", "2023-12-30T23:59:59.000Z", "完成Java基础语法练习", false));
        assignments.add(new Assignment("数据结构实验二", "数据结构", "2023-12-25T23:59:59.000Z", "完成链表和队列的实现", true));
        assignments.add(new Assignment("网络编程作业", "计算机网络", "2024-01-05T23:59:59.000Z", "完成Socket编程实验", false));
        assignments.add(new Assignment("操作系统实验", "操作系统", "2024-01-10T23:59:59.000Z", "完成进程调度模拟", false));
        return assignments;
    }

    /**
     * 获取模拟考试数据
     * 
     * @return 考试列表
     */
    public static List<Exam> getMockExams() {
        List<Exam> exams = new ArrayList<>();
        exams.add(new Exam("Java程序设计", "期末考试", "2024-01-15", "14:00", "教学楼A101", "A-123"));
        exams.add(new Exam("数据结构", "期末考试", "2024-01-17", "09:00", "教学楼B203", "B-456"));
        exams.add(new Exam("计算机网络", "期中考试", "2023-12-01", "14:00", "教学楼C305", "C-789"));
        exams.add(new Exam("操作系统", "期末考试", "2024-01-20", "09:00", "教学楼D407", "D-101"));
        return exams;
    }

    /**
     * 获取模拟成绩数据
     * 
     * @return 成绩列表
     */
    public static List<Grade> getMockGrades() {
        List<Grade> grades = new ArrayList<>();
        grades.add(new Grade("Java程序设计", "必修", "2023-2024-1", 88, 4.0));
        grades.add(new Grade("数据结构", "必修", "2023-2024-1", 92, 4.0));
        grades.add(new Grade("计算机网络", "必修", "2023-2024-1", 78, 3.0));
        grades.add(new Grade("操作系统", "必修", "2023-2024-1", 85, 3.0));
        return grades;
    }

    /**
     * 获取模拟课程表数据
     * 
     * @return 课程表列表
     */
    public static List<CourseSchedule> getMockCourseSchedules() {
        List<CourseSchedule> schedules = new ArrayList<>();
        Date now = new Date();
        schedules.add(new CourseSchedule(1, "Java程序设计", "张教授", "周二 1-2节", "教学楼A101", now, now));
        schedules.add(new CourseSchedule(2, "数据结构", "李教授", "周三 3-4节", "教学楼B203", now, now));
        schedules.add(new CourseSchedule(3, "计算机网络", "王教授", "周四 5-6节", "教学楼C305", now, now));
        schedules.add(new CourseSchedule(4, "操作系统", "赵教授", "周五 7-8节", "教学楼D407", now, now));
        return schedules;
    }
}