package com.example.appfirebaselittledemons.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import com.google.firebase.database.*;

public class FirebaseAdminManager {
    private final DatabaseReference adminsRef;
    private final DatabaseReference roomsRef;

    public FirebaseAdminManager() {
        adminsRef = FirebaseDatabase.getInstance().getReference("admins");
        roomsRef = FirebaseDatabase.getInstance().getReference("rooms");
    }

    /** ✅ Validate if a user is an admin and if the room code matches */
    public void validateAdminLogin(String adminUsername, String enteredPassword, OnAdminLoginListener listener) {
        Log.d("FirebaseDEBUG", "Checking admin login for: " + adminUsername);

        adminsRef.child(adminUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object raw = snapshot.getValue();
                    Log.d("FirebaseDEBUG", "Snapshot raw value: " + raw);

                    String storedPassword = snapshot.getValue(String.class);
                    Log.d("FirebaseDEBUG", "Parsed password: " + storedPassword);

                    if (storedPassword != null && storedPassword.equals(enteredPassword)) {
                        Log.d("FirebaseDEBUG", "Admin login success");
                        listener.onSuccess();
                    } else {
                        Log.e("FirebaseDEBUG", "Incorrect password for admin");
                        listener.onFailure("Incorrect admin password.");
                    }
                } else {
                    Log.e("FirebaseDEBUG", "Admin user not found");
                    listener.onFailure("You are not a registered admin.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseDEBUG", "Database error: " + error.getMessage());
                listener.onFailure("Database error: " + error.getMessage());
            }
        });
    }




    /** ✅ Delete a specific room (Admin already validated) */
    public void deleteRoom(String roomCode, OnRoomDeleteListener listener) {
        roomsRef.child(roomCode).removeValue()
                .addOnSuccessListener(aVoid -> listener.onDeleteSuccess("Sala eliminada correctamente."))
                .addOnFailureListener(e -> listener.onDeleteFailure("Error al eliminar la sala: " + e.getMessage()));
    }

    /** ✅ Delete all rooms from Firebase */
    public void deleteAllRooms(OnRoomDeleteListener listener) {
        roomsRef.removeValue()
                .addOnSuccessListener(aVoid -> listener.onDeleteSuccess("Todas las salas han sido eliminadas."))
                .addOnFailureListener(e -> listener.onDeleteFailure("Error al eliminar todas las salas: " + e.getMessage()));
    }

    /** ✅ Listener for Admin Login */
    public interface OnAdminLoginListener {
        void onSuccess();
        void onFailure(String error);
    }

    /** ✅ Listener for Room Deletion */
    public interface OnRoomDeleteListener {
        void onDeleteSuccess(String message);
        void onDeleteFailure(String error);
    }
}
