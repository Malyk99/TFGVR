package com.example.appfirebaselittledemons.activities;

import android.content.Intent;
import android.os.Bundle;
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
        String roomCode = roomCodeInput.getText().toString().trim();

        if (username.isEmpty() || roomCode.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // First, check if the user is an admin
        adminManager.validateAdminLogin(username, roomCode, new FirebaseAdminManager.OnAdminLoginListener() {
            @Override
            public void onSuccess() {
                // If admin, redirect to AdminViewActivity
                Intent intent = new Intent(ManualEntryActivity.this, AdminViewActivity.class);
                intent.putExtra("adminUsername", username);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String error) {
                // If not admin, check if room exists and join as a player
                validateAndJoinRoom(username, roomCode);
            }
        });
    }

    private void validateAndJoinRoom(String username, String roomCode) {
        dataManager.checkRoomExists(roomCode, exists -> {
            if (exists) {
                Intent intent = new Intent(ManualEntryActivity.this, WaitingLobbyActivity.class);
                intent.putExtra("roomCode", roomCode);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(ManualEntryActivity.this, "Room not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
