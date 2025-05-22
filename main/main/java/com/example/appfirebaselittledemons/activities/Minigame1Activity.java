package com.example.appfirebaselittledemons.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.utils.FirebaseUtils;
import com.example.appfirebaselittledemons.utils.NavigationUtils;
import com.google.firebase.database.*;

public class Minigame1Activity extends AppCompatActivity {
    private String roomCode, userId, username;
    private DatabaseReference minigameRef, countdownRef;
    private Button buttonBlock1, buttonBlock2, buttonBlock3;
    private TextView textCountdown;
    private final Handler handler = new Handler();
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Minigame1Activity", "Entered Minigame1Activity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame1);

        if (getIntent() != null && getIntent().hasExtra("roomCode") && getIntent().hasExtra("userId") && getIntent().hasExtra("username")) {
            roomCode = getIntent().getStringExtra("roomCode");
            userId = getIntent().getStringExtra("userId");
            username = getIntent().getStringExtra("username");
        } else {
            Toast.makeText(this, "Room data missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        FirebaseUtils.monitorPlayerStatus(this, roomCode, userId);

        // ✅ Firebase References
        minigameRef = FirebaseDatabase.getInstance()
                .getReference("rooms")
                .child(roomCode)
                .child("minigames")
                .child("minigame1");

        countdownRef = minigameRef.child("minigame1Countdown");

        // ✅ Initialize UI Components
        textCountdown = findViewById(R.id.textCountdown);
        buttonBlock1 = findViewById(R.id.buttonBlock1);
        buttonBlock2 = findViewById(R.id.buttonBlock2);
        buttonBlock3 = findViewById(R.id.buttonBlock3);

        // ✅ Apply animations
        applyAnimation(buttonBlock1);
        applyAnimation(buttonBlock2);
        applyAnimation(buttonBlock3);

        // ✅ Set Click Listeners
        buttonBlock1.setOnClickListener(v -> activateBlocker("blocker1", buttonBlock1));
        buttonBlock2.setOnClickListener(v -> activateBlocker("blocker2", buttonBlock2));
        buttonBlock3.setOnClickListener(v -> activateBlocker("blocker3", buttonBlock3));

        // ✅ Listen for blocker state changes
        setupBlockerListener("blocker1", buttonBlock1);
        setupBlockerListener("blocker2", buttonBlock2);
        setupBlockerListener("blocker3", buttonBlock3);

        // ✅ Start Countdown Timer
        startCountdown();
    }


    /** ✅ Start the countdown timer */
    private void startCountdown() {
        countdownRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long timeRemaining = snapshot.getValue(Long.class);
                if (timeRemaining == null || timeRemaining <= 0) {
                    countdownRef.setValue(60); // Start new
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

    /** ✅ Run countdown and update Firebase every second */
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
                disableAllButtons(); // Optional
                waitForGameFinishSignal();
            }

        }.start();
    }
    private void disableAllButtons() {
        buttonBlock1.setEnabled(false);
        buttonBlock2.setEnabled(false);
        buttonBlock3.setEnabled(false);
    }

    private void waitForGameFinishSignal() {
        DatabaseReference gameStateRef = FirebaseDatabase.getInstance()
                .getReference("rooms")
                .child(roomCode)
                .child("minigames")
                .child("minigame1")
                .child("gameState");

        gameStateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String state = snapshot.getValue(String.class);
                if ("finished".equals(state)) {
                    gameStateRef.removeEventListener(this); // cleanup
                    Toast.makeText(Minigame1Activity.this, "Game finished, returning to lobby…", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(() -> NavigationUtils.returnToLobby(Minigame1Activity.this, roomCode, userId, username), 5000);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to listen for gameState change", error.toException());
            }
        });
    }

    /** ✅ End minigame with 5s delay and return to lobby */
    private void endMinigame() {
        Toast.makeText(this, "Game finished, heading back to the lobby…", Toast.LENGTH_SHORT).show();
        handler.postDelayed(() -> NavigationUtils.returnToLobby(this, roomCode, userId, username), 5000);
    }

    private void activateBlocker(String blockerKey, Button button) {
        minigameRef.child(blockerKey).setValue(true)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", blockerKey + " set to true");
                    disableButtonForUser(button);
                    resetBlockerState(blockerKey);
                })
                .addOnFailureListener(e -> Log.e("FirebaseError", "Failed to update " + blockerKey, e));
    }

    private void resetBlockerState(String blockerKey) {
        handler.postDelayed(() -> {
            minigameRef.child(blockerKey).setValue(false);
            Log.d("Firebase", blockerKey + " reset to false after 2 seconds");
        }, 2000);
    }

    private void disableButtonForUser(Button button) {
        String originalText = button.getText().toString();
        float originalSize = button.getScaleX();

        button.setEnabled(false);
        button.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
        button.setScaleX(0.8f);
        button.setScaleY(0.8f);

        for (int i = 10; i >= 0; i--) {
            final int count = i;
            handler.postDelayed(() -> button.setText(String.valueOf(count)), (10 - i) * 1000L);
        }

        handler.postDelayed(() -> {
            button.setEnabled(true);
            button.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
            button.setText(originalText);
            button.setScaleX(originalSize);
            button.setScaleY(originalSize);
        }, 10000);
    }

    private void setupBlockerListener(String blockerKey, Button button) {
        minigameRef.child(blockerKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isBlocked = snapshot.getValue(Boolean.class);
                if (isBlocked != null && isBlocked) {
                    button.setEnabled(false);
                    button.setBackgroundTintList(ContextCompat.getColorStateList(Minigame1Activity.this, R.color.grey));

                    handler.postDelayed(() -> {
                        if (!button.getText().toString().matches("\\d+")) {
                            button.setEnabled(true);
                            button.setBackgroundTintList(ContextCompat.getColorStateList(Minigame1Activity.this, R.color.green));
                        }
                    }, 2000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to read blocker state: " + blockerKey);
            }
        });
    }

    private void applyAnimation(Button button) {
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        button.startAnimation(pulse);
    }
}
