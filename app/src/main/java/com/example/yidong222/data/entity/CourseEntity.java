package com.example.yidong222.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "courses")
public class CourseEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String teacher;
    private String classroom;
    private String classTime;
    private int startSection;
    private int endSection;
    private int weekday;
    private int startWeek;
    private int endWeek;
    private String semesterId;
    private String createdAt;
    private String updatedAt;

    public CourseEntity() {
    }

    public CourseEntity(int id, String name, String teacher, String classroom, String classTime,
            int startSection, int endSection, int weekday, int startWeek, int endWeek,
            String semesterId, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.teacher = teacher;
        this.classroom = classroom;
        this.classTime = classTime;
        this.startSection = startSection;
        this.endSection = endSection;
        this.weekday = weekday;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.semesterId = semesterId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public CourseEntity(int id, String name, String teacher, String classroom, String classTime,
            int startSection, int endSection, int weekday, int startWeek, int endWeek, String semesterId) {
        this.id = id;
        this.name = name;
        this.teacher = teacher;
        this.classroom = classroom;
        this.classTime = classTime;
        this.startSection = startSection;
        this.endSection = endSection;
        this.weekday = weekday;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.semesterId = semesterId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
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

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
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

    public String getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(String semesterId) {
        this.semesterId = semesterId;
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