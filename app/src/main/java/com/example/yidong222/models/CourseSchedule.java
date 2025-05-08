package com.example.yidong222.models;

import java.util.Date;
import com.google.gson.annotations.SerializedName;

public class CourseSchedule {
    private int id;

    @SerializedName("course_name")
    private String courseName;

    @SerializedName("teacher_name")
    private String teacherName;

    @SerializedName("class_time")
    private String classTime;

    private String classroom;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("updated_at")
    private Date updatedAt;

    public CourseSchedule(int id, String courseName, String teacherName, String classTime, String classroom,
            Date createdAt, Date updatedAt) {
        this.id = id;
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.classTime = classTime;
        this.classroom = classroom;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}