package com.example.projects.utils;
import androidx.room.TypeConverter;
import java.util.Date;

// Класс-конвертер для преобразования типов между Date и Long.

public class DateTypeConverter {
    /**
     * Преобразование Long в объект Date.
     *
     * @param timestamp Long, представляющий метку времени.
     * @return Объект Date или null, если метка времени равна null.
     */
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    /**
     * Преобразование объекта Date в Long (метку времени).
     *
     * @param date Объект Date или null.
     * @return Long, представляющий метку времени, или null, если объект Date равен null.
     */
    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}


