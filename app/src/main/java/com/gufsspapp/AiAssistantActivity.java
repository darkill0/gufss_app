package com.gufsspapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gufsspapp.adapters.ChatAdapter;
import com.gufsspapp.models.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AiAssistantActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText etMessage;
    private ChatAdapter adapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());

    // Simple AI responses
    private final String[][] responses = {
            {"срок", "Срок предоставления отчёта по исполнительному производству установлен регламентом и составляет 5 рабочих дней с момента запроса информации.\n\nИсточник:\n• Регламент работы с ИП, п. 4.2\n• Приказ №12 от 01.03.2023"},
            {"отчёт", "Ежемесячный отчёт о работе отдела предоставляется до 5-го числа следующего месяца. Форма отчёта утверждена приказом №8 от 15.01.2023."},
            {"задач", "В вашем профиле числится 7 задач: 1 срочная, 2 важные, 4 в работе. Рекомендую начать с задачи «Проверка ИП №12345»."},
            {"уведомлен", "У вас 5 непрочитанных уведомлений. Самое важное: новое поручение от 10:30."},
            {"документ", "В системе доступно 6 документов. Последний добавленный: «Приказ №45 от 10.05.2024»."},
            {"пароль", "Для смены пароля перейдите в раздел «Профиль» → «Изменить пароль». Новый пароль должен содержать не менее 8 символов."},
            {"алимент", "Анализ задолженности по алиментам проводится согласно регламенту ежеквартально. Ближайший срок — 30.06.2024."},
            {"совещан", "Ближайшее совещание запланировано на 15.05.2024 в 11:00 в конференц-зале."},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_assistant);

        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);

        adapter = new ChatAdapter(messages);
        rvChat.setAdapter(adapter);

        // Welcome message
        addAIMessage("Здравствуйте, Иванов И.И.!\nЧем могу помочь?\n\nЯ могу ответить на вопросы о:\n• Сроках и регламентах\n• Задачах и документах\n• Уведомлениях\n• Совещаниях и мероприятиях");

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSend).setOnClickListener(v -> sendMessage());

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        etMessage.setText("");
        addUserMessage(text);

        // Simulate AI thinking
        handler.postDelayed(() -> {
            String response = getAIResponse(text.toLowerCase());
            addAIMessage(response);
        }, 800);
    }

    private String getAIResponse(String query) {
        for (String[] pair : responses) {
            if (query.contains(pair[0])) {
                return pair[1];
            }
        }
        return "Извините, я не могу найти точный ответ на ваш вопрос. Попробуйте переформулировать запрос или обратитесь к документации системы.\n\nМогу помочь с вопросами о:\n• Регламентах работы\n• Сроках исполнения\n• Документах и задачах\n• Уведомлениях";
    }

    private void addUserMessage(String text) {
        messages.add(new ChatMessage(text, true, getCurrentTime()));
        adapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();
    }

    private void addAIMessage(String text) {
        messages.add(new ChatMessage(text, false, getCurrentTime()));
        adapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();
    }

    private void scrollToBottom() {
        if (messages.size() > 0) {
            rvChat.smoothScrollToPosition(messages.size() - 1);
        }
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }
}
