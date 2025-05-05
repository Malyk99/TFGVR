package com.example.appfirebaselittledemons.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseRepository {
    private final DatabaseReference databaseReference;

    public FirebaseRepository() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    /** Referencia a todas las salas */
    public DatabaseReference getRoomsReference() {
        return databaseReference.child("rooms");
    }

    /** Referencia a los jugadores en una sala específica */
    public DatabaseReference getPlayersReference(String roomCode) {
        return databaseReference.child("rooms").child(roomCode).child("players");
    }

    /** Referencia a los minijuegos */
    public DatabaseReference getMinigamesReference(String roomCode) {
        return databaseReference.child("rooms").child(roomCode).child("minijuegos");
    }
}
