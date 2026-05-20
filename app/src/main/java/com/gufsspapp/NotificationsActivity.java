package com.gufsspapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.gufsspapp.adapters.NotificationAdapter;
import com.gufsspapp.db.UserFileManager;
import com.gufsspapp.models.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationsActivity extends AppCompatActivity {

    private NotificationAdapter adapter;
    private UserFileManager db;
    private List<Notification> allNotifications;
    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        db = UserFileManager.getInstance(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvNotifications);
        rv.setLayoutManager(new LinearLayoutManager(this));

        allNotifications = db.getAllNotifications();

        adapter = new NotificationAdapter(allNotifications, notification -> {
            db.markNotificationRead(notification.getId());
            notification.setRead(true);
            adapter.notifyDataSetChanged();
        });
        rv.setAdapter(adapter);

        // Tabs
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                filterNotifications();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Bottom Nav
        setupBottomNav();
    }

    private void filterNotifications() {
        List<Notification> filtered;
        switch (currentTab) {
            case 1: // unread
                filtered = allNotifications.stream()
                        .filter(n -> !n.isRead())
                        .collect(Collectors.toList());
                break;
            case 2: // important
                filtered = allNotifications.stream()
                        .filter(Notification::isImportant)
                        .collect(Collectors.toList());
                break;
            default:
                filtered = allNotifications;
        }
        adapter.updateData(filtered);
    }

    private void setupBottomNav() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navProfile = findViewById(R.id.navProfile);
        LinearLayout navPanel = findViewById(R.id.navPanel);

        navHome.setOnClickListener(v ->
                startActivity(new Intent(this, HubActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
        navPanel.setOnClickListener(v ->
                startActivity(new Intent(this, AdminPanelActivity.class)));
    }
}
