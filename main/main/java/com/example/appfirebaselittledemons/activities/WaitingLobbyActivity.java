package com.example.appfirebaselittledemons.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.adapters.PlayerAdapter;
import com.example.appfirebaselittledemons.firebase.FirebaseDataManager;
import com.example.appfirebaselittledemons.models.Players;
import com.google.firebase.database.*;

import java.util.List;

public class WaitingLobbyActivity extends AppCompatActivity {
    private String roomCode, currentUsername, userId;
    private RecyclerView recyclerViewPlayers;
    private PlayerAdapter playerAdapter;
    private FirebaseDataManager dataManager;
    private Button buttonReady, buttonStartGame, buttonLeave;
    private DatabaseReference roomRef;
    private boolean isReady = false; // Player's ready state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_lobby);

        // ✅ Retrieve roomCode, username, and userId from Intent
        if (getIntent() != null && getIntent().hasExtra("roomCode") && getIntent().hasExtra("username") && getIntent().hasExtra("userId")) {
            roomCode = getIntent().getStringExtra("roomCode");
            currentUsername = getIntent().getStringExtra("username");
            userId = getIntent().getStringExtra("userId");  // ✅ Retrieve userId

            Log.d("WaitingLobbyActivity", "RoomCode received: " + roomCode); // Debugging
        } else {
            Toast.makeText(this, "Room data missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ✅ Now userId is available and can be used in toggleReadyState()
        roomRef = FirebaseDatabase.getInstance().getReference("rooms").child(roomCode).child("players");

        // Initialize UI components
        recyclerViewPlayers = findViewById(R.id.recyclerViewPlayers);
        recyclerViewPlayers.setLayoutManager(new LinearLayoutManager(this));

        buttonReady = findViewById(R.id.buttonReady);
        buttonStartGame = findViewById(R.id.buttonStartGame);
        buttonLeave = findViewById(R.id.buttonLeave);

        // Set button listeners
        buttonReady.setOnClickListener(v -> toggleReadyState());
        buttonLeave.setOnClickListener(v -> leaveRoom());
        buttonStartGame.setOnClickListener(v -> startGame());

        // Initialize FirebaseDataManager and load players
        dataManager = new FirebaseDataManager();
        loadPlayers();
        observeReadyState();
    }


    /** Load players and update RecyclerView */
    private void loadPlayers() {
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Players> playerList = dataManager.convertSnapshotToPlayerList(snapshot);
                playerAdapter = new PlayerAdapter(playerList);
                recyclerViewPlayers.setAdapter(playerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WaitingLobbyActivity.this, "Failed to load players!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** ✅ Toggle Ready/Unready State */
    private void toggleReadyState() {
        if (userId == null) {
            Log.e("FirebaseError", "User ID is null, cannot toggle ready state.");
            return;
        }

        DatabaseReference playerRef = roomRef.child(userId);

        playerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean currentReadyState = snapshot.child("ready").getValue(Boolean.class);
                    boolean newReadyState = (currentReadyState != null) ? !currentReadyState : true;

                    // ✅ Update only the "ready" field in the existing player entry
                    playerRef.child("ready").setValue(newReadyState)
                            .addOnSuccessListener(aVoid -> {
                                buttonReady.setText(newReadyState ? "Unready" : "Ready");
                                buttonReady.setBackgroundTintList(
                                        ContextCompat.getColorStateList(WaitingLobbyActivity.this,
                                                newReadyState ? R.color.red : R.color.green)
                                );
                            })
                            .addOnFailureListener(e -> Log.e("FirebaseError", "Failed to update ready state", e));
                } else {
                    Log.e("FirebaseError", "Player not found in Firebase.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch player data: " + error.getMessage());
            }
        });
    }


    /** ✅ Leave Room */
    private void leaveRoom() {
        roomRef.child(currentUsername).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(WaitingLobbyActivity.this, "You left the room.", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to previous screen
                })
                .addOnFailureListener(e -> Log.e("FirebaseError", "Failed to leave room", e));
    }

    /** ✅ Start Game if All Players are Ready */
    private void startGame() {
        if (roomCode == null || roomCode.isEmpty()) {
            Toast.makeText(this, "Room data missing!", Toast.LENGTH_SHORT).show();
            Log.e("WaitingLobbyActivity", "RoomCode is null or empty when trying to start game.");
            return;
        }

        Intent intent = new Intent(WaitingLobbyActivity.this, GameSelectActivity.class);
        intent.putExtra("roomCode", roomCode);
        intent.putExtra("username", currentUsername);
        intent.putExtra("userId", userId);
        Log.d("WaitingLobbyActivity", "Starting game with roomCode: " + roomCode + ", username: " + currentUsername + ", userId: " + userId);
        startActivity(intent);
        finish();
    }

    /** ✅ Observe Ready State of All Players */
    private void observeReadyState() {
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean allReady = true;
                for (DataSnapshot playerSnapshot : snapshot.getChildren()) {
                    Boolean ready = playerSnapshot.child("ready").getValue(Boolean.class);
                    if (ready == null || !ready) {
                        allReady = false;
                        break;
                    }
                }
                buttonStartGame.setEnabled(allReady);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to check ready state", error.toException());
            }
        });
    }
}
