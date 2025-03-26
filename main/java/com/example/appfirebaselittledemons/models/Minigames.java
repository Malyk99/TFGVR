package com.example.appfirebaselittledemons.models;

import java.util.HashMap;
import java.util.Map;

public class Minigames {
    private Map<String, Boolean> blockers;

    // Constructor vacío requerido por Firebase
    public Minigames() {
        this.blockers = new HashMap<>();
    }

    public Minigames(Map<String, Boolean> blockers) {
        this.blockers = blockers;
    }

    public Map<String, Boolean> getBlockers() {
        return blockers;
    }

    public void setBlockers(Map<String, Boolean> blockers) {
        this.blockers = blockers;
    }

    public void setBlockerState(String blocker, boolean state) {
        this.blockers.put(blocker, state);
    }
}
