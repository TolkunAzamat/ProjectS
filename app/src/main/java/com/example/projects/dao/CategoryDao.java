package com.example.projects.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projects.models.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    // Получить все категории
    @Query("SELECT * FROM category")
    List<Category> getAllCategory();

    // Получить список имен категорий
    @Query("SELECT name From category")
    List<String> getCategories();

    // Получить идентификатор категории по её имени
    @Query("SELECT id FROM Category WHERE name = :name")
    int getCategoryIdByName(String name);

    // Вставить новую категорию в базу данных
    @Insert
    void insertCategory(Category category);

    // Получить категорию по идентификатору
    @Query("SELECT * FROM category WHERE id = :categoryId")
    Category getCategoryById(int categoryId);

    // Обновить информацию о категории
    @Update
    void updateCategory(Category category);

    // Удалить категорию из базы данных
    @Delete
    void deleteCategory(Category category);
}