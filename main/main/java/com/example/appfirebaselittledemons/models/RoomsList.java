package com.example.appfirebaselittledemons.models;

import java.util.List;

public class RoomsList {
    private List<Rooms> rooms;

    public RoomsList() {}

    public RoomsList(List<Rooms> rooms) {
        this.rooms = rooms;
    }

    public List<Rooms> getRooms() {
        return rooms;
    }

    public void setRooms(List<Rooms> rooms) {
        this.rooms = rooms;
    }
}
