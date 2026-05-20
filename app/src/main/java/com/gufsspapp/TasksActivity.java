package com.gufsspapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.gufsspapp.adapters.TaskAdapter;
import com.gufsspapp.db.UserFileManager;
import com.gufsspapp.models.Task;

import java.util.List;
import java.util.stream.Collectors;

public class TasksActivity extends AppCompatActivity {

    private TaskAdapter adapter;
    private UserFileManager db;
    private List<Task> allTasks;
    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        db = UserFileManager.getInstance(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvTasks);
        rv.setLayoutManager(new LinearLayoutManager(this));

        allTasks = db.getAllTasks();

        adapter = new TaskAdapter(allTasks, (task, anchor) -> showTaskMenu(task, anchor));
        rv.setAdapter(adapter);

        // Tabs
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                filterTasks();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // FAB
        findViewById(R.id.fabAdd).setOnClickListener(v -> showAddTaskDialog());

        setupBottomNav();
    }

    private void filterTasks() {
        List<Task> filtered;
        switch (currentTab) {
            case 1: // in_progress
                filtered = allTasks.stream()
                        .filter(t -> t.getStatus().equals("in_progress") || t.getStatus().equals("urgent") || t.getStatus().equals("important"))
                        .collect(Collectors.toList());
                break;
            case 2: // done
                filtered = allTasks.stream()
                        .filter(t -> t.getStatus().equals("done"))
                        .collect(Collectors.toList());
                break;
            default:
                filtered = allTasks;
        }
        adapter.updateData(filtered);
    }

    private void showTaskMenu(Task task, View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add(0, 0, 0, "Изменить статус");
        popup.getMenu().add(0, 1, 0, "Удалить");
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 0) {
                showStatusDialog(task);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.confirm_delete)
                        .setPositiveButton("Да", (d, w) -> {
                            db.deleteTask(task.getId());
                            allTasks.remove(task);
                            filterTasks();
                            Toast.makeText(this, "Задача удалена", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Нет", null)
                        .show();
            }
            return true;
        });
        popup.show();
    }

    private void showStatusDialog(Task task) {
        String[] statuses = {"Срочно", "Важно", "В работе", "Выполнено"};
        String[] statusKeys = {"urgent", "important", "in_progress", "done"};
        new AlertDialog.Builder(this)
                .setTitle("Выберите статус")
                .setItems(statuses, (d, which) -> {
                    task.setStatus(statusKeys[which]);
                    db.updateTask(task);
                    filterTasks();
                })
                .show();
    }

    private void showAddTaskDialog() {
        android.widget.EditText etTitle = new android.widget.EditText(this);
        etTitle.setHint("Название задачи");
        etTitle.setTextColor(0xFFE6EDF3);
        etTitle.setHintTextColor(0xFF6E7681);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.new_task))
                .setView(etTitle)
                .setPositiveButton(getString(R.string.save), (d, w) -> {
                    String title = etTitle.getText().toString().trim();
                    if (!title.isEmpty()) {
                        Task task = new Task();
                        task.setTitle(title);
                        task.setDescription("Новая задача");
                        task.setStatus("in_progress");
                        task.setDeadline("31.05.2024");
                        task.setAssignedTo("Иванов И.И.");
                        task.setCreatedBy("Иванов И.И.");
                        task.setCreatedDate("15.05.2024");
                        db.addTask(task);
                        allTasks.add(task);
                        filterTasks();
                        Toast.makeText(this, R.string.task_created, Toast.LENGTH_SHORT).show();
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
