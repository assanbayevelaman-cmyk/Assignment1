package org.example.validators;

import java.util.regex.Pattern;

public class UserValidator {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    public boolean validate(String username, String password, String email) {
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return false;
        }
        if (password == null || password.length() < 6) {
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return false;
        }
        return true;
    }
}