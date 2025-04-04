package com.valeriia.pet_app.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Event {
    public static ArrayList<Event> eventsList = new ArrayList<>();
    private String name;
    private LocalDate date;
    private LocalTime time;
    private int userId; // New field

//    public static ArrayList<Event> eventsForDate(LocalDate date) {
//        ArrayList<Event> events = new ArrayList<>();
//
//        for (Event event : eventsList) {
//            if (event.getDate().equals(date))
//                events.add(event);
//        }
//
//        return events;
//    }

    public static ArrayList<Event> eventsForDateAndUser(LocalDate date, int userId) {
        ArrayList<Event> events = new ArrayList<>();

        for (Event event : eventsList) {
            if (event.getDate().equals(date) && event.getUserId() == userId) {
                events.add(event);
            }
        }

        return events;
    }

    public Event(String name, LocalDate date, LocalTime time, int userId) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.userId = userId; // Initialize the userId field
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public int getUserId() {  // Getter for userId
        return userId;
    }
}
