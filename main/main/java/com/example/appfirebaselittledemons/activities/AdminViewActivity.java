package com.example.appfirebaselittledemons.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.adapters.AdminRoomAdapter;
import com.example.appfirebaselittledemons.firebase.FirebaseDataManager;
import com.example.appfirebaselittledemons.models.Rooms;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class AdminViewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdminRoomAdapter adapter;
    private FirebaseDataManager dataManager;
    private String adminUsername;
    private Button buttonCreateRoom, buttonDeleteSelected, buttonSelectAll;
    private Set<String> selectedRooms = new HashSet<>();

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

        buttonCreateRoom = findViewById(R.id.buttonCreateRoom);
        buttonDeleteSelected = findViewById(R.id.buttonDeleteSelected);
        buttonSelectAll = findViewById(R.id.buttonSelectAll);

        dataManager = new FirebaseDataManager();

        buttonCreateRoom.setOnClickListener(v -> showCreateRoomDialog());
        buttonDeleteSelected.setOnClickListener(v -> confirmDeleteSelectedRooms());
        buttonSelectAll.setOnClickListener(v -> selectAllRooms());

        loadRooms();
    }

    private void loadRooms() {
        dataManager.adminFetchRooms(roomList -> {
            adapter = new AdminRoomAdapter(
                    this,
                    roomList,
                    this::deleteRoom,
                    this::updateSelectedRooms,
                    this::onRoomClicked
            );
            recyclerView.setAdapter(adapter);
        });
    }

    private void updateSelectedRooms(Set<String> selected) {
        selectedRooms = selected;
        buttonDeleteSelected.setEnabled(!selectedRooms.isEmpty());
    }

    private void confirmDeleteSelectedRooms() {
        if (selectedRooms.isEmpty()) return;

        new AlertDialog.Builder(this)
                .setTitle("Delete Rooms?")
                .setMessage("Are you sure you want to delete these rooms?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dataManager.deleteMultipleRooms(selectedRooms, success -> {
                        if (success) {
                            Toast.makeText(this, "Rooms deleted successfully!", Toast.LENGTH_SHORT).show();
                            selectedRooms.clear();
                            loadRooms();
                        } else {
                            Toast.makeText(this, "Failed to delete rooms!", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void selectAllRooms() {
        adapter.selectAllRooms();
        selectedRooms = adapter.getSelectedRoomIds();
        buttonDeleteSelected.setEnabled(true);
    }

    private void deleteRoom(String roomId) {
        dataManager.deleteRoom(roomId, success -> {
            if (success) {
                Toast.makeText(this, "Room deleted successfully!", Toast.LENGTH_SHORT).show();
                adapter.removeRoomById(roomId);
            } else {
                Toast.makeText(this, "Failed to delete room!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onRoomClicked(String roomCode) {
        Intent intent = new Intent(this, AdminRoomPlayersActivity.class);
        intent.putExtra("roomCode", roomCode);
        startActivity(intent);
    }

    private void showCreateRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Room");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_room, null);
        builder.setView(dialogView);

        EditText inputAdminUsername = dialogView.findViewById(R.id.editTextAdminUsername);
        inputAdminUsername.setText(adminUsername);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String enteredUsername = inputAdminUsername.getText().toString().trim();
            if (enteredUsername.isEmpty()) {
                Toast.makeText(this, "Admin username required!", Toast.LENGTH_SHORT).show();
                return;
            }

            dataManager.createRoom(enteredUsername, (success, roomId) -> {
                if (success) {
                    Toast.makeText(this, "Room created successfully!", Toast.LENGTH_SHORT).show();
                    loadRooms();
                } else {
                    Toast.makeText(this, "Failed to create room!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
