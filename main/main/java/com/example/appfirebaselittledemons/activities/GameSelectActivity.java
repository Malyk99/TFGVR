package com.example.appfirebaselittledemons.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.utils.FirebaseUtils;
import com.google.firebase.database.*;

import java.util.HashMap;



/*Esto es una antigua clase , utilizada para el desarrollo que permitia el acceso
 a los juegos sin necesitar tener confirmación por parte de Firbease DB*/
public class GameSelectActivity extends AppCompatActivity {
    private String roomCode, userId;
    private DatabaseReference playerRef;
    private ValueEventListener playerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_select);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (getIntent() != null && getIntent().hasExtra("roomCode") && getIntent().hasExtra("userId")) {
            roomCode = getIntent().getStringExtra("roomCode");
            userId = getIntent().getStringExtra("userId");

            // Initialize minigame data in Firebase
            initializeMinigamesStructure(roomCode);
        } else {
            Toast.makeText(this, "Room data missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        Button buttonMinigame1 = findViewById(R.id.buttonMinigame1);
        Button buttonMinigame2 = findViewById(R.id.buttonMinigame2);
        Button buttonMinigame3 = findViewById(R.id.buttonMinigame3);
        Button buttonMinigame4 = findViewById(R.id.buttonMinigame4);
        Button buttonBack = findViewById(R.id.buttonBack);


        buttonMinigame1.setOnClickListener(v -> openMinigame("Minigame1Activity"));
        buttonMinigame2.setOnClickListener(v -> openMinigame("Minigame2Activity"));
        buttonMinigame3.setOnClickListener(v -> openMinigame("Minigame3Activity"));
        buttonMinigame4.setOnClickListener(v -> openMinigame("Minigame4Activity"));
        buttonBack.setOnClickListener(v -> finish());


        FirebaseUtils.monitorPlayerStatus(this, roomCode, userId);

    }


    private void openMinigame(String activityName) {
        try {
            Class<?> activityClass = Class.forName("com.example.appfirebaselittledemons.activities." + activityName);
            Intent intent = new Intent(GameSelectActivity.this, activityClass);
            intent.putExtra("roomCode", roomCode);
            intent.putExtra("userId", userId);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            Log.e("GameSelectActivity", "Minigame activity not found!", e);
            Toast.makeText(this, "Minigame not available!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeMinigamesStructure(String roomCode) {
        DatabaseReference minigamesRef = FirebaseDatabase.getInstance()
                .getReference("rooms")
                .child(roomCode)
                .child("minigames");


        minigamesRef.child("minigame1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // Create default structure for minigame1
                    HashMap<String, Object> minigame1Defaults = new HashMap<>();
                    minigame1Defaults.put("blocker1", false);
                    minigame1Defaults.put("blocker2", false);
                    minigame1Defaults.put("blocker3", false);

                    minigamesRef.child("minigame1").setValue(minigame1Defaults)
                            .addOnSuccessListener(aVoid -> Log.d("Firebase", "Minigame1 structure initialized"))
                            .addOnFailureListener(e -> Log.e("FirebaseError", "Failed to initialize minigame1", e));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to check minigames structure", error.toException());
            }
        });
    }


}
