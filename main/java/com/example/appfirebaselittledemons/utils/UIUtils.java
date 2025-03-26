package com.example.appfirebaselittledemons.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class UIUtils {

    // Animación de pulsación para botones habilitados
    public static void applyPulseAnimation(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 1.1f, 1.0f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        view.startAnimation(scaleAnimation);
    }

    // Animación de reducción cuando el botón está deshabilitado
    public static void applyShrinkAnimation(View view) {
        view.clearAnimation();
        ScaleAnimation shrinkAnimation = new ScaleAnimation(
                1.0f, 0.9f, 1.0f, 0.9f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        shrinkAnimation.setDuration(300);
        shrinkAnimation.setFillAfter(true);
        view.startAnimation(shrinkAnimation);
    }

    // Efecto de parpadeo cuando se deshabilita un botón
    public static void applyFadeAnimation(View view) {
        AlphaAnimation fade = new AlphaAnimation(1.0f, 0.5f);
        fade.setDuration(300);
        fade.setFillAfter(true);
        view.startAnimation(fade);
    }
}
