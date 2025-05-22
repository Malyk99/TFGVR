package com.example.appfirebaselittledemons.utils;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.appfirebaselittledemons.activities.WaitingLobbyActivity;

public class NavigationUtils {
    public static void returnToLobby(Activity activity, String roomCode, String userId, String username) {
        if (roomCode == null || userId == null || username == null) {
            Toast.makeText(activity, "Missing data, cannot return to lobby", Toast.LENGTH_SHORT).show();
            activity.finish();
            return;
        }

        Intent intent = new Intent(activity, WaitingLobbyActivity.class);
        intent.putExtra("roomCode", roomCode);
        intent.putExtra("userId", userId);
        intent.putExtra("username", username);
        activity.startActivity(intent);
        activity.finish();
    }
}
