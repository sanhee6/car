package com.example.yidong222.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exams")
public class ExamEntity {
    @PrimaryKey
    public int id;
    public int courseId;
    public String examName;
    public String examDate;
    public String examTime;
    public String location;
    public String type;
}