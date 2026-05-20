package com.gufsspapp.models;

public class Notification {
    private int id;
    private String title;
    private String description;
    private String time;
    private boolean isRead;
    private boolean isImportant;

    public Notification() {}

    public Notification(int id, String title, String description, String time,
                        boolean isRead, boolean isImportant) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.time = time;
        this.isRead = isRead;
        this.isImportant = isImportant;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public boolean isImportant() { return isImportant; }
    public void setImportant(boolean important) { isImportant = important; }

    public String toFileString() {
        return id + "|" + esc(title) + "|" + esc(description) + "|" + time + "|" +
                (isRead ? "1" : "0") + "|" + (isImportant ? "1" : "0");
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("|", "§");
    }

    public static Notification fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 6) return null;
        try {
            return new Notification(
                    Integer.parseInt(parts[0].trim()),
                    parts[1].trim().replace("§", "|"),
                    parts[2].trim().replace("§", "|"),
                    parts[3].trim(),
                    "1".equals(parts[4].trim()),
                    "1".equals(parts[5].trim())
            );
        } catch (Exception e) {
            return null;
        }
    }
}
