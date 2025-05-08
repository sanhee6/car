package com.example.yidong222.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "assignments")
public class AssignmentEntity {
    @PrimaryKey
    public int id;
    public int courseId;
    public String title;
    public String description;
    public String dueDate;
    public String status;
}