package com.example.appfirebaselittledemons.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.utils.FirebaseUtils;
import com.google.firebase.database.*;
import java.util.HashSet;
import java.util.Set;

public class Minigame3Activity extends AppCompatActivity {
    private String roomCode, userId;
    private TextView textCountdown, textRound, textPressCount;
    private Button buttonBack;
    private GridLayout gridButtons;
    private DatabaseReference minigameRef, playersRef;
    private int round = 1;
    private final int totalRounds = 4;
    private CountDownTimer roundTimer;
    private int pressesLeft;
    private Set<String> selectedButtons = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame3);

        if (getIntent() != null && getIntent().hasExtra("roomCode") && getIntent().hasExtra("userId")) {
            roomCode = getIntent().getStringExtra("roomCode");
            userId = getIntent().getStringExtra("userId");
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
        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

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

        selectedButtons.add("btn" + index);
        gridButtons.getChildAt(index).setEnabled(false);
        gridButtons.getChildAt(index).setBackgroundColor(getResources().getColor(R.color.grey));
        pressesLeft--;
        textPressCount.setText(String.valueOf(pressesLeft));

        minigameRef.child("round" + round).child(userId).child("btn" + index).setValue(true);
    }

    private void calculatePressLimit() {
        playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long playerCount = snapshot.getChildrenCount();
                if (playerCount == 2) pressesLeft = 6;
                else if (playerCount == 3) pressesLeft = 3;
                else if (playerCount == 4) pressesLeft = 2;
                else pressesLeft = 1;
                startRound();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to get player count");
            }
        });
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
        Toast.makeText(this, "Minigame Over!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Minigame3Activity.this, GameSelectActivity.class);
        intent.putExtra("roomCode", roomCode);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
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
