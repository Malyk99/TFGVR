package com.example.appfirebaselittledemons.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Rooms {
    private int id;
    private HashMap<String, Players> players;

    // Constructor vacío requerido por Firebase
    public Rooms() {}

    public Rooms(int id) {
        this.id = id;
        this.players = new HashMap<>();
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public HashMap<String, Players> getPlayers() { return players; }
    public void setPlayers(HashMap<String, Players> players) { this.players = players; }

    /*public void addPlayer(Players player) {
        if (players == null) {
            players = new ArrayList<>();
        }
        players.add(player);
    }*/
}
