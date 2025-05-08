package com.example.yidong222.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class GradeStatsDto implements Serializable {
    private Stats stats;
    private List<AssignmentStat> assignments;

    public GradeStatsDto() {
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public List<AssignmentStat> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<AssignmentStat> assignments) {
        this.assignments = assignments;
    }

    // 内部类：统计信息
    public static class Stats implements Serializable {
        @SerializedName("average_score")
        private Double averageScore;

        @SerializedName("max_score")
        private Double maxScore;

        @SerializedName("min_score")
        private Double minScore;

        @SerializedName("total_grades")
        private Integer totalGrades;

        public Stats() {
        }

        public Double getAverageScore() {
            return averageScore;
        }

        public void setAverageScore(Double averageScore) {
            this.averageScore = averageScore;
        }

        public Double getMaxScore() {
            return maxScore;
        }

        public void setMaxScore(Double maxScore) {
            this.maxScore = maxScore;
        }

        public Double getMinScore() {
            return minScore;
        }

        public void setMinScore(Double minScore) {
            this.minScore = minScore;
        }

        public Integer getTotalGrades() {
            return totalGrades;
        }

        public void setTotalGrades(Integer totalGrades) {
            this.totalGrades = totalGrades;
        }
    }

    // 内部类：作业统计
    public static class AssignmentStat implements Serializable {
        private Integer id;
        private String title;

        @SerializedName("average_score")
        private Double averageScore;

        private Integer submissions;

        public AssignmentStat() {
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Double getAverageScore() {
            return averageScore;
        }

        public void setAverageScore(Double averageScore) {
            this.averageScore = averageScore;
        }

        public Integer getSubmissions() {
            return submissions;
        }

        public void setSubmissions(Integer submissions) {
            this.submissions = submissions;
        }
    }
}