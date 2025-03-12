package com.example.appfirebaselittledemons.models;

public class Players {
    private String id;
    private String name;
    private boolean ready;

    // Constructor vacío requerido por Firebase
    public Players() {}

    public Players(String id, String name, boolean ready) {
        this.id = id;
        this.name = name;
        this.ready = ready;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
