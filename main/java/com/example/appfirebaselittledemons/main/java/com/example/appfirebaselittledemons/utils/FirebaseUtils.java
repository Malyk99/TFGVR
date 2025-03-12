package com.example.appfirebaselittledemons.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {

    // Generar un código de sala aleatorio de 6 dígitos
    public static String generateRoomCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    // Obtener referencia a Firebase Database
    public static DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    // Obtener referencia específica a una sala
    public static DatabaseReference getRoomReference(String roomCode) {
        return getDatabaseReference().child("rooms").child(roomCode);
    }
}
