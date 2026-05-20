package com.gufsspapp.models;

public class Document {
    private int id;
    private String title;
    private String description;
    private String type; // report, decree, memo, request, resolution
    private String author;
    private String createdDate;
    private String status; // approved, pending, rejected
    private String category; // incoming, outgoing, draft
    private String size;

    public Document() {}

    public Document(int id, String title, String description, String type,
                    String author, String createdDate, String status,
                    String category, String size) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.author = author;
        this.createdDate = createdDate;
        this.status = status;
        this.category = category;
        this.size = size;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String toFileString() {
        return id + "|" + esc(title) + "|" + esc(description) + "|" + type + "|" +
                esc(author) + "|" + createdDate + "|" + status + "|" + category + "|" + size;
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("|", "§");
    }

    public static Document fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 9) return null;
        try {
            return new Document(
                    Integer.parseInt(parts[0].trim()),
                    parts[1].trim().replace("§", "|"),
                    parts[2].trim().replace("§", "|"),
                    parts[3].trim(),
                    parts[4].trim().replace("§", "|"),
                    parts[5].trim(),
                    parts[6].trim(),
                    parts[7].trim(),
                    parts[8].trim()
            );
        } catch (Exception e) {
            return null;
        }
    }
}
