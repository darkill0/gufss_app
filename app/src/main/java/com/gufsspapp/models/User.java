package com.gufsspapp.models;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String role; // employee, head, admin
    private String position;
    private String department;
    private String phone;
    private boolean isActive;

    public User() {}

    public User(int id, String name, String email, String password, String role,
                String position, String department, String phone, boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.position = position;
        this.department = department;
        this.phone = phone;
        this.isActive = isActive;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getInitials() {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < Math.min(parts.length, 3); i++) {
            if (!parts[i].isEmpty()) {
                initials.append(parts[i].charAt(0));
            }
        }
        return initials.toString().toUpperCase();
    }

    public String toFileString() {
        return id + "|" + name + "|" + email + "|" + password + "|" + role + "|"
                + position + "|" + department + "|" + phone + "|" + (isActive ? "1" : "0");
    }

    public static User fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 9) return null;
        try {
            return new User(
                    Integer.parseInt(parts[0].trim()),
                    parts[1].trim(),
                    parts[2].trim(),
                    parts[3].trim(),
                    parts[4].trim(),
                    parts[5].trim(),
                    parts[6].trim(),
                    parts[7].trim(),
                    "1".equals(parts[8].trim())
            );
        } catch (Exception e) {
            return null;
        }
    }
}
