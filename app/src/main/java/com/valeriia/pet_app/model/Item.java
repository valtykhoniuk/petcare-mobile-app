package com.valeriia.pet_app.model;

import com.google.firebase.Timestamp;

public class Item {
    private String text;
    private Timestamp customDate; // Храним дату как Timestamp
    private int userId; // Поле для userId

    public Item(String text, Timestamp customDate, int userId) {
        this.text = text;
        this.customDate = customDate; // Инициализируем поле customDate
        this.userId = userId; // Инициализируем поле userId
    }

    public String getText() {
        return text;
    }

    public Timestamp getCustomDate() {
        return customDate; // Возвращаем Timestamp
    }

    public int getUserId() {  // Getter для userId
        return userId;
    }
}
