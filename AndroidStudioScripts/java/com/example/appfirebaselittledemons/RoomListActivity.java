package com.example.appfirebaselittledemons;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class RoomListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;
    private DatabaseReference roomsRef;
    private Button buttonRefresh, buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        recyclerView = findViewById(R.id.recyclerViewRooms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(roomList, this);
        recyclerView.setAdapter(roomAdapter);

        roomsRef = FirebaseDatabase.getInstance().getReference("rooms");

        buttonRefresh = findViewById(R.id.buttonRefresh);
        buttonBack = findViewById(R.id.buttonBack);

        // Load rooms initially
        loadRooms();

        // Refresh button reloads the list
        buttonRefresh.setOnClickListener(view -> loadRooms());

        // Back button navigates back to RoomSelectionActivity
        buttonBack.setOnClickListener(view -> {
            Intent intent = new Intent(RoomListActivity.this, RoomSelectionActivity.class);
            startActivity(intent);
            finish(); // Closes current activity
        });

    }

    private void loadRooms() {
        roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomList.clear();
                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    String roomId = roomSnapshot.getKey();
                    Room room = new Room(Integer.parseInt(roomId));

                    // Fetch number of players
                    if (roomSnapshot.child("players").exists()) {
                        int playerCount = (int) roomSnapshot.child("players").getChildrenCount();
                        room.setPlayerCount(playerCount);
                    } else {
                        room.setPlayerCount(0);
                    }

                    roomList.add(room);
                }
                roomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RoomListActivity.this, "Failed to load rooms!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
