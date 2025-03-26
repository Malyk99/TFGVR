package com.example.appfirebaselittledemons.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.adapters.RoomAdapter;
import com.example.appfirebaselittledemons.firebase.FirebaseDataManager;
import com.example.appfirebaselittledemons.models.Rooms;
import java.util.List;

public class RoomSelectionActivity extends AppCompatActivity {
    private RecyclerView recyclerViewRooms;
    private RoomAdapter roomAdapter;
    private FirebaseDataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_selection);

        recyclerViewRooms = findViewById(R.id.recyclerViewRooms);
        recyclerViewRooms.setLayoutManager(new LinearLayoutManager(this));

        dataManager = new FirebaseDataManager();
        loadRooms();
    }

    private void loadRooms() {
        dataManager.fetchRooms(roomList -> {
            roomAdapter = new RoomAdapter(this, roomList, this::onRoomSelected);
            recyclerViewRooms.setAdapter(roomAdapter);
        });
    }

    private void onRoomSelected(String roomCode) {
        showUsernameDialog(roomCode);
    }

    private void showUsernameDialog(String roomCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Username");

        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_username_input, null);
        builder.setView(dialogView);

        EditText inputUsername = dialogView.findViewById(R.id.editTextUsername);

        builder.setPositiveButton("Join", (dialog, which) -> {
            String username = inputUsername.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(RoomSelectionActivity.this, "Username cannot be empty!", Toast.LENGTH_SHORT).show();
            } else {
                joinRoom(roomCode, username);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
    private void joinRoom(String roomCode, String username) {
        dataManager.checkRoomExists(roomCode, exists -> {
            if (exists) {
                // ✅ Add the player before proceeding to the next screen
                dataManager.addPlayerToRoom(roomCode, username, new FirebaseDataManager.OnPlayerAddedListener() {
                    @Override
                    public void onSuccess(String playerId) {
                        Intent intent = new Intent(RoomSelectionActivity.this, WaitingLobbyActivity.class);
                        intent.putExtra("roomCode", roomCode);
                        intent.putExtra("username", username);
                        intent.putExtra("userId", playerId);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("FirebaseError", "Failed to add player", e);
                        Toast.makeText(RoomSelectionActivity.this, "Failed to join room", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(RoomSelectionActivity.this, "Room not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
