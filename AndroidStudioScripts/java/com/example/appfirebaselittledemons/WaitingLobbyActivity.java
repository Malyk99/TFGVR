package com.example.appfirebaselittledemons;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class WaitingLobbyActivity extends AppCompatActivity {
    private String roomCode, currentUsername, userId;
    private DatabaseReference roomRef;
    private RecyclerView recyclerView;
    private PlayerAdapter playerAdapter;
    private List<Player> playerList;
    private Button readyButton;
    private boolean isReady = false; // Default state
    private Button startGameButton;

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_lobby);

        // ✅ Ensure roomCode and username are retrieved from the intent
        if (getIntent() != null && getIntent().hasExtra("roomCode") && getIntent().hasExtra("username")) {
            roomCode = getIntent().getStringExtra("roomCode");
            currentUsername = getIntent().getStringExtra("username");
        } else {
            showErrorDialog("Error: No room or user data received!");
            return;
        }

        // ✅ Initialize Firebase reference
        roomRef = FirebaseDatabase.getInstance().getReference("rooms").child(roomCode).child("players");

        // ✅ Add current user to Firebase if not already in the room
        addCurrentUserToRoom();

        // ✅ Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerViewPlayers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        playerList = new ArrayList<>();
        playerAdapter = new PlayerAdapter(playerList);
        recyclerView.setAdapter(playerAdapter);

        // ✅ Initialize Buttons
        Button readyButton = findViewById(R.id.buttonReady);
        Button leaveRoomButton = findViewById(R.id.buttonLeave);
        Button startGameButton = findViewById(R.id.buttonStartGame);

        // ✅ Handle Ready button click
        readyButton.setOnClickListener(view -> toggleReadyStatus());

        // ✅ Handle Leave Room button click
        leaveRoomButton.setOnClickListener(view -> leaveRoom());

        // ✅ Load players in the room and check if game can start
        loadPlayers(startGameButton);
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_lobby);

        // ✅ Retrieve roomCode, userId, and username from the intent
        if (getIntent() != null && getIntent().hasExtra("roomCode") && getIntent().hasExtra("userId") && getIntent().hasExtra("username")) {
            roomCode = getIntent().getStringExtra("roomCode");
            userId = getIntent().getStringExtra("userId");
            currentUsername = getIntent().getStringExtra("username");
        } else {
            showErrorDialog("Error: No room or user data received!");
            return;
        }

        // ✅ Initialize Firebase reference
        roomRef = FirebaseDatabase.getInstance().getReference("rooms").child(roomCode).child("players");

        // ✅ Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerViewPlayers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        playerList = new ArrayList<>();
        playerAdapter = new PlayerAdapter(playerList);
        recyclerView.setAdapter(playerAdapter);

        // ✅ Initialize Buttons
        Button readyButton = findViewById(R.id.buttonReady);
        Button startGameButton = findViewById(R.id.buttonStartGame);
        startGameButton.setOnClickListener(view -> startGame());

        Button leaveRoomButton = findViewById(R.id.buttonLeave);
        leaveRoomButton.setOnClickListener(view -> leaveRoom());


        // ✅ Set up "Ready" button functionality
        readyButton.setOnClickListener(view -> toggleReadyStatus(readyButton));

        // ✅ Set up a listener to check if all players are ready
        checkAllPlayersReady(startGameButton);

        // ✅ Load players in the room
        loadPlayers();

    }
    private void startGame() {
        Intent intent = new Intent(WaitingLobbyActivity.this, GameSelectActivity.class);
        intent.putExtra("roomCode", roomCode);
        startActivity(intent);
        finish(); // Close Waiting Lobby
    }





    private void showErrorDialog(String message) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    finish(); // Close the activity since it's invalid
                })
                .show();
    }
    private void addCurrentUserToRoom() {
        DatabaseReference userRef = roomRef.child(currentUsername);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // Add user to Firebase if not already in the room
                    userRef.setValue(new Player(currentUsername, false));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseError", "Failed to check if user exists: " + error.getMessage());
            }
        });
    }



    /*private void loadPlayers(Button startGameButton) {
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                playerList.clear();
                boolean allReady = true;

                for (DataSnapshot playerSnapshot : snapshot.getChildren()) {
                    Player player = playerSnapshot.getValue(Player.class);
                    if (player != null) {
                        playerList.add(player);
                        if (!player.ready) {
                            allReady = false;
                        }
                    }
                }

                playerAdapter.notifyDataSetChanged();
                startGameButton.setEnabled(allReady); // ✅ Enable Start Game button if all players are ready
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseError", "Failed to load players: " + error.getMessage());
            }
        });
    }*/
    private void loadPlayers() {
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                playerList.clear();

                for (DataSnapshot playerSnapshot : snapshot.getChildren()) {
                    Player player = playerSnapshot.getValue(Player.class);
                    if (player != null) {
                        playerList.add(player);
                    }
                }

                playerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                showErrorDialog("Failed to load players!");
            }
        });
    }

    private void checkAllPlayersReady(Button startGameButton) {
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean allReady = true;

                for (DataSnapshot playerSnapshot : snapshot.getChildren()) {
                    Boolean isReady = playerSnapshot.child("ready").getValue(Boolean.class);
                    if (isReady == null || !isReady) {
                        allReady = false;
                        break;
                    }
                }

                startGameButton.setEnabled(allReady); // ✅ Enable button if all players are ready
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseError", "Failed to check ready status: " + error.getMessage());
            }
        });
    }





    private void navigateToGameSelect() {
        Toast.makeText(this, "All players are ready! Starting the game...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(WaitingLobbyActivity.this, GameSelectActivity.class);
        intent.putExtra("roomCode", roomCode);
        startActivity(intent);
        finish(); // Close Waiting Lobby
    }




    private void toggleReadyStatus(Button readyButton) {
        DatabaseReference playerRef = roomRef.child(userId).child("ready");

        playerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean isReady = snapshot.getValue(Boolean.class);
                    boolean newReadyState = (isReady == null) ? true : !isReady;

                    playerRef.setValue(newReadyState)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("FirebaseDebug", "Player ready state updated: " + newReadyState);
                                readyButton.setText(newReadyState ? "Unready" : "Ready");
                            })
                            .addOnFailureListener(e -> Log.e("FirebaseError", "Failed to update ready state", e));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch player data: " + error.getMessage());
            }
        });
    }



    private void leaveRoom() {
        DatabaseReference playerRef = roomRef.child(userId);

        playerRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseDebug", "Player left the room successfully.");
                    Intent intent = new Intent(WaitingLobbyActivity.this, RoomSelectionActivity.class);
                    startActivity(intent);
                    finish(); // ✅ Exit back to Room Selection
                })
                .addOnFailureListener(e -> Log.e("FirebaseError", "Failed to leave room", e));
    }



}
