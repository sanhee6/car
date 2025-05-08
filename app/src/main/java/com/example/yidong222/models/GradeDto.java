package com.example.yidong222.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class GradeDto implements Serializable {
    private Integer id;

    @SerializedName("student_id")
    private String studentId;

    @SerializedName("course_id")
    private Integer courseId;

    @SerializedName("course_name")
    private String courseName;

    @SerializedName("assignment_id")
    private Integer assignmentId;

    @SerializedName("assignment_title")
    private String assignmentTitle;

    @SerializedName("exam_id")
    private Integer examId;

    private Double score;

    @SerializedName("grade_type")
    private String gradeType;

    @SerializedName("submission_date")
    private String submissionDate;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public GradeDto() {
    }

    public GradeDto(String studentId, Integer courseId, Integer assignmentId, Integer examId, Double score,
            String gradeType) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.assignmentId = assignmentId;
        this.examId = examId;
        this.score = score;
        this.gradeType = gradeType;
    }

    // 将GradeDto转换为Grade对象，用于应用内部使用
    public Grade toGrade() {
        // 获取课程类型，根据API返回的gradeType决定
        String courseType = "必修";
        if ("elective".equals(this.gradeType)) {
            courseType = "选修";
        } else if ("general".equals(this.gradeType)) {
            courseType = "通识";
        }

        // 获取学期从API返回的数据，如果没有则默认使用当前学期
        String semester = this.submissionDate != null ? formatSemester(this.submissionDate) : "2023-1";

        // 计算学分，如果API没有提供则默认为3.0
        double credit = 3.0; // 实际应用中应该从课程信息中获取

        Grade grade = new Grade(
                courseName != null ? courseName : "未知课程",
                courseType,
                semester,
                score != null ? score : 0.0,
                credit);
        grade.setId(id != null ? id.toString() : "");
        return grade;
    }

    // 从日期格式化学期
    private String formatSemester(String date) {
        if (date == null || date.isEmpty()) {
            return "2023-1";
        }

        try {
            // 简单处理：根据日期获取学年和学期
            // 假设格式为：yyyy-MM-dd 或 yyyy-MM-ddTHH:mm:ss.SSSZ
            String[] parts = date.split("[-T]");
            if (parts.length >= 2) {
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);

                // 上半年为第二学期，下半年为第一学期
                if (month >= 2 && month <= 7) {
                    return (year - 1) + "-" + year + "学年第二学期";
                } else {
                    return year + "-" + (year + 1) + "学年第一学期";
                }
            }
        } catch (Exception e) {
            // 解析出错，返回默认值
        }

        return "2023-1";
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Integer assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getAssignmentTitle() {
        return assignmentTitle;
    }

    public void setAssignmentTitle(String assignmentTitle) {
        this.assignmentTitle = assignmentTitle;
    }

    public Integer getExamId() {
        return examId;
    }

    public void setExamId(Integer examId) {
        this.examId = examId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getGradeType() {
        return gradeType;
    }

    public void setGradeType(String gradeType) {
        this.gradeType = gradeType;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
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