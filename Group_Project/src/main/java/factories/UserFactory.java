package org.example.factories;

import org.example.entities.User;

public class UserFactory {
    public static User createAdmin(String username, String password, String email) {
        User user = new User(username, password, email, "ADMIN");
        return user;
    }

    public static User createManager(String username, String password, String email) {
        User user = new User(username, password, email, "MANAGER");
        return user;
    }

    public static User createEditor(String username, String password, String email) {
        User user = new User(username, password, email, "EDITOR");
        return user;
    }

    public static User createCustomer(String username, String password, String email) {
        User user = new User(username, password, email, "CUSTOMER");
        return user;
    }
}
