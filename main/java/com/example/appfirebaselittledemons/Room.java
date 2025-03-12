package com.example.appfirebaselittledemons;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Room implements Serializable {
    public int id;
    public List<Player> players;
    public int playerCount; // ✅ New field to store number of players

    public Room() {
        this.players = new ArrayList<>();
    }

    public Room(int id) {
        this.id = id;
        this.players = new ArrayList<>();
    }

    public void setPlayerCount(int count) {
        this.playerCount = count;
    }

    public int getPlayerCount() {
        return playerCount;
    }
}

