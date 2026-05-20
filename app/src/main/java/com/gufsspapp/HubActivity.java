package com.gufsspapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gufsspapp.db.UserFileManager;
import com.gufsspapp.models.User;

public class HubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);

        UserFileManager db = UserFileManager.getInstance(this);
        int userId = db.getSessionUserId();
        User user = db.getUserById(userId);

        // Set user name
        TextView tvUserName = findViewById(R.id.tvUserName);
        if (user != null) {
            String[] parts = user.getName().split("\\s+");
            if (parts.length >= 2) {
                String shortName = parts[0] + " " + parts[1].charAt(0) + "." +
                        (parts.length > 2 ? parts[2].charAt(0) + "." : "");
                tvUserName.setText(shortName);
            } else {
                tvUserName.setText(user.getName());
            }
        }

        // Notification badge
        long unreadCount = db.getAllNotifications().stream()
                .filter(n -> !n.isRead()).count();
        TextView tvBadge = findViewById(R.id.tvNotifBadge);
        tvBadge.setText(String.valueOf(unreadCount));

        // Notification bell
        findViewById(R.id.btnNotifBell).setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        // Cards
        FrameLayout cardNotifications = findViewById(R.id.cardNotifications);
        FrameLayout cardTasks = findViewById(R.id.cardTasks);
        FrameLayout cardDocuments = findViewById(R.id.cardDocuments);
        FrameLayout cardCalendar = findViewById(R.id.cardCalendar);
        FrameLayout cardEmployees = findViewById(R.id.cardEmployees);
        FrameLayout cardAnalytics = findViewById(R.id.cardAnalytics);
        LinearLayout cardAI = findViewById(R.id.cardAI);

        cardNotifications.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));
        cardTasks.setOnClickListener(v ->
                startActivity(new Intent(this, TasksActivity.class)));
        cardDocuments.setOnClickListener(v ->
                startActivity(new Intent(this, DocumentsActivity.class)));
        cardCalendar.setOnClickListener(v ->
                startActivity(new Intent(this, CalendarActivity.class)));
        cardEmployees.setOnClickListener(v ->
                startActivity(new Intent(this, EmployeesActivity.class)));
        cardAnalytics.setOnClickListener(v ->
                startActivity(new Intent(this, AdminPanelActivity.class)));
        cardAI.setOnClickListener(v ->
                startActivity(new Intent(this, AiAssistantActivity.class)));

        // Bottom Nav
        setupBottomNav(user);
    }

    private void setupBottomNav(User user) {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navProfile = findViewById(R.id.navProfile);
        LinearLayout navPanel = findViewById(R.id.navPanel);

        navHome.setOnClickListener(v -> {}); // already here

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        navPanel.setOnClickListener(v -> {
            if (user != null && user.getRole().equals("admin")) {
                startActivity(new Intent(this, AdminPanelActivity.class));
            } else {
                startActivity(new Intent(this, AdminPanelActivity.class));
            }
        });
    }
}
