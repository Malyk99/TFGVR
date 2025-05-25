package com.example.appfirebaselittledemons.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.adapters.PlayerAdapter;
import com.example.appfirebaselittledemons.firebase.FirebaseDataManager;
import com.example.appfirebaselittledemons.models.Players;
import com.example.appfirebaselittledemons.utils.GameStateUtils;
import com.example.appfirebaselittledemons.utils.MusicManager;
import com.google.firebase.database.*;

import java.util.List;


public class WaitingLobbyActivity extends AppCompatActivity {
    private String roomCode, currentUsername, userId;
    private RecyclerView recyclerViewPlayers;
    private PlayerAdapter playerAdapter;
    private FirebaseDataManager dataManager;
    private Button buttonReady, buttonLeave;
    private DatabaseReference roomRef;
    private boolean isReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_lobby);


        VideoView loadingVideo = findViewById(R.id.loadingVideo);
        View lobbyContent = findViewById(R.id.lobbyContent);
        lobbyContent.setVisibility(View.GONE);

        loadingVideo.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.intro);

        loadingVideo.setOnPreparedListener(mp -> {
            Log.d("VideoDebug", "Video is prepared, starting playback.");
            loadingVideo.requestFocus();

            new Handler().postDelayed(() -> {
                try {
                    mp.setLooping(false);
                    loadingVideo.start();
                } catch (IllegalStateException e) {
                    Log.e("VideoDebug", "Error during start: " + e.getMessage());
                }
            }, 100);
        });


        loadingVideo.setOnCompletionListener(mp -> {
            Log.d("VideoDebug", "Video completed, showing lobby.");
            //loadingVideo.setVisibility(View.GONE);
            lobbyContent.setVisibility(View.VISIBLE);
        });

        new Handler().postDelayed(() -> {
            Log.d("VideoDebug", "Video playing? " + loadingVideo.isPlaying());
            Log.d("VideoDebug", "Video duration: " + loadingVideo.getDuration());

            if (lobbyContent.getVisibility() != View.VISIBLE) {
                Log.w("VideoDebug", "Fallback triggered after 4s.");
                loadingVideo.setVisibility(View.GONE);
                lobbyContent.setVisibility(View.VISIBLE);
            }
        }, 6000);







        if (getIntent() != null && getIntent().hasExtra("roomCode") &&
                getIntent().hasExtra("username") && getIntent().hasExtra("userId")) {
            roomCode = getIntent().getStringExtra("roomCode");
            currentUsername = getIntent().getStringExtra("username");
            userId = getIntent().getStringExtra("userId");
            Log.d("WaitingLobbyActivity", "RoomCode received: " + roomCode);
        } else {
            Toast.makeText(this, "Room data missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        roomRef = FirebaseDatabase.getInstance().getReference("rooms").child(roomCode).child("players");

        recyclerViewPlayers = findViewById(R.id.recyclerViewPlayers);
        recyclerViewPlayers.setLayoutManager(new LinearLayoutManager(this));

        buttonReady = findViewById(R.id.buttonReady);
        buttonLeave = findViewById(R.id.buttonLeave);

        buttonReady.setOnClickListener(v -> toggleReadyState());
        buttonLeave.setOnClickListener(v -> leaveRoom());
        findViewById(R.id.button_settings).setOnClickListener(v -> showSettingsDialog());

        dataManager = new FirebaseDataManager();
        loadPlayers();
        observeReadyState();

        TextView gameInfoTextView = findViewById(R.id.text_game_info);
        GameStateUtils.setupGameStateListeners(roomCode, this, gameInfoTextView);
    }


    private void showSettingsDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.settings_dialog, null);
        Switch musicSwitch = dialogView.findViewById(R.id.switch_music);

        musicSwitch.setChecked(MusicManager.isMusicPlaying());

        musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !MusicManager.isMusicPlaying()) {
                MusicManager.startMusic(this);
            } else {
                MusicManager.stopMusic();
            }
        });

        new AlertDialog.Builder(this)
                .setTitle("Settings")
                .setView(dialogView)
                .setPositiveButton("Close", null)
                .show();
    }

    private void loadPlayers() {
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Players> playerList = dataManager.convertSnapshotToPlayerList(snapshot);

                if (playerAdapter == null) {
                    playerAdapter = new PlayerAdapter(playerList);
                    recyclerViewPlayers.setAdapter(playerAdapter);
                } else {
                    playerAdapter.updateData(playerList);
                    playerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WaitingLobbyActivity.this, "Failed to load players!", Toast.LENGTH_SHORT).show();
            }
        });
    }

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

    private void leaveRoom() {
        if (currentUsername == null) {
            Toast.makeText(this, "Username missing, can't leave room.", Toast.LENGTH_SHORT).show();
            return;
        }

        roomRef.child(currentUsername).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(WaitingLobbyActivity.this, "You left the room.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Log.e("FirebaseError", "Failed to leave room", e));
    }

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to check ready state", error.toException());
            }
        });
    }
}
