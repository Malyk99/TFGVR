package com.example.appfirebaselittledemons.models;

import java.util.ArrayList;
import java.util.List;

public class Rooms {
    private int id;
    private List<Players> players;

    // Constructor vacío requerido por Firebase
    public Rooms() {}

    public Rooms(int id) {
        this.id = id;
        this.players = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Players> getPlayers() {
        return players;
    }

    public void setPlayers(List<Players> players) {
        this.players = players;
    }

    public void addPlayer(Players player) {
        if (players == null) {
            players = new ArrayList<>();
        }
        players.add(player);
    }
}
