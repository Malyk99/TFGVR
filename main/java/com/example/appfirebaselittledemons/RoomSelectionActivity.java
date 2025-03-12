package com.example.appfirebaselittledemons;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class RoomSelectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_selection);
    }

    // Navigate to RecyclerView Screen
    public void onChooseRoom(View view) {
        Intent intent = new Intent(this, RoomListActivity.class);
        startActivity(intent);
    }

    // Navigate to Manual Entry Screen
    public void onEnterManually(View view) {
        Intent intent = new Intent(this, ManualEntryActivity.class);
        startActivity(intent);
    }
}
