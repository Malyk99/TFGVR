package com.example.appfirebaselittledemons.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    /**
     * ✅ Monitors if a player is still in the room. If removed, it exits the activity.
     * @param activity The current activity (for finishing it if needed).
     * @param roomCode The room ID.
     * @param userId The player ID.
     */
    public static void monitorPlayerStatus(Activity activity, String roomCode, String userId) {
        DatabaseReference playerRef = FirebaseDatabase.getInstance()
                .getReference("rooms")
                .child(roomCode)
                .child("players")
                .child(userId);

        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(activity, "You have been removed from the room!", Toast.LENGTH_LONG).show();
                    activity.finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseUtils", "Error checking player status", error.toException());
            }
        });
    }

}
