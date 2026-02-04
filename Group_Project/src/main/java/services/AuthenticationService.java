package org.example.services;

import org.example.entities.User;
import org.example.repositories.UserRepository;
import org.example.factories.UserFactory;

public class AuthenticationService {
    private UserRepository userRepository;
    private User currentUser;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean login(String username, String password) {
        User user = userRepository.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean hasPermission(String requiredRole) {
        if (currentUser == null) return false;
        return currentUser.hasPermission(requiredRole);
    }

    public boolean register(String username, String password, String email, String role) {
        User user = null;
        switch (role.toUpperCase()) {
            case "ADMIN":
                user = UserFactory.createAdmin(username, password, email);
                break;
            case "MANAGER":
                user = UserFactory.createManager(username, password, email);
                break;
            case "EDITOR":
                user = UserFactory.createEditor(username, password, email);
                break;
            default:
                user = UserFactory.createCustomer(username, password, email);
        }
        return userRepository.createUser(user);
    }
}