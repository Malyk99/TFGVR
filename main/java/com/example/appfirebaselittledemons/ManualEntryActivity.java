package com.example.appfirebaselittledemons;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManualEntryActivity extends AppCompatActivity {
    private EditText usernameInput, roomCodeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);

        usernameInput = findViewById(R.id.usernameInput);
        roomCodeInput = findViewById(R.id.roomCodeInput);
    }
    public void onJoinRoom(View view) {
        String username = usernameInput.getText().toString().trim();
        String roomCode = roomCodeInput.getText().toString().trim();

        if (username.isEmpty() || roomCode.isEmpty()) {
            showErrorDialog("Please enter all fields.");
            return;
        }

        DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("rooms").child(roomCode);

        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // ✅ Generate a unique ID for the user
                    String userId = roomRef.child("players").push().getKey();

                    if (userId == null) {
                        showErrorDialog("Error generating user ID.");
                        return;
                    }

                    DatabaseReference playerRef = roomRef.child("players").child(userId);

                    playerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot playerSnapshot) {
                            if (!playerSnapshot.exists()) {
                                // ✅ Add user to room with a unique ID, name, and `ready: false`
                                playerRef.setValue(new Player(userId, username, false));
                            }

                            // ✅ Navigate to Waiting Lobby and pass `userId` and `username`
                            Intent intent = new Intent(ManualEntryActivity.this, WaitingLobbyActivity.class);
                            intent.putExtra("roomCode", roomCode);
                            intent.putExtra("userId", userId);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            showErrorDialog("Failed to check player status.");
                        }
                    });
                } else {
                    showErrorDialog("Room not found!");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                showErrorDialog("Database error! Please try again.");
            }
        });
    }



    /*public void onJoinRoom(View view) {
        String username = usernameInput.getText().toString().trim();
        String roomCode = roomCodeInput.getText().toString().trim();

        if (username.length() > 20 || roomCode.length() > 20) {
            showErrorDialog("Username and Room Code must be 20 characters max.");
            return;
        }

        if (username.isEmpty() || roomCode.isEmpty()) {
            showErrorDialog("Please enter all fields.");
            return;
        }

        DatabaseReference adminsRef = FirebaseDatabase.getInstance().getReference("Admins").child(username);
        DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("rooms").child(roomCode);

        adminsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot adminSnapshot) {
                if (adminSnapshot.exists()) {
                    String storedRoomCode = adminSnapshot.getValue(String.class);
                    if (storedRoomCode != null && storedRoomCode.equals(roomCode)) {
                        // User is an admin and room code matches → Open Admin View
                        Intent intent = new Intent(ManualEntryActivity.this, AdminViewActivity.class);
                        intent.putExtra("roomCode", roomCode);
                        intent.putExtra("username", username);
                        startActivity(intent);
                        finish();
                        return;
                    }
                }

                // If not an admin or room code doesn't match, check if the room exists
                roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot roomSnapshot) {
                        if (roomSnapshot.exists()) {
                            // Open Waiting Lobby for normal players
                            Intent intent = new Intent(ManualEntryActivity.this, WaitingLobbyActivity.class);
                            intent.putExtra("roomCode", roomCode);
                            intent.putExtra("username", username);
                            startActivity(intent);

                        } else {
                            showErrorDialog("Room not found! Please check the code and try again.");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        showErrorDialog("Database error! Please try again.");
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                showErrorDialog("Database error! Please try again.");
            }
        });
    }*/
    private void showErrorDialog(String message) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }


}
