package com.gufsspapp.db;

import android.content.Context;
import android.util.Log;

import com.gufsspapp.models.Document;
import com.gufsspapp.models.Employee;
import com.gufsspapp.models.Notification;
import com.gufsspapp.models.Task;
import com.gufsspapp.models.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class UserFileManager {
    private static final String TAG = "UserFileManager";
    private static final String USERS_FILE = "users.txt";
    private static final String TASKS_FILE = "tasks.txt";
    private static final String DOCUMENTS_FILE = "documents.txt";
    private static final String NOTIFICATIONS_FILE = "notifications.txt";
    private static final String EMPLOYEES_FILE = "employees.txt";

    private final Context context;
    private static UserFileManager instance;

    private UserFileManager(Context context) {
        this.context = context.getApplicationContext();
        initDefaultData();
    }

    public static synchronized UserFileManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserFileManager(context);
        }
        return instance;
    }

    private void initDefaultData() {
        if (!getFile(USERS_FILE).exists()) {
            createDefaultUsers();
        }
        if (!getFile(TASKS_FILE).exists()) {
            createDefaultTasks();
        }
        if (!getFile(DOCUMENTS_FILE).exists()) {
            createDefaultDocuments();
        }
        if (!getFile(NOTIFICATIONS_FILE).exists()) {
            createDefaultNotifications();
        }
        if (!getFile(EMPLOYEES_FILE).exists()) {
            createDefaultEmployees();
        }
    }

    private File getFile(String filename) {
        return new File(context.getFilesDir(), filename);
    }

    private List<String> readLines(String filename) {
        List<String> lines = new ArrayList<>();
        File file = getFile(filename);
        if (!file.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.startsWith("#")) {
                    lines.add(line.trim());
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading file: " + filename, e);
        }
        return lines;
    }

    private void writeLines(String filename, List<String> lines) {
        File file = getFile(filename);
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, false))) {
            for (String line : lines) {
                pw.println(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error writing file: " + filename, e);
        }
    }

    // ========== USERS ==========

    private void createDefaultUsers() {
        List<String> lines = new ArrayList<>();
        lines.add("# id|name|email|password|role|position|department|phone|active");
        lines.add("1|Иванов Иван Иванович|ivanov@gufss.ru|password123|admin|Главный специалист|Отдел организационного обеспечения|8-495-111-22-33|1");
        lines.add("2|Петров Петр Петрович|petrov@gufss.ru|password123|head|Начальник отдела|Отдел по работе с обращениями|8-495-123-45-67|1");
        lines.add("3|Сидоров Сергей Сергеевич|sidorov@gufss.ru|password123|head|Заместитель руководителя|Административный отдел|8-495-234-56-78|1");
        lines.add("4|Иванова Анна Алексеевна|ivanova@gufss.ru|password123|employee|Ведущий специалист|Отдел документооборота|8-495-345-67-89|1");
        lines.add("5|Кузнецов Максим Михайлович|kuznetsov@gufss.ru|password123|employee|Ведущий специалист|Отдел IT|8-495-456-78-90|1");
        lines.add("6|Смирнова Елена Васильевна|smirnova@gufss.ru|password123|employee|Специалист 1 категории|Отдел бухгалтерии|8-495-567-89-01|1");
        writeLines(USERS_FILE, lines);
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        for (String line : readLines(USERS_FILE)) {
            User u = User.fromFileString(line);
            if (u != null) users.add(u);
        }
        return users;
    }

    public User getUserByEmailAndPassword(String email, String password) {
        for (User u : getAllUsers()) {
            if (u.getEmail().equalsIgnoreCase(email.trim()) &&
                    u.getPassword().equals(password.trim())) {
                return u;
            }
        }
        return null;
    }

    public User getUserById(int id) {
        for (User u : getAllUsers()) {
            if (u.getId() == id) return u;
        }
        return null;
    }

    public boolean addUser(User user) {
        List<User> users = getAllUsers();
        int maxId = 0;
        for (User u : users) maxId = Math.max(maxId, u.getId());
        user.setId(maxId + 1);
        users.add(user);
        return saveAllUsers(users);
    }

    public boolean updateUser(User user) {
        List<User> users = getAllUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                users.set(i, user);
                return saveAllUsers(users);
            }
        }
        return false;
    }

    public boolean deleteUser(int id) {
        List<User> users = getAllUsers();
        users.removeIf(u -> u.getId() == id);
        return saveAllUsers(users);
    }

    private boolean saveAllUsers(List<User> users) {
        List<String> lines = new ArrayList<>();
        lines.add("# id|name|email|password|role|position|department|phone|active");
        for (User u : users) lines.add(u.toFileString());
        writeLines(USERS_FILE, lines);
        return true;
    }

    // ========== TASKS ==========

    private void createDefaultTasks() {
        List<String> lines = new ArrayList<>();
        lines.add("# id|title|description|status|deadline|assignedTo|createdBy|createdDate");
        lines.add("1|Проверка ИП №12345|Провести проверку по исполнительному производству|urgent|15.05.2024|Иванов И.И.|Петров П.П.|10.05.2024");
        lines.add("2|Подготовка отчёта|Подготовить еженедельный отчёт по отделу|important|16.05.2024|Иванов И.И.|Петров П.П.|10.05.2024");
        lines.add("3|Анализ задолженности|Проанализировать задолженность по алиментам|in_progress|17.05.2024|Иванов И.И.|Сидоров С.С.|11.05.2024");
        lines.add("4|Подготовка ответа|Подготовить ответ на обращение гражданина|in_progress|18.05.2024|Иванов И.И.|Петров П.П.|12.05.2024");
        lines.add("5|Обновление базы данных|Обновить базу данных должников|done|14.05.2024|Иванов И.И.|Сидоров С.С.|08.05.2024");
        lines.add("6|Составление акта|Составить акт об исполнительных действиях|done|13.05.2024|Иванов И.И.|Петров П.П.|07.05.2024");
        lines.add("7|Служебная записка|Написать служебную записку о необходимости ресурсов|in_progress|20.05.2024|Иванов И.И.|Иванов И.И.|13.05.2024");
        writeLines(TASKS_FILE, lines);
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        for (String line : readLines(TASKS_FILE)) {
            Task t = Task.fromFileString(line);
            if (t != null) tasks.add(t);
        }
        return tasks;
    }

    public boolean addTask(Task task) {
        List<Task> tasks = getAllTasks();
        int maxId = 0;
        for (Task t : tasks) maxId = Math.max(maxId, t.getId());
        task.setId(maxId + 1);
        tasks.add(task);
        return saveAllTasks(tasks);
    }

    public boolean updateTask(Task task) {
        List<Task> tasks = getAllTasks();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.set(i, task);
                return saveAllTasks(tasks);
            }
        }
        return false;
    }

    public boolean deleteTask(int id) {
        List<Task> tasks = getAllTasks();
        tasks.removeIf(t -> t.getId() == id);
        return saveAllTasks(tasks);
    }

    private boolean saveAllTasks(List<Task> tasks) {
        List<String> lines = new ArrayList<>();
        lines.add("# id|title|description|status|deadline|assignedTo|createdBy|createdDate");
        for (Task t : tasks) lines.add(t.toFileString());
        writeLines(TASKS_FILE, lines);
        return true;
    }

    // ========== DOCUMENTS ==========

    private void createDefaultDocuments() {
        List<String> lines = new ArrayList<>();
        lines.add("# id|title|description|type|author|createdDate|status|category|size");
        lines.add("1|Приказ №45 от 10.05.2024|О внесении изменений в регламент|decree|Иванов И.И.|10.05.2024|approved|incoming|PDF - 0.8 MB");
        lines.add("2|Отчёт за май 2024|Ежемесячный отчёт о проделанной работе за май 2024 года.|report|Иванов И.И.|09.05.2024|pending|outgoing|PDF - 1.2 MB");
        lines.add("3|Служебная записка|О необходимости предоставления информации|memo|Петров П.П.|08.05.2024|approved|incoming|PDF - 0.3 MB");
        lines.add("4|Постановление о возбуждении ИП №56789/23|Постановление о возбуждении исполнительного производства|resolution|Сидоров С.С.|07.05.2024|approved|incoming|PDF - 0.5 MB");
        lines.add("5|Запрос информации|В УФНС России по МО|request|Иванов И.И.|06.05.2024|pending|outgoing|PDF - 0.2 MB");
        lines.add("6|Протокол совещания|Протокол еженедельного совещания|memo|Петров П.П.|05.05.2024|approved|incoming|PDF - 0.4 MB");
        writeLines(DOCUMENTS_FILE, lines);
    }

    public List<Document> getAllDocuments() {
        List<Document> docs = new ArrayList<>();
        for (String line : readLines(DOCUMENTS_FILE)) {
            Document d = Document.fromFileString(line);
            if (d != null) docs.add(d);
        }
        return docs;
    }

    public boolean addDocument(Document doc) {
        List<Document> docs = getAllDocuments();
        int maxId = 0;
        for (Document d : docs) maxId = Math.max(maxId, d.getId());
        doc.setId(maxId + 1);
        docs.add(doc);
        return saveAllDocuments(docs);
    }

    public boolean updateDocument(Document doc) {
        List<Document> docs = getAllDocuments();
        for (int i = 0; i < docs.size(); i++) {
            if (docs.get(i).getId() == doc.getId()) {
                docs.set(i, doc);
                return saveAllDocuments(docs);
            }
        }
        return false;
    }

    public boolean deleteDocument(int id) {
        List<Document> docs = getAllDocuments();
        docs.removeIf(d -> d.getId() == id);
        return saveAllDocuments(docs);
    }

    private boolean saveAllDocuments(List<Document> docs) {
        List<String> lines = new ArrayList<>();
        lines.add("# id|title|description|type|author|createdDate|status|category|size");
        for (Document d : docs) lines.add(d.toFileString());
        writeLines(DOCUMENTS_FILE, lines);
        return true;
    }

    // ========== NOTIFICATIONS ==========

    private void createDefaultNotifications() {
        List<String> lines = new ArrayList<>();
        lines.add("# id|title|description|time|isRead|isImportant");
        lines.add("1|Новое поручение|Назначена задача Проверка ИП №12345|10:30|0|1");
        lines.add("2|Изменение документа|Документ Отчёт за май изменён|09:15|0|0");
        lines.add("3|Совещание|Напоминание о совещании 15.05.2024 в 11:00|Вчера|1|1");
        lines.add("4|Системное сообщение|Обновление регламента работы с обращениями|Вчера|1|0");
        lines.add("5|Новое поручение|Назначена задача Анализ задолженности|13:05|0|0");
        writeLines(NOTIFICATIONS_FILE, lines);
    }

    public List<Notification> getAllNotifications() {
        List<Notification> notifs = new ArrayList<>();
        for (String line : readLines(NOTIFICATIONS_FILE)) {
            Notification n = Notification.fromFileString(line);
            if (n != null) notifs.add(n);
        }
        return notifs;
    }

    public boolean addNotification(Notification notif) {
        List<Notification> notifs = getAllNotifications();
        int maxId = 0;
        for (Notification n : notifs) maxId = Math.max(maxId, n.getId());
        notif.setId(maxId + 1);
        notifs.add(notif);
        return saveAllNotifications(notifs);
    }

    public boolean deleteNotification(int id) {
        List<Notification> notifs = getAllNotifications();
        notifs.removeIf(n -> n.getId() == id);
        return saveAllNotifications(notifs);
    }

    public boolean markNotificationRead(int id) {
        List<Notification> notifs = getAllNotifications();
        for (Notification n : notifs) {
            if (n.getId() == id) {
                n.setRead(true);
                return saveAllNotifications(notifs);
            }
        }
        return false;
    }

    private boolean saveAllNotifications(List<Notification> notifs) {
        List<String> lines = new ArrayList<>();
        lines.add("# id|title|description|time|isRead|isImportant");
        for (Notification n : notifs) lines.add(n.toFileString());
        writeLines(NOTIFICATIONS_FILE, lines);
        return true;
    }

    // ========== EMPLOYEES ==========

    private void createDefaultEmployees() {
        List<String> lines = new ArrayList<>();
        lines.add("# id|name|position|department|phone|isFavorite");
        lines.add("1|Петров Петр Петрович|Начальник отдела|Отдел по работе с обращениями|8-495-123-45-67|1");
        lines.add("2|Сидоров Сергей Сергеевич|Заместитель руководителя|Административный отдел|8-495-234-56-78|0");
        lines.add("3|Иванова Анна Алексеевна|Ведущий специалист|Отдел документооборота|8-495-345-67-89|0");
        lines.add("4|Кузнецов Максим Михайлович|Ведущий специалист|Отдел IT|8-495-456-78-90|1");
        lines.add("5|Смирнова Елена Васильевна|Специалист 1 категории|Отдел бухгалтерии|8-495-567-89-01|0");
        lines.add("6|Козлов Дмитрий Александрович|Специалист 2 категории|Отдел организационного обеспечения|8-495-678-90-12|0");
        lines.add("7|Николаева Ольга Петровна|Специалист|Архивный отдел|8-495-789-01-23|0");
        writeLines(EMPLOYEES_FILE, lines);
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        for (String line : readLines(EMPLOYEES_FILE)) {
            Employee e = Employee.fromFileString(line);
            if (e != null) employees.add(e);
        }
        return employees;
    }

    public boolean toggleFavorite(int id) {
        List<Employee> employees = getAllEmployees();
        for (Employee e : employees) {
            if (e.getId() == id) {
                e.setFavorite(!e.isFavorite());
                return saveAllEmployees(employees);
            }
        }
        return false;
    }

    private boolean saveAllEmployees(List<Employee> employees) {
        List<String> lines = new ArrayList<>();
        lines.add("# id|name|position|department|phone|isFavorite");
        for (Employee e : employees) lines.add(e.toFileString());
        writeLines(EMPLOYEES_FILE, lines);
        return true;
    }

    // ========== SESSION ==========

    private static final String SESSION_FILE = "session.txt";

    public void saveSession(int userId) {
        List<String> lines = new ArrayList<>();
        lines.add(String.valueOf(userId));
        writeLines(SESSION_FILE, lines);
    }

    public int getSessionUserId() {
        List<String> lines = readLines(SESSION_FILE);
        if (lines.isEmpty()) return -1;
        try {
            return Integer.parseInt(lines.get(0).trim());
        } catch (Exception e) {
            return -1;
        }
    }

    public void clearSession() {
        File f = getFile(SESSION_FILE);
        if (f.exists()) f.delete();
    }
}
