package com.example.projects.interfaces;

import java.util.Date;

//Интерфейс для обработки событий клика на элементы цели.

public interface GoalClickListener {
    /**
     * Вызывается при клике на редактирование цели.
     *
     * @param categoryId  Идентификатор категории, к которой относится цель.
     * @param goalId      Идентификатор цели.
     * @param name        Название цели.
     * @param targetSum   Целевая сумма.
     * @param date        Дата цели.
     */
    void editClick(int categoryId, int goalId, String name, int targetSum, Date date);

    /**
     * Вызывается при клике на удаление цели.
     *
     * @param goalId Идентификатор цели.
     */
    void deleteClick(int goalId);

    /**
     * Вызывается при клике на добавление суммы к цели.
     *
     * @param goalId Идентификатор цели.
     */
    void addClick(int goalId);
}

