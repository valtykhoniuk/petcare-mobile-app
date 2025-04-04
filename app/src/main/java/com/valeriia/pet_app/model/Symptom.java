package com.valeriia.pet_app.model;

public class Symptom {
    private String name;
    private boolean isSelected;

    public Symptom(String name) {
        this.name = name;
        this.isSelected = false;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
