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

public class Minijuego1Activity extends AppCompatActivity {
    private DatabaseReference gameRef; // Nueva referencia a minijuego1 en Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minijuego1);

        // Obtener el código de la sala desde el intent
        String roomCode = getIntent().getStringExtra("roomCode");

        // Nueva referencia a Firebase: rooms > roomCode > minijuego1
        gameRef = FirebaseDatabase.getInstance().getReference("rooms").child(roomCode).child("minijuego1");

        // Inicializar botones y timers
        Button buttonBlock1 = findViewById(R.id.buttonBlock1);
        Button buttonBlock2 = findViewById(R.id.buttonBlock2);
        Button buttonBlock3 = findViewById(R.id.buttonBlock3);

        TextView timerBlock1 = findViewById(R.id.timerBlock1);
        TextView timerBlock2 = findViewById(R.id.timerBlock2);
        TextView timerBlock3 = findViewById(R.id.timerBlock3);

        // Aplicar animación de pulso mientras están habilitados
        applyPulseAnimation(buttonBlock1);
        applyPulseAnimation(buttonBlock2);
        applyPulseAnimation(buttonBlock3);

        // Configurar botones para actualizar Firebase
        buttonBlock1.setOnClickListener(view -> handleBlockPress(buttonBlock1, timerBlock1, "blocker1"));
        buttonBlock2.setOnClickListener(view -> handleBlockPress(buttonBlock2, timerBlock2, "blocker2"));
        buttonBlock3.setOnClickListener(view -> handleBlockPress(buttonBlock3, timerBlock3, "blocker3"));
    }

    private void handleBlockPress(Button button, TextView timerText, String firebaseKey) {
        // Nueva ruta en Firebase → rooms > roomCode > minijuego1 > blockerX
        gameRef.child(firebaseKey).setValue(true);

        // Deshabilitar el botón, hacerlo más pequeño y cambiar color
        button.setEnabled(false);
        button.setBackgroundColor(Color.RED);
        button.animate().scaleX(0.8f).scaleY(0.8f).setDuration(200);

        // Eliminar la animación de pulso temporalmente
        button.clearAnimation();

        // Mostrar el temporizador de 10 segundos
        timerText.setVisibility(View.VISIBLE);
        new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerText.setText("Wait: " + (millisUntilFinished / 1000) + "s");
            }

            public void onFinish() {
                button.setEnabled(true);
                button.setBackgroundColor(Color.LTGRAY);
                button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200); // Restaurar tamaño
                timerText.setVisibility(View.GONE);
                applyPulseAnimation(button); // Volver a aplicar la animación de pulso
            }
        }.start();
    }

    private void applyPulseAnimation(Button button) {
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        button.startAnimation(pulse);
    }
}
