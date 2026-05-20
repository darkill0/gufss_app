package com.gufsspapp.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gufsspapp.R;
import com.gufsspapp.adapters.TaskAdapter;
import com.gufsspapp.db.UserFileManager;
import com.gufsspapp.models.Task;

import java.util.List;
import java.util.stream.Collectors;

public class AdminTasksFragment extends Fragment {

    private TaskAdapter adapter;
    private UserFileManager db;
    private List<Task> allTasks;
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_tasks, container, false);

        db = UserFileManager.getInstance(requireContext());
        allTasks = db.getAllTasks();

        RecyclerView rv = view.findViewById(R.id.rvTasks);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new TaskAdapter(allTasks, (task, anchor) -> showTaskMenu(task, anchor));
        rv.setAdapter(adapter);

        EditText etSearch = view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString();
                filterTasks();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        view.findViewById(R.id.btnAdd).setOnClickListener(v -> showAddTaskDialog());

        return view;
    }

    private void filterTasks() {
        List<Task> filtered = allTasks;
        if (!searchQuery.isEmpty()) {
            String q = searchQuery.toLowerCase();
            filtered = allTasks.stream()
                    .filter(t -> t.getTitle().toLowerCase().contains(q) ||
                            t.getAssignedTo().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }
        adapter.updateData(filtered);
    }

    private void showTaskMenu(Task task, View anchor) {
        PopupMenu popup = new PopupMenu(requireContext(), anchor);
        popup.getMenu().add(0, 0, 0, "Изменить статус");
        popup.getMenu().add(0, 1, 0, "Удалить");
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 0) {
                String[] statuses = {"Срочно", "Важно", "В работе", "Выполнено"};
                String[] statusKeys = {"urgent", "important", "in_progress", "done"};
                new AlertDialog.Builder(requireContext())
                        .setTitle("Выберите статус")
                        .setItems(statuses, (d, which) -> {
                            task.setStatus(statusKeys[which]);
                            db.updateTask(task);
                            filterTasks();
                        })
                        .show();
            } else {
                new AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.confirm_delete))
                        .setPositiveButton("Удалить", (d, w) -> {
                            db.deleteTask(task.getId());
                            allTasks = db.getAllTasks();
                            filterTasks();
                            Toast.makeText(requireContext(),
                                    "Задача удалена", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
            }
            return true;
        });
        popup.show();
    }

    private void showAddTaskDialog() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = dpToPx(20);
        layout.setPadding(pad, dpToPx(12), pad, dpToPx(8));

        EditText etTitle = createEditText("Название задачи");
        EditText etDesc = createEditText("Описание");
        EditText etAssigned = createEditText("Исполнитель");
        EditText etDeadline = createEditText("Срок (дд.мм.гггг)");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dpToPx(8), 0, 0);

        etDesc.setLayoutParams(params);
        etAssigned.setLayoutParams(params);
        etDeadline.setLayoutParams(params);

        Spinner spStatus = new Spinner(requireContext());
        String[] statuses = {"Срочно", "Важно", "В работе", "Выполнено"};
        ArrayAdapter<String> sa = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, statuses);
        sa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(sa);
        spStatus.setLayoutParams(params);

        layout.addView(etTitle);
        layout.addView(etDesc);
        layout.addView(etAssigned);
        layout.addView(etDeadline);
        layout.addView(spStatus);

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.new_task))
                .setView(layout)
                .setPositiveButton(getString(R.string.save), (d, w) -> {
                    String title = etTitle.getText().toString().trim();
                    if (title.isEmpty()) {
                        Toast.makeText(requireContext(),
                                getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] statusKeys = {"urgent", "important", "in_progress", "done"};

                    Task task = new Task();
                    task.setTitle(title);
                    task.setDescription(etDesc.getText().toString().trim().isEmpty() ?
                            "Описание не указано" : etDesc.getText().toString().trim());
                    task.setAssignedTo(etAssigned.getText().toString().trim().isEmpty() ?
                            "Иванов И.И." : etAssigned.getText().toString().trim());
                    task.setDeadline(etDeadline.getText().toString().trim().isEmpty() ?
                            "31.05.2024" : etDeadline.getText().toString().trim());
                    task.setStatus(statusKeys[spStatus.getSelectedItemPosition()]);
                    task.setCreatedBy("Администратор");
                    task.setCreatedDate("15.05.2024");

                    db.addTask(task);
                    allTasks = db.getAllTasks();
                    filterTasks();
                    Toast.makeText(requireContext(),
                            getString(R.string.task_created), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private EditText createEditText(String hint) {
        EditText et = new EditText(requireContext());
        et.setHint(hint);
        et.setTextColor(0xFFE6EDF3);
        et.setHintTextColor(0xFF6E7681);
        et.setBackgroundResource(R.drawable.input_bg);
        et.setPadding(dpToPx(10), dpToPx(8), dpToPx(10), dpToPx(8));
        return et;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
