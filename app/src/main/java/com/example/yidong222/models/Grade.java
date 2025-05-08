package com.example.yidong222.models;

import java.io.Serializable;

public class Grade implements Serializable {
    private String id;
    private String courseName;
    private String courseType; // 必修/选修
    private String semester;
    private double score;
    private double credit;
    private String gpa; // 绩点

    public Grade(String courseName, String courseType, String semester, double score, double credit) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.courseName = courseName;
        this.courseType = courseType;
        this.semester = semester;
        this.score = score;
        this.credit = credit;
        calculateGpa();
    }

    private void calculateGpa() {
        // 简单的绩点计算逻辑
        if (score >= 90) {
            gpa = "4.0";
        } else if (score >= 80) {
            gpa = "3.0";
        } else if (score >= 70) {
            gpa = "2.0";
        } else if (score >= 60) {
            gpa = "1.0";
        } else {
            gpa = "0.0";
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
        calculateGpa();
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public String getGpa() {
        return gpa;
    }
}