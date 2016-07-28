package com.example.alarm.materialhw;

public class User {
    private String name;
    private String surname;
    private int rating;
    private String group;

    public User(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getRating() {
        return rating;
    }

}
