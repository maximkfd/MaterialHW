package com.example.alarm.materialhw;

public class Rating {
    int year;
    int max;
    int rating;
    String rate;
    String fuckulty;

    public Rating(){}

    public Rating(int year, int max, int rating, String fuckulty) {
        this.year = year;
        this.max = max;
        this.rating = rating;
        this.fuckulty = fuckulty;
    }

    public Rating(String rate, String fuckulty) {
        this.rate = rate;
        this.fuckulty = fuckulty;
    }
}
