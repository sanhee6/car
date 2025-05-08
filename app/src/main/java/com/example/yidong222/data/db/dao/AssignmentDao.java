package com.example.yidong222.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.yidong222.data.db.entity.AssignmentEntity;

import java.util.List;

@Dao
public interface AssignmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AssignmentEntity assignment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AssignmentEntity> assignments);

    @Update
    void update(AssignmentEntity assignment);

    @Delete
    void delete(AssignmentEntity assignment);

    @Query("SELECT * FROM assignments WHERE id = :assignmentId")
    AssignmentEntity getAssignmentById(int assignmentId);

    @Query("SELECT * FROM assignments WHERE courseId = :courseId")
    List<AssignmentEntity> getAssignmentsByCourseId(int courseId);

    @Query("SELECT * FROM assignments WHERE status = :status")
    List<AssignmentEntity> getAssignmentsByStatus(String status);

    @Query("DELETE FROM assignments WHERE courseId = :courseId")
    void deleteByCourse(int courseId);

    @Query("DELETE FROM assignments")
    void deleteAll();

    @Query("SELECT * FROM assignments")
    List<AssignmentEntity> getAllAssignments();
}