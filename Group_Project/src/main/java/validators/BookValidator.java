package org.example.validators;

public class BookValidator {
    private String errorMessage;

    public boolean validate(String title, String author, double price, int quantity) {
        errorMessage = "";

        if (title == null || title.trim().isEmpty()) {
            errorMessage = "Title is required!";
            return false;
        }

        if (author == null || author.trim().isEmpty()) {
            errorMessage = "Author is required!";
            return false;
        }

        if (price <= 0) {
            errorMessage = "Price must be greater than 0!";
            return false;
        }

        if (price > 10000) {
            errorMessage = "Price cannot exceed $10,000!";
            return false;
        }

        if (quantity < 0) {
            errorMessage = "Quantity cannot be negative!";
            return false;
        }

        if (quantity > 10000) {
            errorMessage = "Quantity cannot exceed 10,000!";
            return false;
        }

        if (title.length() > 200) {
            errorMessage = "Title is too long! Maximum 200 characters.";
            return false;
        }

        if (author.length() > 100) {
            errorMessage = "Author name is too long! Maximum 100 characters.";
            return false;
        }

        return true;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}