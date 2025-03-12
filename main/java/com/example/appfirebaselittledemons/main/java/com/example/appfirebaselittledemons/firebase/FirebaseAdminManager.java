package com.example.appfirebaselittledemons.firebase;

import androidx.annotation.NonNull;
import com.google.firebase.database.*;

public class FirebaseAdminManager {
    private final DatabaseReference adminsRef;

    public FirebaseAdminManager() {
        adminsRef = FirebaseDatabase.getInstance().getReference("Admins");
    }

    /** Verifica si el usuario es administrador con el código de sala correcto */
    public void validateAdminLogin(String adminUsername, String enteredRoomCode, OnAdminLoginListener listener) {
        adminsRef.child(adminUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String storedRoomCode = snapshot.getValue(String.class);
                    if (storedRoomCode != null && storedRoomCode.equals(enteredRoomCode)) {
                        listener.onSuccess();
                    } else {
                        listener.onFailure("Código incorrecto para el administrador.");
                    }
                } else {
                    listener.onFailure("No eres un administrador registrado.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure("Error de base de datos: " + error.getMessage());
            }
        });
    }

    /** Interfaz para manejar el resultado del inicio de sesión */
    public interface OnAdminLoginListener {
        void onSuccess();
        void onFailure(String error);
    }

}
