package com.example.yidong222.data.api.response;

public class AssignmentResponse {
    private int id;
    private int courseId;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private String status;

    public int getId() {
        return id;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }
}