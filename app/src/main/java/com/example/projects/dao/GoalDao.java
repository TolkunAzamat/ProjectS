package com.example.projects.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projects.models.Category;
import com.example.projects.models.Goal;

import java.util.List;
@Dao
public interface GoalDao {
    // Получить все цели
    @Query("SELECT * FROM goal")
    List<Goal> getAllGoal();

    // Получить список имен целей
    @Query("SELECT name From goal")
    List<String> getGoals();

    // Вставить новую цель в базу данных
    @Insert
    void insertGoal(Goal goal);

    // Получить список целей по идентификатору категории
    @Query("SELECT * FROM goal WHERE idCategory = :categoryId")
    List<Goal> getGoalsByCategory(int categoryId);

    // Получить все цели
    @Query("SELECT * FROM goal")
    List<Goal> getGoal();

    // Получить идентификатор цели по её имени
    @Query("SELECT id FROM goal WHERE name = :name")
    int getGoalIdByName(String name);

    // Получить цель по идентификатору
    @Query("SELECT * FROM goal WHERE id = :goalId")
    Goal getGoalById(int goalId);

    // Обновить информацию о цели
    @Update
    void updateGoal(Goal goal);

    // Удалить цель из базы данных
    @Delete
    void deleteGoal(Goal goal);
}