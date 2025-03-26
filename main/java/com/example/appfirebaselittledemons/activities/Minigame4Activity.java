package com.example.appfirebaselittledemons.activities;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.customviews.Minigame4View;
import com.example.appfirebaselittledemons.utils.FirebaseUtils;
import com.google.firebase.database.FirebaseDatabase;

public class Minigame4Activity extends AppCompatActivity implements SensorEventListener {

    private Minigame4View minigameView;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private String roomCode, userId;
    private FrameLayout container;
    private Button tapButton;
    private ProgressBar loadingIndicator;
    private int tapCount = 0;
    private final int TAP_GOAL = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame4);

        roomCode = getIntent().getStringExtra("roomCode");
        userId = getIntent().getStringExtra("userId");

        FirebaseUtils.monitorPlayerStatus(this, roomCode, userId);

        container = findViewById(R.id.gameContainer);
        tapButton = findViewById(R.id.tapButton);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        tapButton.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);

        minigameView = new Minigame4View(this);
        container.addView(minigameView);

        minigameView.setBallInHoleListener(() -> runOnUiThread(() -> {
            tapButton.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Hole reached! Tap to activate!", Toast.LENGTH_SHORT).show();
        }));

        minigameView.setOnReadyListener(() -> runOnUiThread(() -> loadingIndicator.setVisibility(View.GONE)));

        tapButton.setOnClickListener(v -> {
            tapCount++;
            if (tapCount >= TAP_GOAL) {
                triggerBomb();
                tapButton.setVisibility(View.GONE);
                tapCount = 0;
                shakeScreen();
                new Handler().postDelayed(() -> minigameView.resetBall(), 500);
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    private void triggerBomb() {
        FirebaseDatabase.getInstance().getReference("rooms")
                .child(roomCode)
                .child("minigames")
                .child("minigame4")
                .child("spawnBomb")
                .setValue(true);

        new Handler().postDelayed(() ->
                FirebaseDatabase.getInstance().getReference("rooms")
                        .child(roomCode)
                        .child("minigames")
                        .child("minigame4")
                        .child("spawnBomb")
                        .setValue(false), 1000);
    }

    private void shakeScreen() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        if (shake != null && container != null) {
            container.startAnimation(shake);
        }
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(300);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = -event.values[0];
            float y = event.values[1];
            if (minigameView != null) {
                minigameView.updateBall(x, y);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
