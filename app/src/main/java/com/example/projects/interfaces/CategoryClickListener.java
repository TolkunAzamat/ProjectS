package com.example.projects.interfaces;

// Интерфейс для обработки событий клика на элементы категории.

public interface CategoryClickListener {
    // Вызывается при клике на изображение категории.

    void onImageClick(int categoryId);

    //Вызывается при клике на текстовое название категории.

    void onTextClick(int categoryId, String categoryName);

   //Вызывается при клике на иконку удаления категории.

    void onIconClick(int categoryId);
}



