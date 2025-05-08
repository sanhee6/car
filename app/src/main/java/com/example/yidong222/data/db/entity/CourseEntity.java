package com.example.yidong222.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "courses")
public class CourseEntity {

    @PrimaryKey
    public int id;
    public String name;
    public String code;
    public String teacher;
    public String semester;
    public int credits;
    public String classroom;
    public int weekday;
    public int startSection;
    public int endSection;
    public int startWeek;
    public int endWeek;
    public String semesterId;

    public CourseEntity() {
    }

    @Ignore
    public CourseEntity(int id, String name, String code, String teacher, String semester,
            int credits, String classroom, int weekday, int startSection, int endSection,
            int startWeek, int endWeek, String semesterId) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.teacher = teacher;
        this.semester = semester;
        this.credits = credits;
        this.classroom = classroom;
        this.weekday = weekday;
        this.startSection = startSection;
        this.endSection = endSection;
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

    public String getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(String semesterId) {
        this.semesterId = semesterId;
    }
}