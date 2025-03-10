package com.example.appfirebaselittledemons;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameSelectActivity extends AppCompatActivity {
    private DatabaseReference roomRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_select);

        // Retrieve roomCode from Intent
        String roomCode = getIntent().getStringExtra("roomCode");
        roomRef = FirebaseDatabase.getInstance().getReference("rooms").child(roomCode);

        // Initialize buttons and timers
        Button buttonBlock1 = findViewById(R.id.buttonBlock1);
        Button buttonBlock2 = findViewById(R.id.buttonBlock2);
        Button buttonBlock3 = findViewById(R.id.buttonBlock3);

        TextView timerBlock1 = findViewById(R.id.timerBlock1);
        TextView timerBlock2 = findViewById(R.id.timerBlock2);
        TextView timerBlock3 = findViewById(R.id.timerBlock3);

        // Apply pulsing effect while enabled
        applyPulseAnimation(buttonBlock1);
        applyPulseAnimation(buttonBlock2);
        applyPulseAnimation(buttonBlock3);

        // Handle button clicks
        buttonBlock1.setOnClickListener(view -> handleBlockPress(buttonBlock1, timerBlock1, "blocker1"));
        buttonBlock2.setOnClickListener(view -> handleBlockPress(buttonBlock2, timerBlock2, "blocker2"));
        buttonBlock3.setOnClickListener(view -> handleBlockPress(buttonBlock3, timerBlock3, "blocker3"));
    }

    private void handleBlockPress(Button button, TextView timerText, String firebaseKey) {
        // Update Firebase
        roomRef.child(firebaseKey).setValue(true);

        // Disable button, shrink, and change color
        button.setEnabled(false);
        button.setBackgroundColor(Color.RED);
        button.animate().scaleX(0.8f).scaleY(0.8f).setDuration(200);

        // Remove pulse animation
        button.clearAnimation();

        // Show countdown timer
        timerText.setVisibility(View.VISIBLE);
        new CountDownTimer(10000, 1000) { // 10-second countdown
            public void onTick(long millisUntilFinished) {
                timerText.setText("Wait: " + (millisUntilFinished / 1000) + "s");
            }

            public void onFinish() {
                button.setEnabled(true);
                button.setBackgroundColor(Color.LTGRAY);
                button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200); // Restore size
                timerText.setVisibility(View.GONE);
                applyPulseAnimation(button); // Reapply pulse animation
            }
        }.start();
    }

    private void applyPulseAnimation(Button button) {
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        button.startAnimation(pulse);
    }
}
