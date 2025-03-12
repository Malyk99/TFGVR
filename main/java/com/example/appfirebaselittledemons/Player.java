package com.example.appfirebaselittledemons;

import java.io.Serializable;

public class Player implements Serializable {
    public String id;
    public String name;
    public boolean ready; // Add a boolean to track ready state

    // Default constructor (required for Firebase)
    public Player(String currentUsername, boolean b) {}

    public Player() {
    }

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.ready = false; // Default to Not Ready
    }
    // Constructor to initialize Player with ID and name
    public Player(String id, String name, boolean ready) {
        this.id = id;
        this.name = name;
        this.ready = ready;
    }
}

