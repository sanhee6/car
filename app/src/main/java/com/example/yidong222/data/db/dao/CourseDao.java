package com.example.yidong222.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.yidong222.data.db.entity.CourseEntity;

import java.util.List;

@Dao
public interface CourseDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(CourseEntity course);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertAll(List<CourseEntity> courses);

        @Update
        void update(CourseEntity course);

        @Delete
        void delete(CourseEntity course);

        @Query("SELECT * FROM courses")
        List<CourseEntity> getAllCourses();

        @Query("SELECT * FROM courses WHERE id = :courseId")
        CourseEntity getCourseById(int courseId);

        @Query("SELECT * FROM courses WHERE semesterId = :semesterId")
        List<CourseEntity> getCoursesBySemester(String semesterId);

        @Query("SELECT * FROM courses WHERE semesterId = :semesterId AND " +
                        "startWeek <= :week AND endWeek >= :week")
        List<CourseEntity> getCoursesByWeek(String semesterId, int week);

        @Query("SELECT * FROM courses WHERE semesterId = :semesterId AND " +
                        "weekday = :weekday AND " +
                        "startWeek <= :week AND endWeek >= :week")
        List<CourseEntity> getCoursesByWeekAndDay(String semesterId, int week, int weekday);

        @Query("DELETE FROM courses WHERE semesterId = :semesterId")
        void deleteBySemester(String semesterId);

        @Query("DELETE FROM courses")
        void deleteAll();
}