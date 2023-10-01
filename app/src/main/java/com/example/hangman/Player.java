package com.example.hangman;

public class Player {
    private String name;
    private long points;

    public Player() {
    }

    public Player(String name, long points) {
        this.name = name;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public long getPoints() {
        return points;
    }
}
