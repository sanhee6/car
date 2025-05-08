package com.example.yidong222.models;

import java.io.Serializable;

public class TimetableCourse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String classroom;
    private String teacher;
    private int weekday; // 1-7，周一到周日
    private int startSection; // 1-12，第几节课开始
    private int endSection; // 1-12，第几节课结束
    private int startWeek; // 开始周
    private int endWeek; // 结束周
    private int databaseId; // 数据库中的ID，用于更新和删除操作

    public TimetableCourse(String name, String classroom, String teacher, int startSection, int endSection,
            int weekday, int startWeek, int endWeek) {
        this.name = name;
        this.classroom = classroom;
        this.teacher = teacher;
        this.weekday = weekday;
        this.startSection = startSection;
        this.endSection = endSection;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.databaseId = 0; // 默认值
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public int getStartSection() {
        return startSection;
    }

    public void setStartSection(int startSection) {
        this.startSection = startSection;
    }

    public int getEndSection() {
        return endSection;
    }

    public void setEndSection(int endSection) {
        this.endSection = endSection;
    }

    public int getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(int startWeek) {
        this.startWeek = startWeek;
    }

    public int getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(int endWeek) {
        this.endWeek = endWeek;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
    }
}