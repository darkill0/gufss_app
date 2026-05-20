package com.gufsspapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.gufsspapp.db.UserFileManager;
import com.gufsspapp.models.User;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private UserFileManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = UserFileManager.getInstance(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnLoginCode = findViewById(R.id.btnLoginCode);
        TextView tvCantLogin = findViewById(R.id.tvCantLogin);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            User user = db.getUserByEmailAndPassword(email, password);
            if (user != null) {
                // Go to 2FA
                Intent intent = new Intent(this, TwoFactorActivity.class);
                intent.putExtra("userId", user.getId());
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.error_wrong_credentials, Toast.LENGTH_SHORT).show();
            }
        });

        btnLoginCode.setOnClickListener(v -> {
            // Simple code login - just go to 2FA with default user
            Intent intent = new Intent(this, TwoFactorActivity.class);
            intent.putExtra("userId", 1);
            startActivity(intent);
        });

        tvCantLogin.setOnClickListener(v -> {
            Toast.makeText(this, "Обратитесь к администратору системы", Toast.LENGTH_LONG).show();
        });
    }
}
