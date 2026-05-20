package com.gufsspapp.models;

public class Task {
    private int id;
    private String title;
    private String description;
    private String status; // urgent, important, in_progress, done
    private String deadline;
    private String assignedTo;
    private String createdBy;
    private String createdDate;

    public Task() {}

    public Task(int id, String title, String description, String status,
                String deadline, String assignedTo, String createdBy, String createdDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.deadline = deadline;
        this.assignedTo = assignedTo;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String toFileString() {
        return id + "|" + escapeField(title) + "|" + escapeField(description) + "|" +
                status + "|" + deadline + "|" + escapeField(assignedTo) + "|" +
                escapeField(createdBy) + "|" + createdDate;
    }

    private String escapeField(String s) {
        if (s == null) return "";
        return s.replace("|", "§");
    }

    public static Task fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 8) return null;
        try {
            return new Task(
                    Integer.parseInt(parts[0].trim()),
                    parts[1].trim().replace("§", "|"),
                    parts[2].trim().replace("§", "|"),
                    parts[3].trim(),
                    parts[4].trim(),
                    parts[5].trim().replace("§", "|"),
                    parts[6].trim().replace("§", "|"),
                    parts[7].trim()
            );
        } catch (Exception e) {
            return null;
        }
    }
}
