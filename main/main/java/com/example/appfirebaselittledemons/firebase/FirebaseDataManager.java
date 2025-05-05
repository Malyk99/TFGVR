package com.example.appfirebaselittledemons.firebase;

import static java.lang.Integer.parseInt;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.appfirebaselittledemons.models.Players;
import com.example.appfirebaselittledemons.models.Rooms;
import com.example.appfirebaselittledemons.utils.FirebaseUtils;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
                    // ✅ Manually handle HashMap conversion
                    Rooms room = roomSnapshot.getValue(Rooms.class);
                    if (room != null) {
                        if (roomSnapshot.child("players").exists()) {
                            HashMap<String, Players> playerMap = new HashMap<>();
                            for (DataSnapshot playerSnapshot : roomSnapshot.child("players").getChildren()) {
                                Players player = playerSnapshot.getValue(Players.class);
                                if (player != null) {
                                    playerMap.put(playerSnapshot.getKey(), player);
                                }
                            }
                            room.setPlayers(playerMap);
                        }
                        roomList.add(room);
                    }
                }
                listener.onRoomsFetched(roomList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error retrieving rooms: " + error.getMessage());
            }
        });
    }



    /** Leer jugadores de una sala */
    public void fetchPlayers(String roomCode, OnPlayersFetchedListener listener) {
        firebaseRepository.getRoomsReference()
                .child(roomCode)
                .child("players")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Players> playerList = new ArrayList<>();

                        for (DataSnapshot playerSnapshot : snapshot.getChildren()) {
                            Players player = playerSnapshot.getValue(Players.class);
                            if (player != null) {
                                player.setId(playerSnapshot.getKey()); // Ensure ID is assigned
                                playerList.add(player);
                            }
                        }

                        listener.onPlayersFetched(playerList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Error fetching players: " + error.getMessage());
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
    /** ✅ Convert DataSnapshot into a List of Players */
    public List<Players> convertSnapshotToPlayerList(@NonNull DataSnapshot snapshot) {
        List<Players> playerList = new ArrayList<>();
        for (DataSnapshot playerSnapshot : snapshot.getChildren()) {
            Players player = playerSnapshot.getValue(Players.class);
            if (player != null) {
                playerList.add(player);
            }
        }
        Log.d("FirebaseDataManager", "Loaded " + playerList.size() + " players.");
        return playerList;
    }


    /** Insertar un nuevo jugador en una sala */
    public void addPlayerToRoom(String roomCode, String username, OnPlayerAddedListener listener) {
        DatabaseReference roomRef = firebaseRepository.getRoomsReference().child(roomCode).child("players");

        String playerId = roomRef.push().getKey(); // Generate unique ID
        Players newPlayer = new Players(playerId, username, false);

        roomRef.child(playerId).setValue(newPlayer)
                .addOnSuccessListener(aVoid -> listener.onSuccess(playerId))
                .addOnFailureListener(listener::onFailure);
    }

    public interface OnPlayerAddedListener {
        void onSuccess(String playerId);
        void onFailure(Exception e);
    }


    /** Insertar datos en minijuegos */
    public void updateMinigameState(String roomCode, String minigame, String blocker, boolean state, OnDataInsertedListener listener) {
        firebaseRepository.getMinigamesReference(roomCode).child(minigame).child(blocker)
                .setValue(state)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    /** Ver si existe la sala y si supera el limite de jugadores */
    public void checkRoomExists(String roomCode, OnRoomCheckListener listener) {
        firebaseRepository.getRoomsReference().child(roomCode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listener.onCheckComplete(snapshot.exists());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "checkRoomExists failed", error.toException());
                        listener.onCheckComplete(false);
                    }
                });
    }

    public void checkRoomExistsAndPlayerLimit(String roomCode, OnRoomLimitCheckListener listener) {
        firebaseRepository.getRoomsReference().child(roomCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    listener.onResult(false, false); // Room doesn't exist
                    return;
                }

                DataSnapshot playersSnapshot = snapshot.child("players");
                int playerCount = (int) playersSnapshot.getChildrenCount();

                if (playerCount >= 5) {
                    listener.onResult(true, true); // Room exists, but full
                } else {
                    listener.onResult(true, false); // Room exists, and space available
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Room check cancelled", error.toException());
                listener.onResult(false, false);
            }
        });
    }

    public interface OnRoomLimitCheckListener {
        void onResult(boolean roomExists, boolean roomFull);
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

    public interface OnRoomDeletedListener {
        void onRoomDeleted(boolean success);
    }
    /**
     * Deletes a room and all its child elements.
     */
    public void deleteRoom(String roomId, OnRoomDeletedListener listener) {
        DatabaseReference roomRef = firebaseRepository.getRoomsReference().child(roomId);

        roomRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseSuccess", "Room " + roomId + " deleted successfully.");
                    listener.onRoomDeleted(true);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Failed to delete room " + roomId, e);
                    listener.onRoomDeleted(false);
                });
    }

    public void deleteMultipleRooms(Set<String> roomIds, OnRoomsDeletedListener listener) {
        DatabaseReference roomsRef = firebaseRepository.getRoomsReference();

        for (String roomId : roomIds) {
            roomsRef.child(roomId).removeValue();
        }

        // ✅ Notify when deletion is complete
        listener.onRoomsDeleted(true);
    }

    // Callback Interface
    public interface OnRoomsDeletedListener {
        void onRoomsDeleted(boolean success);
    }


    public interface OnRoomCreatedListener {
        void onRoomCreated(boolean success, String roomId);
    }

    /**
     * Creates a new room with a unique ID and assigns an admin player.
     */
    public void createRoom(String adminUsername, OnRoomCreatedListener listener) {
        DatabaseReference roomsRef = firebaseRepository.getRoomsReference();

        String roomId = FirebaseUtils.generateRoomCode(); // Generate a 6-digit room code

        if (roomId == null) {
            listener.onRoomCreated(false, null);
            return;
        }

        // Create a new empty room
        Rooms newRoom = new Rooms(parseInt(roomId));

        roomsRef.child(roomId).setValue(newRoom)
                .addOnSuccessListener(aVoid -> {
                    // Add the admin as the first player in the room
                    addPlayerToRoom(roomId, adminUsername, new OnPlayerAddedListener() {
                        @Override
                        public void onSuccess(String playerId) {
                            listener.onRoomCreated(true, roomId);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            listener.onRoomCreated(false, null);
                        }
                    });
                })
                .addOnFailureListener(e -> listener.onRoomCreated(false, null));
    }




}
