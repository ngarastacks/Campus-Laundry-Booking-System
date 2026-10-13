package com.campuslaundry.service;

import com.campuslaundry.model.Role;
import com.campuslaundry.model.User;
import com.campuslaundry.repository.UserRepository;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User register(String studentId, String name, Role role) {
        if (repository.existsById(studentId)) {
            throw new IllegalArgumentException("A user with ID " + studentId + " already exists.");
        }
        User user = new User(studentId, name, role);
        repository.save(user);
        return user;
    }

    // Methods BookingService will call

    public Optional<User> findByStudentId(String id) {
        return repository.findById(id);
    }

    public boolean isActive(String id) {
        Optional<User> user = repository.findById(id);
        return user.isPresent() && user.get().isActive();
    }

    // Extras for the console menu

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public void deactivate(String id) {
        setActive(id, false);
    }

    public void reactivate(String id) {
        setActive(id, true);
    }

    private void setActive(String id, boolean active) {
        Optional<User> user = repository.findById(id);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("No user with ID " + id);
        }
        user.get().setActive(active);
    }
}
