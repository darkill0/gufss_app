package com.gufsspapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.gufsspapp.db.UserFileManager;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progress = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBar);

        // Init DB
        UserFileManager.getInstance(this);

        startProgress();
    }

    private void startProgress() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progress += 5;
                progressBar.setProgress(progress);
                if (progress < 100) {
                    handler.postDelayed(this, 30);
                } else {
                    checkSession();
                }
            }
        }, 30);
    }

    private void checkSession() {
        UserFileManager db = UserFileManager.getInstance(this);
        int userId = db.getSessionUserId();
        Intent intent;
        if (userId != -1 && db.getUserById(userId) != null) {
            intent = new Intent(this, HubActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
