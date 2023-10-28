package com.example.projects.utils;

import android.app.Application;

import androidx.room.Room;
//Класс приложения, расширяющий класс Application.
public class App extends Application {
    private static App instance;
    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Инициализируем базу данных Room при запуске приложения
        database = Room.databaseBuilder(this, AppDatabase.class, "database").build();
    }

    /**
     * Получение экземпляра приложения.
     *
     * @return Экземпляр приложения.
     */
    public static App getInstance() {
        return instance;
    }

    /**
     * Получение базы данных приложения.
     *
     * @return Объект базы данных.
     */
    public AppDatabase getDatabase() {
        return database;
    }
}