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
import java.util.Set;

public class AdminViewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdminRoomAdapter adapter;
    private FirebaseDataManager dataManager;
    private String adminUsername;
    private Button buttonCreateRoom;

    private Button buttonDeleteSelected, buttonSelectAll;
    private Set<String> selectedRooms;

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
        loadRooms();

        buttonCreateRoom.setOnClickListener(v -> showCreateRoomDialog());
        buttonDeleteSelected.setOnClickListener(v -> confirmDeleteSelectedRooms());
        buttonSelectAll.setOnClickListener(v -> selectAllRooms());
    }

    private void loadRooms() {
        dataManager.fetchRooms(roomList -> {
            adapter = new AdminRoomAdapter(this, roomList, this::deleteRoom, this::updateSelectedRooms);
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
                            selectedRooms.clear(); // ✅ Clear selection
                            loadRooms(); // ✅ Reload updated list
                        } else {
                            Toast.makeText(this, "Failed to delete rooms!", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void selectAllRooms() {
        for (Rooms room : adapter.getSelectedRooms()) {
            selectedRooms.add(String.valueOf(room.getId()));
        }
        adapter.notifyDataSetChanged();
        buttonDeleteSelected.setEnabled(true);
    }

    // This method handles room deletion
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
    private void showCreateRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Room");

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_create_room, null);
        builder.setView(dialogView);

        EditText inputAdminUsername = dialogView.findViewById(R.id.editTextAdminUsername);
        inputAdminUsername.setText(adminUsername); // Pre-fill with current admin name

        builder.setPositiveButton("Create", (dialog, which) -> {
            String enteredUsername = inputAdminUsername.getText().toString().trim();
            if (enteredUsername.isEmpty()) {
                Toast.makeText(AdminViewActivity.this, "Admin username required!", Toast.LENGTH_SHORT).show();
                return;
            }

            dataManager.createRoom(enteredUsername, (success, roomId) -> {
                if (success) {
                    Toast.makeText(AdminViewActivity.this, "Room created successfully!", Toast.LENGTH_SHORT).show();
                    loadRooms(); // Refresh list
                } else {
                    Toast.makeText(AdminViewActivity.this, "Failed to create room!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
