package com.example.appfirebaselittledemons.activities;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.adapters.AdminRoomAdapter;
import com.example.appfirebaselittledemons.firebase.FirebaseDataManager;
import com.example.appfirebaselittledemons.models.Rooms;
import java.util.List;

public class AdminViewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdminRoomAdapter adapter;
    private FirebaseDataManager dataManager;
    private String adminUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

        if (getIntent().hasExtra("adminUsername")) {
            adminUsername = getIntent().getStringExtra("adminUsername");
        } else {
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerViewRooms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataManager = new FirebaseDataManager();
        loadRooms();
    }

    private void loadRooms() {
        dataManager.fetchRooms(roomList -> {
            adapter = new AdminRoomAdapter(this, roomList, adminUsername);
            recyclerView.setAdapter(adapter);
        });
    }
}

