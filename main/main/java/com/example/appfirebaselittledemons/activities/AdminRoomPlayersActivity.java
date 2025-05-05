package com.example.appfirebaselittledemons.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.adapters.AdminPlayerAdapter;
import com.example.appfirebaselittledemons.firebase.FirebaseDataManager;
import com.example.appfirebaselittledemons.models.Players;

import java.util.List;

public class AdminRoomPlayersActivity extends AppCompatActivity {

    private String roomCode;
    private RecyclerView recyclerViewPlayers;
    private AdminPlayerAdapter adapter;
    private FirebaseDataManager dataManager;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_room_players);

        roomCode = getIntent().getStringExtra("roomCode");
        if (roomCode == null) {
            Toast.makeText(this, "Room code missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dataManager = new FirebaseDataManager();

        //recyclerViewPlayers = findViewById(R.id.recyclerViewPlayers);
        recyclerViewPlayers = findViewById(R.id.recyclerViewAdminPlayers);
        recyclerViewPlayers.setLayoutManager(new LinearLayoutManager(this));

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

        loadPlayers();
    }

    private void loadPlayers() {
        dataManager.fetchPlayers(roomCode, new FirebaseDataManager.OnPlayersFetchedListener() {
            @Override
            public void onPlayersFetched(List<Players> playerList) {
                adapter = new AdminPlayerAdapter(roomCode, playerList, AdminRoomPlayersActivity.this, AdminRoomPlayersActivity.this::loadPlayers);
                recyclerViewPlayers.setAdapter(adapter);
            }
        });
    }
}
