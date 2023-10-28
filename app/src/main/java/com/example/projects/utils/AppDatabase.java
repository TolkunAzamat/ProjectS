package com.example.projects.utils;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.projects.dao.CategoryDao;
import com.example.projects.dao.GoalDao;
import com.example.projects.models.Category;
import com.example.projects.models.Goal;
import androidx.room.TypeConverters;

 //Класс базы данных приложения, использующий Room.

@Database(entities = {Category.class, Goal.class}, version = 7, exportSchema = false)
@TypeConverters(DateTypeConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    // Абстрактные методы для получения Data Access Objects (DAO)
    public abstract CategoryDao categoryDao();
    public abstract GoalDao goalDao();
}
