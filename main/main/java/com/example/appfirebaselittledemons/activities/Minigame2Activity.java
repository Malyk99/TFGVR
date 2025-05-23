package com.example.appfirebaselittledemons.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.firebase.FirebaseDataManager;
import com.example.appfirebaselittledemons.utils.FirebaseUtils;
import com.google.firebase.database.*;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class Minigame2Activity extends AppCompatActivity {
    private String roomCode, userId, username;
    private TextView textPoints;
    private FirebaseDataManager dataManager;
    private DatabaseReference minigameRef, countdownRef;
    private View movingBlock;
    private Button buttonLeft, buttonRight;
    private TextView textCountdown;
    private int blockPosition = 0;
    private int positionIncrement = 10;
    private CountDownTimer countDownTimer;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_minigame2);

        textPoints = findViewById(R.id.textPoints);
        dataManager = new FirebaseDataManager();

        // Disable back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });

        if (getIntent() != null && getIntent().hasExtra("roomCode") && getIntent().hasExtra("userId")) {
            roomCode = getIntent().getStringExtra("roomCode");
            userId = getIntent().getStringExtra("userId");
            username = getIntent().getStringExtra("username");
        } else {
            Toast.makeText(this, "Room data missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        FirebaseUtils.monitorPlayerStatus(this, roomCode, userId);

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

        textCountdown = findViewById(R.id.textCountdown);
        movingBlock = findViewById(R.id.movingBlock);
        buttonLeft = findViewById(R.id.buttonLeft);
        buttonRight = findViewById(R.id.buttonRight);

        buttonLeft.setOnClickListener(v -> updateBlockPosition(-positionIncrement));
        buttonRight.setOnClickListener(v -> updateBlockPosition(positionIncrement));

        adjustPositionIncrement();
        listenForBlockUpdates();

        startCountdown();
    }

    private void updateBlockPosition(int delta) {
        int newPosition = blockPosition + delta;

        if (newPosition < -100 || newPosition > 100) {
            int buttonId = delta < 0 ? R.id.buttonLeft : R.id.buttonRight;
            Button pressedButton = findViewById(buttonId);
            Animation blink = AnimationUtils.loadAnimation(this, R.anim.blink);
            pressedButton.startAnimation(blink);
            return;
        }
        minigameRef.setValue(newPosition)
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
        dataManager.fetchMinigame2Points(roomCode, new FirebaseDataManager.OnPointsFetchedListener() {
            @Override
            public void onPointsFetched(int points) {
                textPoints.setText("Points: " + points*-1);
            }

            @Override
            public void onFetchFailed(String error) {
                textPoints.setText("Error loading score");
                Log.e("Minigame2Points", error);
            }
        });

    }

    private void resetBlockPosition() {
        minigameRef.setValue(0);
        blockPosition = 0;
        movingBlock.setTranslationX(0);
        Log.d("Minigame2", "Block position reset to 0");
    }

    private void endMinigame() {
        FirebaseDatabase.getInstance()
                .getReference("rooms")
                .child(roomCode)
                .child("minigames")
                .child("minigame2")
                .child("gameState")
                .setValue("finished"); // GameState lo coge desde ahí
    }
}
