package com.gufsspapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.gufsspapp.fragments.AdminDocumentsFragment;
import com.gufsspapp.fragments.AdminNotificationsFragment;
import com.gufsspapp.fragments.AdminStatisticsFragment;
import com.gufsspapp.fragments.AdminTasksFragment;
import com.gufsspapp.fragments.AdminUsersFragment;

public class AdminPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() { return 5; }

            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return new AdminUsersFragment();
                    case 1: return new AdminDocumentsFragment();
                    case 2: return new AdminTasksFragment();
                    case 3: return new AdminNotificationsFragment();
                    case 4: return new AdminStatisticsFragment();
                    default: return new AdminUsersFragment();
                }
            }
        });

        String[] tabTitles = {"Пользователи", "Документы", "Задачи", "Уведомления", "Статистика"};
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])).attach();

        setupBottomNav();
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
        navPanel.setOnClickListener(v -> {}); // already here
    }
}
