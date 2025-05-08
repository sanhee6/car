package com.example.yidong222.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.yidong222.data.db.entity.ExamEntity;

import java.util.List;

@Dao
public interface ExamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ExamEntity exam);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ExamEntity> exams);

    @Update
    void update(ExamEntity exam);

    @Delete
    void delete(ExamEntity exam);

    @Query("SELECT * FROM exams WHERE id = :examId")
    ExamEntity getExamById(int examId);

    @Query("SELECT * FROM exams WHERE courseId = :courseId")
    List<ExamEntity> getExamsByCourseId(int courseId);

    @Query("SELECT * FROM exams WHERE examDate BETWEEN :startDate AND :endDate")
    List<ExamEntity> getExamsByDateRange(String startDate, String endDate);

    @Query("DELETE FROM exams WHERE courseId = :courseId")
    void deleteByCourse(int courseId);

    @Query("DELETE FROM exams")
    void deleteAll();

    @Query("SELECT * FROM exams")
    List<ExamEntity> getAllExams();
}