package com.gufsspapp.models;

public class Employee {
    private int id;
    private String name;
    private String position;
    private String department;
    private String phone;
    private boolean isFavorite;

    public Employee() {}

    public Employee(int id, String name, String position, String department,
                    String phone, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.department = department;
        this.phone = phone;
        this.isFavorite = isFavorite;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public String getInitials() {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < Math.min(parts.length, 2); i++) {
            if (!parts[i].isEmpty()) initials.append(parts[i].charAt(0));
        }
        return initials.toString().toUpperCase();
    }

    public String toFileString() {
        return id + "|" + esc(name) + "|" + esc(position) + "|" + esc(department) + "|" +
                phone + "|" + (isFavorite ? "1" : "0");
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("|", "§");
    }

    public static Employee fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 6) return null;
        try {
            return new Employee(
                    Integer.parseInt(parts[0].trim()),
                    parts[1].trim().replace("§", "|"),
                    parts[2].trim().replace("§", "|"),
                    parts[3].trim().replace("§", "|"),
                    parts[4].trim(),
                    "1".equals(parts[5].trim())
            );
        } catch (Exception e) {
            return null;
        }
    }
}
