package com.example.appfirebaselittledemons;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RotateInstructionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate_instruction);

        findViewById(R.id.buttonContinue).setOnClickListener(view -> {
            Intent intent = new Intent(RotateInstructionActivity.this, Minijuego1Activity.class);
            startActivity(intent);
            finish();
        });
    }
}
