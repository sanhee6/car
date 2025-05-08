package com.example.yidong222.models;

public class CourseDetail {
    private String name;
    private String teacher;
    private String classroom;
    private String time;
    private String weeks;

    public CourseDetail(String name, String teacher, String classroom, String time, String weeks) {
        this.name = name;
        this.teacher = teacher;
        this.classroom = classroom;
        this.time = time;
        this.weeks = weeks;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }
}