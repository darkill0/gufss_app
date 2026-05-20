package com.gufsspapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gufsspapp.db.UserFileManager;
import com.gufsspapp.models.User;

public class ProfileActivity extends AppCompatActivity {

    private UserFileManager db;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = UserFileManager.getInstance(this);
        int userId = db.getSessionUserId();
        currentUser = db.getUserById(userId);

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        setupProfile();
        setupActions();
        setupBottomNav();
    }

    private void setupProfile() {
        TextView tvAvatarInitials = findViewById(R.id.tvAvatarInitials);
        TextView tvFullName = findViewById(R.id.tvFullName);
        TextView tvPosition = findViewById(R.id.tvPosition);
        TextView tvDepartment = findViewById(R.id.tvDepartment);

        tvAvatarInitials.setText(currentUser.getInitials());
        tvFullName.setText(currentUser.getName());
        tvPosition.setText(currentUser.getPosition());
        tvDepartment.setText(currentUser.getDepartment());
    }

    private void setupActions() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSettings).setOnClickListener(v ->
                Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show());

        findViewById(R.id.rowPersonal).setOnClickListener(v -> showPersonalDataDialog());
        findViewById(R.id.rowDept).setOnClickListener(v ->
                Toast.makeText(this, "Подразделение: " + currentUser.getDepartment(), Toast.LENGTH_LONG).show());
        findViewById(R.id.rowContact).setOnClickListener(v ->
                Toast.makeText(this, "Телефон: " + currentUser.getPhone() +
                        "\nEmail: " + currentUser.getEmail(), Toast.LENGTH_LONG).show());
        findViewById(R.id.rowPassword).setOnClickListener(v -> showChangePasswordDialog());

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Выход из системы")
                    .setMessage("Вы уверены, что хотите выйти?")
                    .setPositiveButton("Выйти", (d, w) -> {
                        db.clearSession();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });
    }

    private void showPersonalDataDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(20), dpToPx(16), dpToPx(20), dpToPx(8));

        EditText etName = new EditText(this);
        etName.setHint("Имя");
        etName.setText(currentUser.getName());
        etName.setTextColor(0xFFE6EDF3);
        etName.setHintTextColor(0xFF6E7681);
        layout.addView(etName);

        EditText etPosition = new EditText(this);
        etPosition.setHint("Должность");
        etPosition.setText(currentUser.getPosition());
        etPosition.setTextColor(0xFFE6EDF3);
        etPosition.setHintTextColor(0xFF6E7681);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dpToPx(12), 0, 0);
        etPosition.setLayoutParams(params);
        layout.addView(etPosition);

        new AlertDialog.Builder(this)
                .setTitle("Личные данные")
                .setView(layout)
                .setPositiveButton("Сохранить", (d, w) -> {
                    String newName = etName.getText().toString().trim();
                    String newPos = etPosition.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        currentUser.setName(newName);
                        currentUser.setPosition(newPos);
                        db.updateUser(currentUser);
                        setupProfile();
                        Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showChangePasswordDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(20), dpToPx(16), dpToPx(20), dpToPx(8));

        EditText etOld = new EditText(this);
        etOld.setHint("Текущий пароль");
        etOld.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etOld.setTextColor(0xFFE6EDF3);
        etOld.setHintTextColor(0xFF6E7681);
        layout.addView(etOld);

        EditText etNew = new EditText(this);
        etNew.setHint("Новый пароль");
        etNew.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etNew.setTextColor(0xFFE6EDF3);
        etNew.setHintTextColor(0xFF6E7681);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dpToPx(12), 0, 0);
        etNew.setLayoutParams(params);
        layout.addView(etNew);

        EditText etConfirm = new EditText(this);
        etConfirm.setHint("Подтвердите пароль");
        etConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etConfirm.setTextColor(0xFFE6EDF3);
        etConfirm.setHintTextColor(0xFF6E7681);
        etConfirm.setLayoutParams(params);
        layout.addView(etConfirm);

        new AlertDialog.Builder(this)
                .setTitle("Изменить пароль")
                .setView(layout)
                .setPositiveButton("Сохранить", (d, w) -> {
                    String oldPass = etOld.getText().toString().trim();
                    String newPass = etNew.getText().toString().trim();
                    String confirmPass = etConfirm.getText().toString().trim();

                    if (!currentUser.getPassword().equals(oldPass)) {
                        Toast.makeText(this, "Неверный текущий пароль", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newPass.length() < 6) {
                        Toast.makeText(this, "Пароль должен быть не менее 6 символов", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!newPass.equals(confirmPass)) {
                        Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    currentUser.setPassword(newPass);
                    db.updateUser(currentUser);
                    Toast.makeText(this, "Пароль изменён", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void setupBottomNav() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navProfile = findViewById(R.id.navProfile);
        LinearLayout navPanel = findViewById(R.id.navPanel);

        navHome.setOnClickListener(v ->
                startActivity(new Intent(this, HubActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        navProfile.setOnClickListener(v -> {}); // already here
        navPanel.setOnClickListener(v ->
                startActivity(new Intent(this, AdminPanelActivity.class)));
    }
}
