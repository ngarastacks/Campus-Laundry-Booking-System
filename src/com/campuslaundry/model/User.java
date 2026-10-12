package com.campuslaundry.model;

import java.util.Objects;

public class User {

    private final String studentId;
    private String name;
    private Role role;
    private boolean active;
    private String email;
    private String phoneNumber;

    public User(String studentId, String name, Role role) {
        this(studentId, name, role, null, null);
    }

    public User(String studentId, String name, Role role, String email, String phoneNumber) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null.");
        }
        this.studentId = studentId.trim();
        this.name = name.trim();
        this.role = role;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.active = true;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name.trim();
    }

    public void setRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null.");
        }
        this.role = role;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return studentId.equals(((User) o).studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return studentId + " | " + name + " | " + role + (active ? "" : " (inactive)");
    }
}
