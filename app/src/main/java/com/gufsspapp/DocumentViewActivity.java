package com.gufsspapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gufsspapp.db.UserFileManager;
import com.gufsspapp.models.Document;

public class DocumentViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_view);

        int docId = getIntent().getIntExtra("docId", -1);
        UserFileManager db = UserFileManager.getInstance(this);

        Document doc = null;
        for (Document d : db.getAllDocuments()) {
            if (d.getId() == docId) {
                doc = d;
                break;
            }
        }

        if (doc == null) {
            finish();
            return;
        }

        final Document finalDoc = doc;

        // Header
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(doc.getTitle());

        // Doc info
        ((TextView) findViewById(R.id.tvDocTitle)).setText(doc.getTitle());
        ((TextView) findViewById(R.id.tvDocSize)).setText(doc.getSize());
        ((TextView) findViewById(R.id.tvType)).setText(getTypeDisplay(doc.getType()));
        ((TextView) findViewById(R.id.tvAuthor)).setText(doc.getAuthor());
        ((TextView) findViewById(R.id.tvCreatedDate)).setText(doc.getCreatedDate());
        ((TextView) findViewById(R.id.tvDescription)).setText(doc.getDescription());

        // Status
        TextView tvStatus = findViewById(R.id.tvStatus);
        setStatusView(tvStatus, doc.getStatus());

        // Approval route
        LinearLayout llRoute = findViewById(R.id.llApprovalRoute);
        addRouteItem(llRoute, "Петров П.П.", "Начальник отдела", "10.05.2024 09:15", "approved");
        addRouteItem(llRoute, "Сидоров С.С.", "Заместитель руководителя", "", "pending");

        // Back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // More
        findViewById(R.id.btnMore).setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add("Редактировать");
            popup.getMenu().add("Удалить");
            popup.setOnMenuItemClickListener(item -> {
                if ("Удалить".equals(item.getTitle())) {
                    db.deleteDocument(finalDoc.getId());
                    Toast.makeText(this, "Документ удалён", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Редактирование недоступно в демо-режиме", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
            popup.show();
        });

        // Download / Open
        findViewById(R.id.btnDownload).setOnClickListener(v ->
                Toast.makeText(this, "Скачивание недоступно в демо-режиме", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnOpen).setOnClickListener(v ->
                Toast.makeText(this, "Открытие недоступно в демо-режиме", Toast.LENGTH_SHORT).show());
    }

    private String getTypeDisplay(String type) {
        switch (type) {
            case "report": return "Отчёт";
            case "decree": return "Приказ";
            case "memo": return "Служебная записка";
            case "request": return "Запрос";
            case "resolution": return "Постановление";
            default: return type;
        }
    }

    private void setStatusView(TextView tv, String status) {
        switch (status) {
            case "approved":
                tv.setText("Согласован");
                tv.setTextColor(Color.parseColor("#2EA043"));
                tv.setBackgroundResource(R.drawable.tag_done);
                break;
            case "pending":
                tv.setText("Не согласован");
                tv.setTextColor(Color.parseColor("#E3902B"));
                tv.setBackgroundResource(R.drawable.tag_important);
                break;
            case "rejected":
                tv.setText("Отклонён");
                tv.setTextColor(Color.parseColor("#DA3633"));
                tv.setBackgroundResource(R.drawable.tag_urgent);
                break;
            default:
                tv.setText(status);
        }
    }

    private void addRouteItem(LinearLayout container, String name, String position,
                              String date, String status) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dpToPx(10));
        row.setLayoutParams(params);

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvName = new TextView(this);
        tvName.setText(name);
        tvName.setTextColor(Color.parseColor("#E6EDF3"));
        tvName.setTextSize(14f);

        TextView tvPos = new TextView(this);
        tvPos.setText(position + (date.isEmpty() ? "" : " · " + date));
        tvPos.setTextColor(Color.parseColor("#8B949E"));
        tvPos.setTextSize(12f);

        info.addView(tvName);
        info.addView(tvPos);

        TextView tvStatus = new TextView(this);
        if ("approved".equals(status)) {
            tvStatus.setText("Согласован");
            tvStatus.setTextColor(Color.parseColor("#2EA043"));
        } else {
            tvStatus.setText("Ожидает");
            tvStatus.setTextColor(Color.parseColor("#E3902B"));
        }
        tvStatus.setTextSize(12f);

        row.addView(info);
        row.addView(tvStatus);
        container.addView(row);

        // Divider
        if (container.getChildCount() < 2) {
            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(Color.parseColor("#21262D"));
            container.addView(divider);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
