package com.example.appfirebaselittledemons.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appfirebaselittledemons.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonRoomSelection = findViewById(R.id.buttonRoomSelection);
        Button buttonManualEntry = findViewById(R.id.buttonManualEntry);

        buttonRoomSelection.setOnClickListener(this::openRoomSelection);
        buttonManualEntry.setOnClickListener(this::openManualEntry);
    }

    private void openRoomSelection(View view) {
        Intent intent = new Intent(MainActivity.this, RoomSelectionActivity.class);
        startActivity(intent);
    }

    private void openManualEntry(View view) {
        Intent intent = new Intent(MainActivity.this, ManualEntryActivity.class);
        startActivity(intent);
    }
}
