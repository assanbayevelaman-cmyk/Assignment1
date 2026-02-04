package org.example.controllers;

import org.example.services.BookStoreService;
import org.example.services.AuthenticationService;
import org.example.strategies.*;

public class BookController {
    private final BookStoreService bookStoreService;
    private final AuthenticationService authService;

    public BookController(BookStoreService bookStoreService, AuthenticationService authService) {
        this.bookStoreService = bookStoreService;
        this.authService = authService;
    }

    public String createBook(String title, String author, double price, int quantity, int categoryId) {
        return bookStoreService.createBook(title, author, price, quantity, categoryId);
    }

    public String getBook(int id) {
        org.example.entities.Book book = bookStoreService.getBookById(id);
        return book != null ? book.toString() : "Book not found!";
    }

    public String getAllBooks() {
        StringBuilder sb = new StringBuilder();
        bookStoreService.searchBooks("").forEach(book ->
                sb.append(book.toString()).append("\n")
        );
        return sb.toString().isEmpty() ? "No books available." : sb.toString();
    }

    public String buyBook(int id, int quantity, String customerName) {
        return bookStoreService.buyBook(id, quantity, customerName);
    }

    public String getAllOrders() {
        return "Order listing feature - use getFullOrderDescription for details.";
    }

    public String cancelOrder(int orderId) {
        return bookStoreService.cancelOrder(orderId);
    }

    public String getFullOrderDescription(int orderId) {
        return bookStoreService.getFullOrderDescription(orderId);
    }

    public String searchBooks(String keyword) {
        StringBuilder sb = new StringBuilder();
        bookStoreService.searchBooks(keyword).forEach(book ->
                sb.append(book.toString()).append("\n")
        );
        return sb.toString().isEmpty() ? "No books found." : sb.toString();
    }

    public String getBooksByCategory(int categoryId) {
        StringBuilder sb = new StringBuilder();
        bookStoreService.getBooksByCategory(categoryId).forEach(book ->
                sb.append(book.toString()).append("\n")
        );
        return sb.toString().isEmpty() ? "No books in this category." : sb.toString();
    }

    public String login(String username, String password) {
        boolean success = authService.login(username, password);
        if (success) {
            return "Login successful! Welcome, " + username;
        }
        return "Login failed! Invalid credentials.";
    }

    public String register(String username, String password, String email, String role) {
        boolean success = authService.register(username, password, email, role);
        return success ? "Registration successful!" : "Registration failed!";
    }

    public String logout() {
        authService.logout();
        return "Logged out successfully!";
    }

    public String getCurrentUserInfo() {
        if (authService.getCurrentUser() == null) {
            return "No user logged in.";
        }
        org.example.entities.User user = authService.getCurrentUser();
        return String.format("Username: %s, Role: %s, Email: %s",
                user.getUsername(), user.getRole(), user.getEmail());
    }

    public String setDiscountStrategy(String strategyType) {
        switch (strategyType.toUpperCase()) {
            case "NO_DISCOUNT":
                bookStoreService.setDiscountStrategy(new NoDiscountStrategy());
                return "No discount strategy set.";
            case "BULK_DISCOUNT":
                bookStoreService.setDiscountStrategy(new BulkDiscountStrategy());
                return "Bulk discount strategy set.";
            case "CATEGORY_DISCOUNT":
                return "Please use setCategoryDiscountStrategy(category, rate) method.";
            default:
                return "Invalid discount strategy!";
        }
    }

    public String setCategoryDiscountStrategy(String category, double discountRate) {
        bookStoreService.setDiscountStrategy(new CategoryDiscountStrategy(category, discountRate));
        return String.format("Category discount set: %s - %.0f%% off", category, discountRate * 100);
    }

    public String validateBookData(String title, String author, double price, int quantity) {
        if (title == null || title.trim().isEmpty()) {
            return "Title is required!";
        }
        if (author == null || author.trim().isEmpty()) {
            return "Author is required!";
        }
        if (price <= 0) {
            return "Price must be greater than 0!";
        }
        if (quantity < 0) {
            return "Quantity cannot be negative!";
        }
        return "Valid";
    }

    public boolean hasPermission(String requiredRole) {
        return authService.hasPermission(requiredRole);
    }

    public String getRole() {
        return authService.getCurrentUser() != null ?
                authService.getCurrentUser().getRole() : "GUEST";
    }
}