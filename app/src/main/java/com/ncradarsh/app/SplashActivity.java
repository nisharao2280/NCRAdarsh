package com.ncradarsh.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500; // 2.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logoImage = findViewById(R.id.splash_logo);
        TextView taglineText = findViewById(R.id.splash_tagline);
        ProgressBar progressBar = findViewById(R.id.splash_progress);

        // Fade-in animation for logo
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(800);
        fadeIn.setFillAfter(true);
        logoImage.startAnimation(fadeIn);

        // Fade-in for tagline after logo
        AlphaAnimation taglineFade = new AlphaAnimation(0f, 1f);
        taglineFade.setDuration(600);
        taglineFade.setStartOffset(600);
        taglineFade.setFillAfter(true);
        taglineText.startAnimation(taglineFade);

        // Animate progress bar
        animateProgressBar(progressBar);

        // Navigate to MainActivity after splash duration
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DURATION);
    }

    private void animateProgressBar(ProgressBar progressBar) {
        final int[] progress = {0};
        final int totalSteps = 25;
        final long stepDelay = SPLASH_DURATION / totalSteps;

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (progress[0] <= 100) {
                    progressBar.setProgress(progress[0]);
                    progress[0] += (100 / totalSteps);
                    handler.postDelayed(this, stepDelay);
                } else {
                    progressBar.setProgress(100);
                }
            }
        };
        handler.post(runnable);
    }
}
