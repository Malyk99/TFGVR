package com.example.appfirebaselittledemons;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminViewActivity extends AppCompatActivity {
    private String roomCode, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

        roomCode = getIntent().getStringExtra("roomCode");
        username = getIntent().getStringExtra("username");

        TextView textView = findViewById(R.id.textViewAdmin);
        textView.setText("Welcome, Admin " + username + "\nRoom Code: " + roomCode);
    }
}
