package com.hanshenrik.gronsleth_hasamishogi;

import android.media.Image;


public class Player {
    public final String name;
    public final String description;
    public int points;
    public final Image avatar;

    public Player(String name, String description, Image avatar) {
        this.name = name;
        this.description = description;
        this.points = 0;
        this.avatar = avatar;
    }
}
