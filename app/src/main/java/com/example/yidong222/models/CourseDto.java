package com.example.yidong222.models;

import com.google.gson.annotations.SerializedName;

public class CourseDto {

    private Integer id;
    private String name;
    private String teacher;
    private String classroom;
    private Integer weekday;

    @SerializedName("start_section")
    private Integer startSection;

    @SerializedName("end_section")
    private Integer endSection;

    @SerializedName("start_week")
    private Integer startWeek;

    @SerializedName("end_week")
    private Integer endWeek;

    @SerializedName("semester_id")
    private String semesterId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public CourseDto() {
    }

    public CourseDto(Integer id, String name, String teacher, String classroom, Integer weekday,
            Integer startSection, Integer endSection, Integer startWeek,
            Integer endWeek, String semesterId) {
        this.id = id;
        this.name = name;
        this.teacher = teacher;
        this.classroom = classroom;
        this.weekday = weekday;
        this.startSection = startSection;
        this.endSection = endSection;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.semesterId = semesterId;
    }

    public CourseDto(String name, String teacher, String classroom, Integer weekday,
            Integer startSection, Integer endSection, Integer startWeek,
            Integer endWeek, String semesterId) {
        this.name = name;
        this.teacher = teacher;
        this.classroom = classroom;
        this.weekday = weekday;
        this.startSection = startSection;
        this.endSection = endSection;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.semesterId = semesterId;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getWeekday() {
        return weekday;
    }

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
    }

    public Integer getStartSection() {
        return startSection;
    }

    public void setStartSection(Integer startSection) {
        this.startSection = startSection;
    }

    public Integer getEndSection() {
        return endSection;
    }

    public void setEndSection(Integer endSection) {
        this.endSection = endSection;
    }

    public Integer getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(Integer startWeek) {
        this.startWeek = startWeek;
    }

    public Integer getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(Integer endWeek) {
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

    // 转换为UI使用的Course对象
    public Course toCourse() {
        String dayText = "";
        switch (weekday) {
            case 1:
                dayText = "周一";
                break;
            case 2:
                dayText = "周二";
                break;
            case 3:
                dayText = "周三";
                break;
            case 4:
                dayText = "周四";
                break;
            case 5:
                dayText = "周五";
                break;
            case 6:
                dayText = "周六";
                break;
            case 7:
                dayText = "周日";
                break;
        }

        String timeText = dayText + " 第" + startSection + "-" + endSection + "节";
        String locationText = classroom != null ? classroom : "未安排教室";

        Course course = new Course(name, locationText, timeText);

        if (id != null) {
            course.setId(id);
        }

        course.setTeacher(teacher);
        course.setRoom(classroom);
        course.setDay(weekday);
        course.setStartSection(startSection);
        course.setEndSection(endSection);

        return course;
    }
}