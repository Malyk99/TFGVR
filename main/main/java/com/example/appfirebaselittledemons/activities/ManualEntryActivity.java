package com.example.appfirebaselittledemons.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.firebase.FirebaseAdminManager;
import com.example.appfirebaselittledemons.firebase.FirebaseDataManager;
import com.google.firebase.database.FirebaseDatabase;

public class ManualEntryActivity extends AppCompatActivity {
    private EditText usernameInput, roomCodeInput;
    private FirebaseAdminManager adminManager;
    private FirebaseDataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);

        usernameInput = findViewById(R.id.usernameInput);
        roomCodeInput = findViewById(R.id.roomCodeInput);
        Button joinRoomButton = findViewById(R.id.buttonJoinRoom);

        adminManager = new FirebaseAdminManager();
        dataManager = new FirebaseDataManager();

        joinRoomButton.setOnClickListener(this::onJoinRoom);

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(ManualEntryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void onJoinRoom(View view) {
        String username = usernameInput.getText().toString().trim();
        String password = roomCodeInput.getText().toString().trim();

        Log.d("ManualEntry", "Username entered: " + username);
        Log.d("ManualEntry", "Room code/password entered: " + password);

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            Log.w("ManualEntry", "Missing username or room code");
            return;
        }

        Log.d("ManualEntry", "Checking if user is admin...");
        adminManager.validateAdminLogin(username, password, new FirebaseAdminManager.OnAdminLoginListener() {
            @Override
            public void onSuccess() {
                Log.d("ManualEntry", "Admin login successful");
                Toast.makeText(ManualEntryActivity.this, "Logged in as Admin", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ManualEntryActivity.this, AdminViewActivity.class);
                intent.putExtra("adminUsername", username);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String error) {
                Log.d("ManualEntry", "Not an admin. Checking player room access...");
                validateAndJoinRoom(username, password);
            }
        });
    }

    private void validateAndJoinRoom(String username, String roomCode) {
        Log.d("ManualEntry", "Checking if room exists: " + roomCode);
        dataManager.checkRoomExistsAndPlayerLimit(roomCode, (exists, full) -> {
            if (!exists) {
                Toast.makeText(ManualEntryActivity.this, "Room not found!", Toast.LENGTH_SHORT).show();
            } else if (full) {
                Toast.makeText(ManualEntryActivity.this, "Room is full!", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("ManualEntry", "Room has space. Adding player: " + username);
                dataManager.addPlayerToRoom(roomCode, username, new FirebaseDataManager.OnPlayerAddedListener() {
                    @Override
                    public void onSuccess(String playerId) {
                        Log.d("ManualEntry", "Player added successfully. ID: " + playerId);
                        Toast.makeText(ManualEntryActivity.this, "Joined room as Player", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ManualEntryActivity.this, WaitingLobbyActivity.class);
                        intent.putExtra("roomCode", roomCode);
                        intent.putExtra("username", username);
                        intent.putExtra("userId", playerId);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("ManualEntry", "Failed to add player", e);
                        Toast.makeText(ManualEntryActivity.this, "Error joining room", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        /*dataManager.checkRoomExists(roomCode, exists -> {
            if (exists) {
                Log.d("ManualEntry", "Room exists. Checking player count...");

                FirebaseDatabase.getInstance().getReference("rooms")
                        .child(roomCode)
                        .child("players")
                        .get()
                        .addOnSuccessListener(snapshot -> {
                            long playerCount = snapshot.getChildrenCount();
                            Log.d("ManualEntry", "Player count: " + playerCount);

                            if (playerCount >= 5) {
                                Toast.makeText(this, "Room is full (max 5 players)", Toast.LENGTH_SHORT).show();
                                Log.w("ManualEntry", "Join denied. Room is full.");
                                return;
                            }

                            Log.d("ManualEntry", "Room has space. Adding player: " + username);
                            dataManager.addPlayerToRoom(roomCode, username, new FirebaseDataManager.OnPlayerAddedListener() {
                                @Override
                                public void onSuccess(String playerId) {
                                    Log.d("ManualEntry", "Player added successfully. ID: " + playerId);
                                    Toast.makeText(ManualEntryActivity.this, "Joined room as Player", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ManualEntryActivity.this, WaitingLobbyActivity.class);
                                    intent.putExtra("roomCode", roomCode);
                                    intent.putExtra("username", username);
                                    intent.putExtra("userId", playerId);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e("ManualEntry", "Failed to add player", e);
                                    Toast.makeText(ManualEntryActivity.this, "Error joining room", Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            Log.e("ManualEntry", "Failed to get player count", e);
                            Toast.makeText(this, "Error checking room capacity", Toast.LENGTH_SHORT).show();
                        });

            } else {
                Log.w("ManualEntry", "Room does not exist: " + roomCode);
                Toast.makeText(ManualEntryActivity.this, "Room not found!", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

}
