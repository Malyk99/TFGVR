package com.example.appfirebaselittledemons.activities;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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
    private String roomCode, userId, username;
    private ImageView bombImage;
    private ProgressBar loadingIndicator;
    private int tapCount = 0;
    private final int TAP_GOAL = 10;
    private final Handler handler = new Handler();
    private CountDownTimer gameTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame4);

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

        minigameView = findViewById(R.id.gameView);
        bombImage = findViewById(R.id.bombImage);
        loadingIndicator = findViewById(R.id.loadingIndicator);

        bombImage.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);

        minigameView.setBallInHoleListener(() -> runOnUiThread(() -> {
            bombImage.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Hole reached! Tap to activate!", Toast.LENGTH_SHORT).show();
        }));

        minigameView.setOnReadyListener(() -> runOnUiThread(() -> loadingIndicator.setVisibility(View.GONE)));

        bombImage.setOnClickListener(v -> {
            tapCount++;
            if (tapCount >= TAP_GOAL) {
                triggerBomb();
                bombImage.setVisibility(View.GONE);
                tapCount = 0;
                shakeScreen();
                new Handler().postDelayed(() -> minigameView.resetBallAndMaze(), 500);
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        startGameTimer();
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
        View rootView = findViewById(android.R.id.content);
        if (shake != null && rootView != null) {
            rootView.startAnimation(shake);
        }
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(300);
        }
    }

    private void endMinigame() {
        FirebaseDatabase.getInstance()
                .getReference("rooms")
                .child(roomCode)
                .child("minigames")
                .child("minigame4")
                .child("gameState")
                .setValue("finished");
    }

    private void startGameTimer() {
        gameTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Optional: show remaining time
            }

            @Override
            public void onFinish() {
                Toast.makeText(Minigame4Activity.this, "Game time over!", Toast.LENGTH_SHORT).show();
                endMinigame();
            }
        }.start();
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
        if (gameTimer != null) {
            gameTimer.cancel();
        }
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
