package com.example.appfirebaselittledemons.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.adapters.PlayerAdapter;
import com.example.appfirebaselittledemons.firebase.FirebaseDataManager;
import com.example.appfirebaselittledemons.models.Players;
import com.google.firebase.database.*;

import java.util.List;

public class WaitingLobbyActivity extends AppCompatActivity {
    private String roomCode, currentUsername;
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

        if (getIntent() != null && getIntent().hasExtra("roomCode") && getIntent().hasExtra("username")) {
            roomCode = getIntent().getStringExtra("roomCode");
            currentUsername = getIntent().getStringExtra("username");
        } else {
            Toast.makeText(this, "Room data missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        roomRef = FirebaseDatabase.getInstance().getReference("rooms").child(roomCode).child("players");

        recyclerViewPlayers = findViewById(R.id.recyclerViewPlayers);
        recyclerViewPlayers.setLayoutManager(new LinearLayoutManager(this));

        buttonReady = findViewById(R.id.buttonReady);
        buttonStartGame = findViewById(R.id.buttonStartGame);
        buttonLeave = findViewById(R.id.buttonLeave);

        buttonReady.setOnClickListener(v -> toggleReadyState());
        buttonLeave.setOnClickListener(v -> leaveRoom());
        buttonStartGame.setOnClickListener(v -> startGame());

        dataManager = new FirebaseDataManager();
        loadPlayers();
        observeReadyState();
    }

    private void loadPlayers() {
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Players> playerList = dataManager.convertSnapshotToPlayerList(snapshot);
                playerAdapter = new PlayerAdapter(playerList);
                recyclerViewPlayers.setAdapter(playerAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(WaitingLobbyActivity.this, "Failed to load players!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleReadyState() {
        isReady = !isReady;
        roomRef.child(currentUsername).child("ready").setValue(isReady)
                .addOnSuccessListener(aVoid -> {
                    buttonReady.setText(isReady ? "Unready" : "Ready");
                    buttonReady.setBackgroundTintList(getResources().getColorStateList(
                            isReady ? R.color.red : R.color.green));
                })
                .addOnFailureListener(e -> Log.e("FirebaseError", "Failed to update ready state", e));
    }

    private void leaveRoom() {
        roomRef.child(currentUsername).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(WaitingLobbyActivity.this, "You left the room.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Log.e("FirebaseError", "Failed to leave room", e));
    }

    private void startGame() {
        Intent intent = new Intent(WaitingLobbyActivity.this, GameSelectActivity.class);
        intent.putExtra("roomCode", roomCode);
        startActivity(intent);
        finish();
    }

    private void observeReadyState() {
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
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
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseError", "Failed to check ready state", error.toException());
            }
        });
    }
}
