package com.example.yidong222.data.api.response;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CourseResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("teacher")
    private String teacher;

    @SerializedName("classroom")
    private String classroom;

    @SerializedName("weekday")
    private String weekday;

    @SerializedName("start_section")
    private int startSection;

    @SerializedName("end_section")
    private int endSection;

    @SerializedName("start_week")
    private int startWeek;

    @SerializedName("end_week")
    private int endWeek;

    private List<Course> courses;

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

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
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

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public static class Course {
        private int id;
        private String name;
        private String code;
        private String teacher;
        private String semester;
        private int credits;
        private String classroom;
        private int weekday;
        private int startSection;
        private int endSection;
        private int startWeek;
        private int endWeek;
        private String semesterId;

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

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getTeacher() {
            return teacher;
        }

        public void setTeacher(String teacher) {
            this.teacher = teacher;
        }

        public String getSemester() {
            return semester;
        }

        public void setSemester(String semester) {
            this.semester = semester;
        }

        public int getCredits() {
            return credits;
        }

        public void setCredits(int credits) {
            this.credits = credits;
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
}