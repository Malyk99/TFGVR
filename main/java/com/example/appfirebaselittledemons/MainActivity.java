package com.example.appfirebaselittledemons;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

        @Override
        protected void onCreate(Bundle savedInstanceState) { // ✅ Fixed Syntax Here
            super.onCreate(savedInstanceState);
            FirebaseApp.initializeApp(this);

            //Create new room for testing
            createRoomWithPlayers();

            // Log Firebase initialization
            databaseReference = FirebaseDatabase.getInstance().getReference();
            Log.d("FirebaseCheck", "Firebase initialized successfully!");

            // ✅ Start Room Selection Activity Automatically
            Intent intent = new Intent(MainActivity.this, RoomSelectionActivity.class);
            startActivity(intent);
            finish(); // Closes MainActivity so it doesn't stay in the background
        }


    public void createRoomWithPlayers() {
        DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("rooms");

        // Generate a random 6-digit room ID
        int roomId = new Random().nextInt(900000) + 100000;

        // Create Room with ID only (No name)
        Room newRoom = new Room(roomId);

        roomsRef.child(String.valueOf(roomId)).setValue(newRoom)
                .addOnSuccessListener(aVoid -> Log.d("FirebaseDB", "Room added successfully!"))
                .addOnFailureListener(e -> Log.e("FirebaseDB", "Failed to add room", e));
    }

    public void readRooms() {
        DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("rooms");

        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    Room room = roomSnapshot.getValue(Room.class);
                    if (room != null) {
                        Log.d("FirebaseDB", "Room ID: " + room.id);

                        // Convert players from HashMap to List<Player> if needed
                        if (roomSnapshot.child("players").exists()) {
                            List<Player> players = new ArrayList<>();
                            for (DataSnapshot playerSnapshot : roomSnapshot.child("players").getChildren()) {
                                Player player = playerSnapshot.getValue(Player.class);
                                if (player != null) {
                                    players.add(player);
                                }
                            }
                            room.players = players; // Set fixed list
                        }

                        // Now print players
                        for (Player player : room.players) {
                            Log.d("FirebaseDB", "  - Player: " + player.name);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseDB", "Failed to read data", error.toException());
            }
        });
    }





}