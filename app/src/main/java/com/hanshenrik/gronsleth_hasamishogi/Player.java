package com.hanshenrik.gronsleth_hasamishogi;

import android.media.Image;

// TODO: maybe not needed, use PlayersProvider directly to store info instead?
public class Player {
    public final String name;
    public final String description;
    public final int id;
    public int points;
    public final Image avatar;

    public Player(int id, String name, String description, int points) {
        this(id, name, description, points, null); // TODO: set to default image instead of null
    }

    public Player(int id, String name, String description, int points, Image avatar) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.points = points;
        this.avatar = avatar;
    }

    public String toString() {
        return this.name + " (" + this.points + ")";
    }
}
