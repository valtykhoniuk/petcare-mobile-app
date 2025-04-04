package com.valeriia.pet_app.model;

public class Pet {

    private String name;
    private int age;
    private String breed;
    private String gender;
    private int userId;  // User ID associated with the pet
    private double weight; // Added weight field

    public Pet(String name, int age, String breed, String gender, int userId, double weight) {
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.gender = gender;
        this.userId = userId;
        this.weight = weight; // Initialize weight
    }

    // Getter and setter for weight
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", breed='" + breed + '\'' +
                ", gender='" + gender + '\'' +
                ", userId=" + userId +
                ", weight=" + weight + // Include weight in the string representation
                '}';
    }
}
