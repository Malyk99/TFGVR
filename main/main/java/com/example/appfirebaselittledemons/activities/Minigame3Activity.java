package com.example.appfirebaselittledemons.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
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

import java.util.HashSet;
import java.util.Set;

public class Minigame3Activity extends AppCompatActivity {
    private String roomCode, userId, username;
    private TextView textCountdown, textRound, textPressCount;
    private GridLayout gridButtons;
    private DatabaseReference minigameRef, playersRef;
    private int round = 1;
    private final int totalRounds = 4;
    private CountDownTimer roundTimer;
    private int pressesLeft;
    private Set<String> selectedButtons = new HashSet<>();
    private boolean gameReset = false;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame3);

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

        textCountdown = findViewById(R.id.textCountdown);
        textRound = findViewById(R.id.textRound);
        textPressCount = findViewById(R.id.textPressCount);
        gridButtons = findViewById(R.id.gridButtons);

        minigameRef = FirebaseDatabase.getInstance()
                .getReference("rooms")
                .child(roomCode)
                .child("minigames")
                .child("minigame3");

        playersRef = FirebaseDatabase.getInstance()
                .getReference("rooms")
                .child(roomCode)
                .child("players");

        setupGrid();
        calculatePressLimit();
    }

    private void setupGrid() {
        for (int i = 0; i < 9; i++) {
            Button button = new Button(this);
            button.setText("Button " + (i + 1));
            int index = i;
            button.setTag("btn" + index);
            button.setOnClickListener(v -> onButtonPressed(index));
            gridButtons.addView(button);
        }
    }

    private void onButtonPressed(int index) {
        if (pressesLeft <= 0 || selectedButtons.contains("btn" + index)) return;

        DatabaseReference roundRef = FirebaseDatabase.getInstance()
                .getReference("rooms")
                .child(roomCode)
                .child("minigames")
                .child("minigame3")
                .child("round" + round);

        roundRef.child("chosenBy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean alreadyTaken = false;

                for (DataSnapshot playerSnap : snapshot.getChildren()) {
                    for (DataSnapshot btnSnap : playerSnap.getChildren()) {
                        if (btnSnap.getValue(Integer.class) == index) {
                            alreadyTaken = true;
                            break;
                        }
                    }
                    if (alreadyTaken) break;
                }

                if (!alreadyTaken) {
                    roundRef.child("chosenBy").child(userId).push().setValue(index);
                    roundRef.child("targetStates").child("btn" + index).setValue(false);

                    selectedButtons.add("btn" + index);
                    gridButtons.getChildAt(index).setEnabled(false);
                    gridButtons.getChildAt(index).setBackgroundColor(getResources().getColor(R.color.grey));
                    pressesLeft--;
                    textPressCount.setText(String.valueOf(pressesLeft));
                } else {
                    Toast.makeText(Minigame3Activity.this, "This target is already disabled by someone else!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error checking button status: " + error.getMessage());
            }
        });
    }

    private void calculatePressLimit() {
        playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long playerCount = snapshot.getChildrenCount();

                if (!gameReset) {
                    gameReset = true;

                    DatabaseReference roundDataRef = FirebaseDatabase.getInstance()
                            .getReference("rooms")
                            .child(roomCode)
                            .child("minigames")
                            .child("minigame3");

                    // Borra solo los datos de rondas previas, pero no el gameState ni otros flags globales
                    roundDataRef.getRef().get().addOnSuccessListener(roundSnapshot -> {
                        for (DataSnapshot child : roundSnapshot.getChildren()) {
                            if (child.getKey().startsWith("round")) {
                                child.getRef().removeValue();
                            }
                        }
                        setPressLimitAndStartRound(playerCount);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(Minigame3Activity.this, "Failed to clean old round data", Toast.LENGTH_SHORT).show();
                    });
                }
                else {
                    setPressLimitAndStartRound(playerCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to get player count");
            }
        });
    }

    private void setPressLimitAndStartRound(long playerCount) {
        if (playerCount == 2) pressesLeft = 6;
        else if (playerCount == 3) pressesLeft = 3;
        else if (playerCount == 4) pressesLeft = 2;
        else pressesLeft = 1;

        if (round == 1) {
            Toast.makeText(this, "Game starting. Choose the slots to block within 5 seconds", Toast.LENGTH_LONG).show();
        }

        startRound();
    }

    private void startRound() {
        textRound.setText("Round " + round);
        textPressCount.setText(String.valueOf(pressesLeft));
        selectedButtons.clear();

        for (int i = 0; i < gridButtons.getChildCount(); i++) {
            Button btn = (Button) gridButtons.getChildAt(i);
            btn.setEnabled(true);
            btn.setBackgroundColor(getResources().getColor(R.color.green));
        }

        setupFirebaseButtonListener(round);

        roundTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textCountdown.setText("Choose: " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                for (int i = 0; i < gridButtons.getChildCount(); i++) {
                    gridButtons.getChildAt(i).setEnabled(false);
                }

                textCountdown.setText("Next round in 10s");

                new CountDownTimer(10000, 1000) {
                    @Override
                    public void onTick(long l) {
                        textCountdown.setText("Next round in " + (l / 1000) + "s");
                    }

                    @Override
                    public void onFinish() {
                        if (round < totalRounds) {
                            round++;
                            calculatePressLimit();
                        } else {
                            endMinigame();
                        }
                    }
                }.start();
            }
        }.start();
    }

    private void endMinigame() {
        Toast.makeText(this, "Game finished, heading back to the lobby…", Toast.LENGTH_SHORT).show();
        handler.postDelayed(() -> NavigationUtils.returnToLobby(this, roomCode, userId, username), 5000);

    }

    private void setupFirebaseButtonListener(int roundNumber) {
        DatabaseReference roundRef = FirebaseDatabase.getInstance()
                .getReference("rooms")
                .child(roomCode)
                .child("minigames")
                .child("minigame3")
                .child("round" + roundNumber);

        roundRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot buttonSnapshot : snapshot.getChildren()) {
                    String buttonKey = buttonSnapshot.getKey();
                    for (int i = 0; i < gridButtons.getChildCount(); i++) {
                        Button button = (Button) gridButtons.getChildAt(i);
                        if (button.getTag().equals(buttonKey)) {
                            button.setEnabled(false);
                            button.setBackgroundTintList(ContextCompat.getColorStateList(Minigame3Activity.this, R.color.grey));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to read button state: " + error.getMessage());
            }
        });
    }
}
