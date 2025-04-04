package com.valeriia.pet_app.model;

import java.util.Date;

public class Note {
    private String title;
    private String description;
    private Date date;
    private int userId; // New field


    // Пустой конструктор, необходим для Firestore
    public Note() {}


    public Note(String title, String description, Date date, int userId) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.userId = userId; // Initialize the userId field
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public int getUserId() {  // Getter for userId
        return userId;
    }
}
