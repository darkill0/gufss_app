package com.gufsspapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    private int currentMonth = 4; // May (0-based)
    private int currentYear = 2024;
    private int selectedDay = 15;
    private TextView tvMonthYear;
    private LinearLayout llCalendarGrid;
    private TextView tvSelectedDate;
    private LinearLayout llEvents;

    // Sample events
    private final String[][] events = {
            {"15", "11:00", "Совещание руководителей", "Конференц-зал"},
            {"15", "14:00", "Подведение итогов месяца", "Каб. 305"},
            {"15", "16:30", "Обучение сотрудников", "Учебный класс"},
            {"20", "10:00", "Планёрка отдела", "Каб. 201"},
            {"22", "14:00", "Рабочая встреча", "Конференц-зал"},
            {"28", "09:00", "Итоговое совещание", "Актовый зал"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        tvMonthYear = findViewById(R.id.tvMonthYear);
        llCalendarGrid = findViewById(R.id.llCalendarGrid);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        llEvents = findViewById(R.id.llEvents);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnPrevMonth).setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < 0) {
                currentMonth = 11;
                currentYear--;
            }
            buildCalendar();
        });

        findViewById(R.id.btnNextMonth).setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > 11) {
                currentMonth = 0;
                currentYear++;
            }
            buildCalendar();
        });

        findViewById(R.id.fabAdd).setOnClickListener(v ->
                Toast.makeText(this, "Добавление события", Toast.LENGTH_SHORT).show());

        buildCalendar();
        showEventsForDay(selectedDay);
        setupBottomNav();
    }

    private void buildCalendar() {
        llCalendarGrid.removeAllViews();

        String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        tvMonthYear.setText(months[currentMonth] + " " + currentYear);

        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonth, 1);

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        // Convert to Monday-first (1=Mon, 7=Sun)
        int startOffset = (firstDayOfWeek == Calendar.SUNDAY) ? 6 : firstDayOfWeek - 2;

        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int day = 1;
        int totalCells = startOffset + daysInMonth;
        int rows = (int) Math.ceil(totalCells / 7.0);

        for (int row = 0; row < rows; row++) {
            LinearLayout weekRow = new LinearLayout(this);
            weekRow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 0, 0, dpToPx(4));
            weekRow.setLayoutParams(rowParams);

            for (int col = 0; col < 7; col++) {
                int cellIndex = row * 7 + col;
                final int cellDay = (cellIndex < startOffset || day > daysInMonth) ? -1 : day++;

                TextView tvDay = new TextView(this);
                LinearLayout.LayoutParams cellParams = new LinearLayout.LayoutParams(
                        0, dpToPx(36), 1f);
                tvDay.setLayoutParams(cellParams);
                tvDay.setGravity(Gravity.CENTER);
                tvDay.setTextSize(14f);

                if (cellDay == -1) {
                    tvDay.setText("");
                } else {
                    tvDay.setText(String.valueOf(cellDay));

                    boolean isToday = (cellDay == 15 && currentMonth == 4 && currentYear == 2024);
                    boolean isSelected = (cellDay == selectedDay && currentMonth == 4 && currentYear == 2024);
                    boolean hasEvent = hasEventOnDay(cellDay);
                    boolean isWeekend = (col >= 5);

                    if (isSelected || isToday) {
                        tvDay.setBackground(getDrawable(R.drawable.calendar_today));
                        tvDay.setTextColor(Color.WHITE);
                    } else if (hasEvent) {
                        tvDay.setBackground(getDrawable(R.drawable.calendar_event));
                        tvDay.setTextColor(Color.WHITE);
                        tvDay.setAlpha(0.7f);
                    } else if (isWeekend) {
                        tvDay.setTextColor(Color.parseColor("#8B949E"));
                        tvDay.setBackground(null);
                    } else {
                        tvDay.setTextColor(Color.parseColor("#E6EDF3"));
                        tvDay.setBackground(null);
                    }

                    final int finalDay = cellDay;
                    tvDay.setOnClickListener(v -> {
                        selectedDay = finalDay;
                        buildCalendar();
                        showEventsForDay(finalDay);
                    });
                }

                weekRow.addView(tvDay);
            }
            llCalendarGrid.addView(weekRow);
        }
    }

    private boolean hasEventOnDay(int day) {
        for (String[] event : events) {
            if (Integer.parseInt(event[0]) == day) return true;
        }
        return false;
    }

    private void showEventsForDay(int day) {
        llEvents.removeAllViews();

        String[] months = {"января", "февраля", "марта", "апреля", "мая", "июня",
                "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        tvSelectedDate.setText(day + " " + months[currentMonth] + " " + currentYear);

        boolean hasEvents = false;
        for (String[] event : events) {
            if (Integer.parseInt(event[0]) == day) {
                hasEvents = true;
                addEventCard(event[1], event[2], event[3]);
            }
        }

        if (!hasEvents) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Нет мероприятий на этот день");
            tvEmpty.setTextColor(Color.parseColor("#8B949E"));
            tvEmpty.setTextSize(14f);
            tvEmpty.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
            llEvents.addView(tvEmpty);
        }
    }

    private void addEventCard(String time, String title, String location) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setBackgroundResource(R.drawable.card_bg);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, dpToPx(8));
        card.setLayoutParams(cardParams);
        card.setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12));

        // Time column
        TextView tvTime = new TextView(this);
        tvTime.setText(time);
        tvTime.setTextColor(Color.parseColor("#2EA043"));
        tvTime.setTextSize(13f);
        tvTime.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(
                dpToPx(50), LinearLayout.LayoutParams.WRAP_CONTENT);
        tvTime.setLayoutParams(timeParams);

        // Vertical divider

        View divider = new View(this);
        LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(
                dpToPx(2), LinearLayout.LayoutParams.MATCH_PARENT);
        divParams.setMargins(dpToPx(8), 0, dpToPx(12), 0);
        divider.setLayoutParams(divParams);
        divider.setBackgroundColor(Color.parseColor("#2EA043"));

        // Info column
        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextColor(Color.parseColor("#E6EDF3"));
        tvTitle.setTextSize(14f);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvLoc = new TextView(this);
        tvLoc.setText(location);
        tvLoc.setTextColor(Color.parseColor("#8B949E"));
        tvLoc.setTextSize(12f);

        info.addView(tvTitle);
        info.addView(tvLoc);

        card.addView(tvTime);
        card.addView(divider);
        card.addView(info);
        llEvents.addView(card);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
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
