package com.example.yidong222.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;

public class ExamDto implements Serializable {
    private Integer id;

    @SerializedName("course_id")
    private Integer courseId;

    @SerializedName("course_name")
    private String courseName;

    private String title;

    @SerializedName("exam_date")
    private String examDate;

    private Integer duration;

    private String location;

    private String description;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public ExamDto() {
    }

    public ExamDto(Integer courseId, String title, String examDate, Integer duration, String location,
            String description) {
        this.courseId = courseId;
        this.title = title;
        this.examDate = examDate;
        this.duration = duration;
        this.location = location;
        this.description = description;
    }

    // 将ExamDto转换为Exam对象，用于应用内部使用
    public Exam toExam() {
        Exam exam = new Exam(
                courseName != null ? courseName : "未知课程",
                title != null ? title : "未命名考试",
                examDate != null ? examDate.split("T")[0] : "", // 仅使用日期部分
                getFormattedTime(),
                location != null ? location : "",
                "" // 座位号在API中未提供
        );

        if (id != null) {
            exam.setId(id);
        }

        if (description != null) {
            exam.setDescription(description);
        }

        return exam;
    }

    // 获取格式化的时间字符串
    public String getFormattedTime() {
        if (examDate == null || !examDate.contains("T")) {
            return "";
        }
        try {
            String timePart = examDate.split("T")[1];
            return timePart.substring(0, 5); // 获取HH:MM格式
        } catch (Exception e) {
            return "";
        }
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}