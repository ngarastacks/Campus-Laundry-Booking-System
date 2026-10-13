package com.campuslaundry.repository;

import com.campuslaundry.model.User;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserRepository {

    private final Map<String, User> users = new LinkedHashMap<>();

    public void save(User user) {
        users.put(user.getStudentId(), user);
    }

    public Optional<User> findById(String studentId) {
        if (studentId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(users.get(studentId.trim()));
    }

    public boolean existsById(String studentId) {
        return studentId != null && users.containsKey(studentId.trim());
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public boolean deleteById(String studentId) {
        return studentId != null && users.remove(studentId.trim()) != null;
    }
}
