package com.example.appfirebaselittledemons.utils;

import android.content.Context;
import android.media.MediaPlayer;
import com.example.appfirebaselittledemons.R;

public class MusicManager {
    private static MediaPlayer mediaPlayer;

    public static void startMusic(Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.background_music);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } else if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public static void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static boolean isMusicPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
}
