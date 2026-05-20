package com.gufsspapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.gufsspapp.adapters.DocumentAdapter;
import com.gufsspapp.db.UserFileManager;
import com.gufsspapp.models.Document;

import java.util.List;
import java.util.stream.Collectors;

public class DocumentsActivity extends AppCompatActivity {

    private DocumentAdapter adapter;
    private UserFileManager db;
    private List<Document> allDocuments;
    private int currentTab = 0;
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        db = UserFileManager.getInstance(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvDocuments);
        rv.setLayoutManager(new LinearLayoutManager(this));

        allDocuments = db.getAllDocuments();

        adapter = new DocumentAdapter(allDocuments, document -> {
            Intent intent = new Intent(this, DocumentViewActivity.class);
            intent.putExtra("docId", document.getId());
            startActivity(intent);
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
                filterDocuments();
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
                filterDocuments();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // FAB
        findViewById(R.id.fabAdd).setOnClickListener(v -> showAddDocDialog());

        setupBottomNav();
    }

    private void filterDocuments() {
        List<Document> filtered = allDocuments;

        // Tab filter
        switch (currentTab) {
            case 1: // incoming
                filtered = filtered.stream()
                        .filter(d -> "incoming".equals(d.getCategory()))
                        .collect(Collectors.toList());
                break;
            case 2: // outgoing
                filtered = filtered.stream()
                        .filter(d -> "outgoing".equals(d.getCategory()))
                        .collect(Collectors.toList());
                break;
            case 3: // draft
                filtered = filtered.stream()
                        .filter(d -> "draft".equals(d.getCategory()))
                        .collect(Collectors.toList());
                break;
        }

        // Search filter
        if (!searchQuery.isEmpty()) {
            String q = searchQuery.toLowerCase();
            filtered = filtered.stream()
                    .filter(d -> d.getTitle().toLowerCase().contains(q) ||
                            d.getDescription().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }

        adapter.updateData(filtered);
    }

    private void showAddDocDialog() {
        EditText etTitle = new EditText(this);
        etTitle.setHint("Название документа");
        etTitle.setTextColor(0xFFE6EDF3);
        etTitle.setHintTextColor(0xFF6E7681);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.new_document))
                .setView(etTitle)
                .setPositiveButton(getString(R.string.save), (d, w) -> {
                    String title = etTitle.getText().toString().trim();
                    if (!title.isEmpty()) {
                        Document doc = new Document();
                        doc.setTitle(title);
                        doc.setDescription("Новый документ");
                        doc.setType("report");
                        doc.setAuthor("Иванов И.И.");
                        doc.setCreatedDate("15.05.2024");
                        doc.setStatus("pending");
                        doc.setCategory("outgoing");
                        doc.setSize("PDF - 0.1 MB");
                        db.addDocument(doc);
                        allDocuments.add(doc);
                        filterDocuments();
                        Toast.makeText(this, R.string.doc_added, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
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
