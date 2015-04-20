package com.hanshenrik.gronsleth_hasamishogi;

public class Player {
    public final String name;
    public final String description;
    public final int id;
    public int points;
    public final String avatarURL;

    public Player(int id, String name, String description, int points, String avatarURL) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.points = points;
        this.avatarURL = avatarURL;
    }

    public String toString() {
        return this.name + ": " + this.points + "p";
    }
}
