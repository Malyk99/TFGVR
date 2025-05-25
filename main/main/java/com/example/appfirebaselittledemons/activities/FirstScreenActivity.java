package com.example.appfirebaselittledemons.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.utils.MusicManager;

public class FirstScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        MusicManager.startMusic(this);

        VideoView videoView = findViewById(R.id.testVideo);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro);
        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(mp -> {
            Log.d("VideoTest", "Video is prepared, starting...");
            videoView.start();
        });

        videoView.setOnCompletionListener(mp -> {
            Log.d("VideoTest", "Video completed. Going to MainActivity.");
            goToMain();
        });

    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
