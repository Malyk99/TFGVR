package com.example.appfirebaselittledemons.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseRepository {
    private final DatabaseReference databaseReference;

    public FirebaseRepository() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }


    public DatabaseReference getRoomsReference() {
        return databaseReference.child("rooms");
    }


    public DatabaseReference getPlayersReference(String roomCode) {
        return databaseReference.child("rooms").child(roomCode).child("players");
    }

    public DatabaseReference getMinigamesReference(String roomCode) {
        return databaseReference.child("rooms").child(roomCode).child("minijuegos");
    }
}
