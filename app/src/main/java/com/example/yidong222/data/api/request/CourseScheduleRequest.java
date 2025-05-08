package com.example.yidong222.data.api.request;

public class CourseScheduleRequest {
    private String courseName;
    private String teacherName;
    private String classTime;
    private String classroom;

    public CourseScheduleRequest() {
    }

    public CourseScheduleRequest(String courseName, String teacherName, String classTime, String classroom) {
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.classTime = classTime;
        this.classroom = classroom;
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
}