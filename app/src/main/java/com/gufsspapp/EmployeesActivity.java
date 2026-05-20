package com.gufsspapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.gufsspapp.adapters.EmployeeAdapter;
import com.gufsspapp.db.UserFileManager;
import com.gufsspapp.models.Employee;

import java.util.List;
import java.util.stream.Collectors;

public class EmployeesActivity extends AppCompatActivity {

    private EmployeeAdapter adapter;
    private UserFileManager db;
    private List<Employee> allEmployees;
    private int currentTab = 0;
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees);

        db = UserFileManager.getInstance(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvEmployees);
        rv.setLayoutManager(new LinearLayoutManager(this));

        allEmployees = db.getAllEmployees();

        adapter = new EmployeeAdapter(allEmployees, employee -> {
            db.toggleFavorite(employee.getId());
            employee.setFavorite(!employee.isFavorite());
            adapter.notifyDataSetChanged();
        });
        rv.setAdapter(adapter);

        // Search
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString();
                filterEmployees();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Tabs
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                filterEmployees();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        setupBottomNav();
    }

    private void filterEmployees() {
        List<Employee> filtered = allEmployees;

        if (currentTab == 2) { // Favorites
            filtered = filtered.stream()
                    .filter(Employee::isFavorite)
                    .collect(Collectors.toList());
        }

        if (!searchQuery.isEmpty()) {
            String q = searchQuery.toLowerCase();
            filtered = filtered.stream()
                    .filter(e -> e.getName().toLowerCase().contains(q) ||
                            e.getPosition().toLowerCase().contains(q) ||
                            e.getDepartment().toLowerCase().contains(q))
                    .collect(Collectors.toList());
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
