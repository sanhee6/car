package com.example.yidong222.models;

import java.io.Serializable;

public class Exam implements Serializable {
    private int id;
    private int courseId;
    private String courseName;
    private String title;
    private String description;
    private String date;
    private String time;
    private String location;
    private String seatNumber;
    private String status; // 未考/已考
    private String examName; // 兼容旧代码

    public Exam(String courseName, String title, String date, String time, String location, String seatNumber) {
        this.courseName = courseName;
        this.title = title;
        this.examName = title; // 兼容旧代码
        this.date = date;
        this.time = time;
        this.location = location;
        this.seatNumber = seatNumber;
        this.status = "未考";
        this.id = (int) System.currentTimeMillis(); // 为兼容旧代码设置ID
    }

    public Exam(String courseName, int id, int courseId, String title, String description, String date, String time,
            String location) {
        this.courseName = courseName;
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.examName = title; // 兼容旧代码
        this.description = description;
        this.date = date;
        this.time = time;
        this.location = location;
        this.status = "未考";
    }

    public String getExamName() {
        return title;
    }

    public void setExamName(String examName) {
        this.title = examName;
        this.examName = examName;
    }

    public String getIdAsString() {
        return String.valueOf(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
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
        this.examName = title; // 保持同步
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}