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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gufsspapp.R;
import com.gufsspapp.adapters.AdminUserAdapter;
import com.gufsspapp.db.UserFileManager;
import com.gufsspapp.models.User;

import java.util.List;
import java.util.stream.Collectors;

public class AdminUsersFragment extends Fragment {

    private AdminUserAdapter adapter;
    private UserFileManager db;
    private List<User> allUsers;
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_users, container, false);

        db = UserFileManager.getInstance(requireContext());
        allUsers = db.getAllUsers();

        RecyclerView rv = view.findViewById(R.id.rvUsers);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new AdminUserAdapter(allUsers, user -> showEditUserDialog(user));
        rv.setAdapter(adapter);

        EditText etSearch = view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString();
                filterUsers();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        view.findViewById(R.id.btnAdd).setOnClickListener(v -> showAddUserDialog());

        return view;
    }

    private void filterUsers() {
        List<User> filtered = allUsers;
        if (!searchQuery.isEmpty()) {
            String q = searchQuery.toLowerCase();
            filtered = allUsers.stream()
                    .filter(u -> u.getName().toLowerCase().contains(q) ||
                            u.getEmail().toLowerCase().contains(q) ||
                            u.getPosition().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }
        adapter.updateData(filtered);
    }

    private void showAddUserDialog() {
        LinearLayout layout = buildUserForm(null);

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.new_user))
                .setView(layout)
                .setPositiveButton(getString(R.string.save), (d, w) -> {
                    EditText etName = layout.findViewWithTag("etName");
                    EditText etEmail = layout.findViewWithTag("etEmail");
                    EditText etPassword = layout.findViewWithTag("etPassword");
                    EditText etPosition = layout.findViewWithTag("etPosition");
                    EditText etDept = layout.findViewWithTag("etDept");
                    EditText etPhone = layout.findViewWithTag("etPhone");
                    Spinner spRole = layout.findViewWithTag("spRole");

                    String name = etName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(requireContext(),
                                getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] roleKeys = {"employee", "head", "admin"};
                    String role = roleKeys[spRole.getSelectedItemPosition()];

                    User user = new User();
                    user.setName(name);
                    user.setEmail(email);
                    user.setPassword(password);
                    user.setRole(role);
                    user.setPosition(etPosition.getText().toString().trim());
                    user.setDepartment(etDept.getText().toString().trim());
                    user.setPhone(etPhone.getText().toString().trim());
                    user.setActive(true);

                    db.addUser(user);
                    allUsers = db.getAllUsers();
                    filterUsers();
                    Toast.makeText(requireContext(),
                            getString(R.string.user_added), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void showEditUserDialog(User user) {
        String[] options = {"Редактировать", "Удалить"};
        new AlertDialog.Builder(requireContext())
                .setTitle(user.getName())
                .setItems(options, (d, which) -> {
                    if (which == 0) {
                        showEditForm(user);
                    } else {
                        new AlertDialog.Builder(requireContext())
                                .setTitle(getString(R.string.confirm_delete))
                                .setMessage("Удалить пользователя " + user.getName() + "?")
                                .setPositiveButton("Удалить", (dd, ww) -> {
                                    db.deleteUser(user.getId());
                                    allUsers = db.getAllUsers();
                                    filterUsers();
                                    Toast.makeText(requireContext(),
                                            getString(R.string.user_deleted), Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("Отмена", null)
                                .show();
                    }
                })
                .show();
    }

    private void showEditForm(User user) {
        LinearLayout layout = buildUserForm(user);

        new AlertDialog.Builder(requireContext())
                .setTitle("Редактирование: " + user.getName())
                .setView(layout)
                .setPositiveButton(getString(R.string.save), (d, w) -> {
                    EditText etName = layout.findViewWithTag("etName");
                    EditText etEmail = layout.findViewWithTag("etEmail");
                    EditText etPassword = layout.findViewWithTag("etPassword");
                    EditText etPosition = layout.findViewWithTag("etPosition");
                    EditText etDept = layout.findViewWithTag("etDept");
                    EditText etPhone = layout.findViewWithTag("etPhone");
                    Spinner spRole = layout.findViewWithTag("spRole");

                    String name = etName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();

                    if (name.isEmpty() || email.isEmpty()) {
                        Toast.makeText(requireContext(),
                                getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] roleKeys = {"employee", "head", "admin"};
                    String role = roleKeys[spRole.getSelectedItemPosition()];

                    user.setName(name);
                    user.setEmail(email);
                    if (!etPassword.getText().toString().isEmpty()) {
                        user.setPassword(etPassword.getText().toString().trim());
                    }
                    user.setRole(role);
                    user.setPosition(etPosition.getText().toString().trim());
                    user.setDepartment(etDept.getText().toString().trim());
                    user.setPhone(etPhone.getText().toString().trim());

                    db.updateUser(user);
                    allUsers = db.getAllUsers();
                    filterUsers();
                    Toast.makeText(requireContext(), "Данные обновлены", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private LinearLayout buildUserForm(@Nullable User user) {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = dpToPx(20);
        layout.setPadding(pad, dpToPx(12), pad, dpToPx(8));

        String[] hints = {"Полное имя", "Email (служебный)", "Пароль",
                "Должность", "Подразделение", "Телефон"};
        String[] tags = {"etName", "etEmail", "etPassword",
                "etPosition", "etDept", "etPhone"};
        String[] values = user != null ? new String[]{
                user.getName(), user.getEmail(), "",
                user.getPosition(), user.getDepartment(), user.getPhone()
        } : new String[]{"", "", "", "", "", ""};

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dpToPx(8), 0, 0);

        for (int i = 0; i < hints.length; i++) {
            EditText et = new EditText(requireContext());
            et.setHint(hints[i]);
            et.setText(values[i]);
            et.setTag(tags[i]);
            et.setTextColor(0xFFE6EDF3);
            et.setHintTextColor(0xFF6E7681);
            et.setBackgroundResource(R.drawable.input_bg);
            et.setPadding(dpToPx(10), dpToPx(8), dpToPx(10), dpToPx(8));
            if (i > 0) et.setLayoutParams(params);
            if (tags[i].equals("etPassword")) {
                et.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            layout.addView(et);
        }

        // Role spinner
        Spinner spRole = new Spinner(requireContext());
        spRole.setTag("spRole");
        String[] roles = {"Сотрудник", "Руководитель", "Администратор"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);
        spRole.setLayoutParams(params);

        if (user != null) {
            switch (user.getRole()) {
                case "head": spRole.setSelection(1); break;
                case "admin": spRole.setSelection(2); break;
                default: spRole.setSelection(0);
            }
        }

        layout.addView(spRole);
        return layout;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
