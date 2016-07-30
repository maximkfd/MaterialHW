package com.example.alarm.materialhw;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private Rating currentRating = new Rating();
    private List<Rating> ratings = new ArrayList<>();
    private String group;
    public static User instance;

    public static User getInstance(){
        if (instance == null){
            instance = new User();
        }
        return instance;
    }
    public User(){}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Rating getCurrentRating() {
        return currentRating;
    }

    public void setCurrentRating(Rating currentRating) {
        this.currentRating = currentRating;
    }
}
