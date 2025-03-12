package com.example.appfirebaselittledemons.firebase;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.appfirebaselittledemons.models.Players;
import com.example.appfirebaselittledemons.models.Rooms;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDataManager {
    private final FirebaseRepository firebaseRepository;

    public FirebaseDataManager() {
        firebaseRepository = new FirebaseRepository();
    }

    /** Leer las salas disponibles */
    public void fetchRooms(OnRoomsFetchedListener listener) {
        firebaseRepository.getRoomsReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Rooms> roomList = new ArrayList<>();
                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    Rooms room = roomSnapshot.getValue(Rooms.class);
                    if (room != null) {
                        roomList.add(room);
                    }
                }
                listener.onRoomsFetched(roomList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error al leer salas: " + error.getMessage());
            }
        });
    }

    /** Leer jugadores de una sala */
    public void fetchPlayers(String roomCode, OnPlayersFetchedListener listener) {
        firebaseRepository.getPlayersReference(roomCode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Players> playerList = new ArrayList<>();
                for (DataSnapshot playerSnapshot : snapshot.getChildren()) {
                    Players player = playerSnapshot.getValue(Players.class);
                    if (player != null) {
                        playerList.add(player);
                    }
                }
                listener.onPlayersFetched(playerList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error al leer jugadores: " + error.getMessage());
            }
        });
    }

    /** Insertar una nueva sala en Firebase */
    public void createRoom(Rooms room, OnDataInsertedListener listener) {
        firebaseRepository.getRoomsReference().child(String.valueOf(room.getId()))
                .setValue(room)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    /** Insertar un nuevo jugador en una sala */
    public void addPlayerToRoom(String roomCode, Players player, OnDataInsertedListener listener) {
        firebaseRepository.getPlayersReference(roomCode).child(player.getId())
                .setValue(player)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    /** Insertar datos en minijuegos */
    public void updateMinigameState(String roomCode, String minigame, String blocker, boolean state, OnDataInsertedListener listener) {
        firebaseRepository.getMinigamesReference(roomCode).child(minigame).child(blocker)
                .setValue(state)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    /** Ver si existe la sala */
    public void checkRoomExists(String roomCode, OnRoomCheckListener listener) {
        roomsRef.child(roomCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listener.onCheckComplete(snapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onCheckComplete(false);
            }
        });
    }

    /** Interface to handle the result of room check */
    public interface OnRoomCheckListener {
        void onCheckComplete(boolean exists);
    }

    /** Interfaces para manejar los datos */
    public interface OnRoomsFetchedListener {
        void onRoomsFetched(List<Rooms> roomList);
    }

    public interface OnPlayersFetchedListener {
        void onPlayersFetched(List<Players> playerList);
    }

    public interface OnDataInsertedListener {
        void onSuccess();
        void onFailure(String error);
    }
}
