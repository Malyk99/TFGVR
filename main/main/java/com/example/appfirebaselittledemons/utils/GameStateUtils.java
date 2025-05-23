package com.example.appfirebaselittledemons.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appfirebaselittledemons.activities.Minigame1Activity;
import com.example.appfirebaselittledemons.activities.Minigame2Activity;
import com.example.appfirebaselittledemons.activities.Minigame3Activity;
import com.example.appfirebaselittledemons.activities.Minigame4Activity;
import com.google.firebase.database.*;
import androidx.annotation.Nullable;

public class GameStateUtils {

    public static void setupGameStateListeners(String roomCode, Activity activity, @Nullable TextView infoTextView) {
        String userId = activity.getIntent().getStringExtra("userId");
        String username = activity.getIntent().getStringExtra("username");

        for (int i = 1; i <= 4; i++) {
            final String minigame = "minigame" + i;

            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("rooms")
                    .child(roomCode)
                    .child("minigames")
                    .child(minigame)
                    .child("gameState");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String state = snapshot.getValue(String.class);
                    Log.d("GameStateDebug", minigame + " gameState changed to: " + state);

                    if ("tutorial".equals(state)) {
                        String description = getMinigameDescription(minigame);
                        String gameName = getMinigameName(minigame);
                        Toast.makeText(activity, "Game " + gameName + ": " + description, Toast.LENGTH_LONG).show();
                        if (infoTextView != null) {
                            infoTextView.setText("Game " + gameName + ": " + description);
                        }
                    }

                    else if ("inProgress".equals(state)) {
                        Intent intent = null;
                        switch (minigame) {
                            case "minigame1":
                                intent = new Intent(activity, Minigame1Activity.class);
                                break;
                            case "minigame2":
                                intent = new Intent(activity, Minigame2Activity.class);
                                break;
                            case "minigame3":
                                intent = new Intent(activity, Minigame3Activity.class);
                                break;
                            case "minigame4":
                                intent = new Intent(activity, Minigame4Activity.class);
                                break;
                        }

                        if (intent != null) {
                            intent.putExtra("roomCode", roomCode);
                            intent.putExtra("userId", userId);
                            intent.putExtra("username", username);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    }

                    else if ("finished".equals(state)) {
                        Log.d("GameStateDebug", "Minigame finished, returning to lobby.");
                        Toast.makeText(activity, "Game finished! Returning to lobby...", Toast.LENGTH_SHORT).show();

                        // Step 1: Clear gameState after 3 seconds
                        new Handler().postDelayed(() -> {
                            FirebaseDatabase.getInstance()
                                    .getReference("rooms")
                                    .child(roomCode)
                                    .child("minigames")
                                    .child(minigame)
                                    .child("gameState")
                                    .setValue(null);
                        }, 3000);

                        // Step 2: Return to lobby after 5 seconds
                        new Handler().postDelayed(() ->
                                NavigationUtils.returnToLobby(activity, roomCode, userId, username), 5000
                        );
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("GameStateDebug", "Listener cancelled for " + minigame + ": " + error.getMessage());
                }
            });
        }
    }


    private static String getMinigameDescription(String minigame) {
        switch (minigame) {
            case "minigame1":
                return "Tap the blocks to stop Player 1 from scoring.";
            case "minigame2":
                return "It's pong. That simple. Move left and right don't let the ball pass.";
            case "minigame3":
                return "Choose the targets you want to deactivate to mess up Player 1. Be quick at the start.";
            case "minigame4":
                return "Tap the screen x10 when the ball reaches the hole. Move your phone to reach the hole.";
            default:
                return "No description available.";
        }
    }

    private static String getMinigameName(String minigame) {
        switch (minigame) {
            case "minigame1":
                return "Basket";
            case "minigame2":
                return "Pong";
            case "minigame3":
                return "Archery";
            case "minigame4":
                return "Puzzle";
            default:
                return "Unknown";
        }
    }
}
