package com.gufsspapp.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gufsspapp.R;
import com.gufsspapp.adapters.NotificationAdapter;
import com.gufsspapp.db.UserFileManager;
import com.gufsspapp.models.Notification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AdminNotificationsFragment extends Fragment {

    private NotificationAdapter adapter;
    private UserFileManager db;
    private List<Notification> allNotifs;
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_notifications, container, false);

        db = UserFileManager.getInstance(requireContext());
        allNotifs = db.getAllNotifications();

        RecyclerView rv = view.findViewById(R.id.rvNotifications);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new NotificationAdapter(allNotifs, notif -> showNotifOptions(notif));
        rv.setAdapter(adapter);

        EditText etSearch = view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString();
                filterNotifs();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        view.findViewById(R.id.btnAdd).setOnClickListener(v -> showAddNotifDialog());

        return view;
    }

    private void filterNotifs() {
        List<Notification> filtered = allNotifs;
        if (!searchQuery.isEmpty()) {
            String q = searchQuery.toLowerCase();
            filtered = allNotifs.stream()
                    .filter(n -> n.getTitle().toLowerCase().contains(q) ||
                            n.getDescription().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }
        adapter.updateData(filtered);
    }

    private void showNotifOptions(Notification notif) {
        new AlertDialog.Builder(requireContext())
                .setTitle(notif.getTitle())
                .setMessage(notif.getDescription())
                .setPositiveButton("Удалить", (d, w) -> {
                    db.deleteNotification(notif.getId());
                    allNotifs = db.getAllNotifications();
                    filterNotifs();
                    Toast.makeText(requireContext(), "Уведомление удалено", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Закрыть", null)
                .show();
    }

    private void showAddNotifDialog() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = dpToPx(20);
        layout.setPadding(pad, dpToPx(12), pad, dpToPx(8));

        EditText etTitle = createEditText("Заголовок");
        EditText etDesc = createEditText("Описание");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dpToPx(8), 0, 0);

        etDesc.setLayoutParams(params);

        CheckBox cbImportant = new CheckBox(requireContext());
        cbImportant.setText("Важное");
        cbImportant.setTextColor(0xFFE6EDF3);
        cbImportant.setLayoutParams(params);

        layout.addView(etTitle);
        layout.addView(etDesc);
        layout.addView(cbImportant);

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.new_notification))
                .setView(layout)
                .setPositiveButton(getString(R.string.save), (d, w) -> {
                    String title = etTitle.getText().toString().trim();
                    if (title.isEmpty()) {
                        Toast.makeText(requireContext(),
                                getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String time = new SimpleDateFormat("HH:mm", Locale.getDefault())
                            .format(new Date());

                    Notification notif = new Notification();
                    notif.setTitle(title);
                    notif.setDescription(etDesc.getText().toString().trim());
                    notif.setTime(time);
                    notif.setRead(false);
                    notif.setImportant(cbImportant.isChecked());

                    db.addNotification(notif);
                    allNotifs = db.getAllNotifications();
                    filterNotifs();
                    Toast.makeText(requireContext(),
                            getString(R.string.notification_created), Toast.LENGTH_SHORT).show();
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
