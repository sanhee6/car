package com.example.yidong222.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.yidong222.data.db.dao.AssignmentDao;
import com.example.yidong222.data.db.dao.CourseDao;
import com.example.yidong222.data.db.dao.ExamDao;
import com.example.yidong222.data.db.entity.AssignmentEntity;
import com.example.yidong222.data.db.entity.CourseEntity;
import com.example.yidong222.data.db.entity.ExamEntity;

@Database(entities = {
        CourseEntity.class,
        ExamEntity.class,
        AssignmentEntity.class
}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "course_db";
    private static AppDatabase instance;

    public abstract CourseDao courseDao();

    public abstract ExamDao examDao();

    public abstract AssignmentDao assignmentDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME).fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}