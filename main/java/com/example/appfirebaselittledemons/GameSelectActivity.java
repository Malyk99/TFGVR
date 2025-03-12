package com.example.appfirebaselittledemons;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class GameSelectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_select);
    }

    public void openMinijuego1(View view) {
        Intent intent = new Intent(GameSelectActivity.this, Minijuego1Activity.class);
        startActivity(intent);
    }
}
