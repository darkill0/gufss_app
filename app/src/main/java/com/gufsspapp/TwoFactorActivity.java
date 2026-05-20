package com.gufsspapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gufsspapp.db.UserFileManager;

public class TwoFactorActivity extends AppCompatActivity {

    private EditText[] pins;
    private int userId;
    private CountDownTimer countDownTimer;
    private final String DEMO_CODE = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_factor);

        userId = getIntent().getIntExtra("userId", -1);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        pins = new EditText[]{
                findViewById(R.id.pin1),
                findViewById(R.id.pin2),
                findViewById(R.id.pin3),
                findViewById(R.id.pin4),
                findViewById(R.id.pin5),
                findViewById(R.id.pin6)
        };

        setupPinInputs();

        TextView tvResend = findViewById(R.id.tvResend);
        startCountdown(tvResend);

        findViewById(R.id.btnConfirm).setOnClickListener(v -> verifyCode());
    }

    private void setupPinInputs() {
        for (int i = 0; i < pins.length; i++) {
            final int index = i;
            pins[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < pins.length - 1) {
                        pins[index + 1].requestFocus();
                    }
                    if (s.length() == 0 && index > 0) {
                        pins[index - 1].requestFocus();
                    }
                    // Auto verify when all filled
                    if (index == pins.length - 1 && s.length() == 1) {
                        verifyCode();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
        pins[0].requestFocus();
    }

    private void verifyCode() {
        StringBuilder code = new StringBuilder();
        for (EditText pin : pins) {
            String text = pin.getText().toString();
            if (text.isEmpty()) {
                Toast.makeText(this, "Введите полный код", Toast.LENGTH_SHORT).show();
                return;
            }
            code.append(text);
        }

        // Accept demo code 123456 or any 6-digit code for demo purposes
        if (code.toString().equals(DEMO_CODE) || code.length() == 6) {
            if (userId != -1) {
                UserFileManager.getInstance(this).saveSession(userId);
            }
            Toast.makeText(this, R.string.success_login, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, HubActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, R.string.error_wrong_code, Toast.LENGTH_SHORT).show();
            clearPins();
        }
    }

    private void clearPins() {
        for (EditText pin : pins) pin.setText("");
        pins[0].requestFocus();
    }

    private void startCountdown(TextView tvResend) {
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(45000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                tvResend.setText(String.format("Отправить код повторно через %02d:%02d",
                        seconds / 60, seconds % 60));
            }

            @Override
            public void onFinish() {
                tvResend.setText("Отправить код повторно");
                tvResend.setOnClickListener(v -> startCountdown(tvResend));
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
