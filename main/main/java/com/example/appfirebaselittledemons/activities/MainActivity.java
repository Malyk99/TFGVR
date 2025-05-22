package com.example.appfirebaselittledemons.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.utils.MusicManager;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonRoomSelection = findViewById(R.id.buttonRoomSelection);
        Button buttonManualEntry = findViewById(R.id.buttonManualEntry);

        buttonRoomSelection.setOnClickListener(this::openRoomSelection);
        buttonManualEntry.setOnClickListener(this::openManualEntry);
        findViewById(R.id.button_settings).setOnClickListener(v -> showSettingsDialog());

    }

    private void openRoomSelection(View view) {
        Intent intent = new Intent(MainActivity.this, RoomSelectionActivity.class);
        startActivity(intent);
    }

    private void openManualEntry(View view) {
        Intent intent = new Intent(MainActivity.this, ManualEntryActivity.class);
        startActivity(intent);
    }

    private void showSettingsDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.settings_dialog, null);
        Switch musicSwitch = dialogView.findViewById(R.id.switch_music);

        musicSwitch.setChecked(MusicManager.isMusicPlaying());

        musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !MusicManager.isMusicPlaying()) {
                MusicManager.startMusic(this);
            } else {
                MusicManager.stopMusic();
            }
        });

        new AlertDialog.Builder(this)
                .setTitle("Settings")
                .setView(dialogView)
                .setPositiveButton("Close", null)
                .show();
    }



}
