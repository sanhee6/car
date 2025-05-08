package com.example.yidong222.models;

import java.io.Serializable;

public class Assignment implements Serializable {
    private int id;
    private int courseId;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private String status;
    private String courseName;
    private boolean completed;

    public Assignment(String title, String startDate, String endDate, String status) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.completed = "完成".equals(status);
        this.id = (int) System.currentTimeMillis();
    }

    public Assignment(int id, int courseId, String title, String description, String startDate, String endDate,
            String status) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.completed = "完成".equals(status);
    }

    public Assignment(String title, String courseName, String deadline, String description, boolean isNew) {
        this.title = title;
        this.courseName = courseName;
        this.startDate = deadline;
        this.endDate = deadline;
        this.description = description;
        this.completed = false;
        this.status = "未完成";
        this.id = (int) System.currentTimeMillis();
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDeadline() {
        return endDate;
    }

    public void setDeadline(String deadline) {
        this.endDate = deadline;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        this.status = completed ? "完成" : "未完成";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdAsString() {
        return String.valueOf(id);
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.completed = "完成".equals(status);
    }
}