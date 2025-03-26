package com.example.appfirebaselittledemons.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.utils.FirebaseUtils;
import com.google.firebase.database.*;

public class Minigame2Activity extends AppCompatActivity {
    private String roomCode, userId;
    private DatabaseReference minigameRef, countdownRef;
    private View movingBlock;
    private Button buttonLeft, buttonRight, buttonBack;
    private TextView textCountdown;
    private int blockPosition = 0;
    private int positionIncrement = 10; // Default movement increment
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Set landscape mode
        setContentView(R.layout.activity_minigame2);

        // Retrieve roomCode and userId
        if (getIntent() != null && getIntent().hasExtra("roomCode") && getIntent().hasExtra("userId")) {
            roomCode = getIntent().getStringExtra("roomCode");
            userId = getIntent().getStringExtra("userId");
        } else {
            Toast.makeText(this, "Room data missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Start kicked player listener
        FirebaseUtils.monitorPlayerStatus(this, roomCode, userId);

        // Firebase References
        minigameRef = FirebaseDatabase.getInstance()
                .getReference("rooms")
                .child(roomCode)
                .child("minigames")
                .child("minigame2")
                .child("position");

        countdownRef = FirebaseDatabase.getInstance()
                .getReference("rooms")
                .child(roomCode)
                .child("minigames")
                .child("minigame2")
                .child("minigame2Countdown");

        // Initialize UI Components
        textCountdown = findViewById(R.id.textCountdown);
        movingBlock = findViewById(R.id.movingBlock);
        buttonLeft = findViewById(R.id.buttonLeft);
        buttonRight = findViewById(R.id.buttonRight);
        buttonBack = findViewById(R.id.buttonBack);

        // Set Click Listeners
        buttonLeft.setOnClickListener(v -> updateBlockPosition(-positionIncrement));
        buttonRight.setOnClickListener(v -> updateBlockPosition(positionIncrement));
        buttonBack.setOnClickListener(v -> navigateBack());

        // Listen for block position updates
        adjustPositionIncrement();
        listenForBlockUpdates();

        // Start Countdown Timer
        startCountdown();
    }

    private void updateBlockPosition(int delta) {
        int newPosition = blockPosition + delta;
        minigameRef.setValue(newPosition)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Position updated: " + newPosition))
                .addOnFailureListener(e -> Log.e("FirebaseError", "Failed to update position", e));
    }

    private void listenForBlockUpdates() {
        minigameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer position = snapshot.getValue(Integer.class);
                if (position != null) {
                    blockPosition = position;
                    movingBlock.setTranslationX(blockPosition * 10);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to read block position");
            }
        });
    }

    private void adjustPositionIncrement() {
        DatabaseReference playersRef = FirebaseDatabase.getInstance()
                .getReference("rooms")
                .child(roomCode)
                .child("players");

        playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long playerCount = snapshot.getChildrenCount();
                if (playerCount == 2) {
                    positionIncrement = 10;
                } else if (playerCount == 3) {
                    positionIncrement = 5;
                } else if (playerCount == 4) {
                    positionIncrement = 3;
                } else {
                    positionIncrement = 2;
                }
                Log.d("Minigame2", "Adjusted position increment: " + positionIncrement);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to retrieve player count", error.toException());
            }
        });
    }

    private void startCountdown() {
        countdownRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long timeRemaining = snapshot.getValue(Long.class);
                if (timeRemaining == null || timeRemaining <= 0) {
                    countdownRef.setValue(60); // Start new countdown
                    beginCountdown(60);
                } else {
                    beginCountdown(timeRemaining);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to read countdown");
            }
        });
    }

    private void beginCountdown(long timeRemaining) {
        countDownTimer = new CountDownTimer(timeRemaining * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                textCountdown.setText(String.valueOf(secondsLeft));
                countdownRef.setValue(secondsLeft);
            }

            @Override
            public void onFinish() {
                countdownRef.setValue(0);
                resetBlockPosition();
                endMinigame();
            }
        }.start();
    }

    private void resetBlockPosition() {
        minigameRef.setValue(0);
        blockPosition = 0;
        movingBlock.setTranslationX(0);
        Log.d("Minigame2", "Block position reset to 0");
    }

    private void endMinigame() {
        Toast.makeText(this, "Minigame Over!", Toast.LENGTH_SHORT).show();
        navigateBack();
    }

    private void navigateBack() {
        Intent intent = new Intent(Minigame2Activity.this, GameSelectActivity.class);
        intent.putExtra("roomCode", roomCode);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }
}
