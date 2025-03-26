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
        String password = roomCodeInput.getText().toString().trim(); // ✅ Using password instead of roomCode

        Log.d("DEBUG", "Entered Username: " + username);
        Log.d("DEBUG", "Entered Password: " + password);

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // First, check if the user is an admin
        adminManager.validateAdminLogin(username, password, new FirebaseAdminManager.OnAdminLoginListener() {
            @Override
            public void onSuccess() {
                Log.d("DEBUG", "Admin validation successful for: " + username);
                Intent intent = new Intent(ManualEntryActivity.this, AdminViewActivity.class);
                intent.putExtra("adminUsername", username);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String error) {
                Log.e("DEBUG", "Admin validation failed: " + error);
                validateAndJoinRoom(username, password); // ✅ If admin login fails, try as player
            }
        });
    }


    private void validateAndJoinRoom(String username, String roomCode) {
        dataManager.checkRoomExists(roomCode, exists -> {
            if (exists) {
                // Only add the player *after* confirming the room exists
                dataManager.addPlayerToRoom(roomCode, username, new FirebaseDataManager.OnPlayerAddedListener() {
                    @Override
                    public void onSuccess(String playerId) {
                        Intent intent = new Intent(ManualEntryActivity.this, WaitingLobbyActivity.class);
                        intent.putExtra("roomCode", roomCode);
                        intent.putExtra("username", username);
                        intent.putExtra("userId", playerId);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("FirebaseError", "Failed to add player", e);
                        Toast.makeText(ManualEntryActivity.this, "Failed to join room", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(ManualEntryActivity.this, "Room not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
