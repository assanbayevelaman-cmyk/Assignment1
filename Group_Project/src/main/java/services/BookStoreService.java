package org.example.services;

import org.example.entities.*;
import org.example.repositories.*;
import org.example.strategies.DiscountStrategy;
import org.example.strategies.BulkDiscountStrategy;
import org.example.validators.BookValidator;

import java.util.List;

public class BookStoreService {
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final AuthenticationService authService;
    private DiscountStrategy discountStrategy;

    public BookStoreService(BookRepository bookRepository, OrderRepository orderRepository,
                            CategoryRepository categoryRepository, AuthenticationService authService) {
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.categoryRepository = categoryRepository;
        this.authService = authService;
        this.discountStrategy = new BulkDiscountStrategy();
    }

    public void setDiscountStrategy(DiscountStrategy strategy) {
        this.discountStrategy = strategy;
    }

    public String createBook(String title, String author, double price, int quantity, int categoryId) {
        if (!authService.hasPermission("EDITOR")) {
            return "Access denied! Editor role required.";
        }

        BookValidator validator = new BookValidator();
        if (!validator.validate(title, author, price, quantity)) {
            return validator.getErrorMessage();
        }

        Book book = new Book(title, author, price, quantity, categoryId);
        boolean created = bookRepository.createBook(book);
        return created ? "Book created successfully!" : "Failed to create book.";
    }

    public Book getBookById(int id) {
        return bookRepository.getBook(id);
    }

    public List<Book> getBooksByCategory(int categoryId) {
        return bookRepository.getBooksByCategory(categoryId);
    }

    public String getFullOrderDescription(int orderId) {
        if (!authService.hasPermission("MANAGER")) {
            return "Access denied! Manager role required.";
        }

        Order order = orderRepository.getFullOrderDescription(orderId);
        if (order == null) {
            return "Order not found!";
        }

        return order.toString();
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> allBooks = bookRepository.getAllBooksWithCategory();
        return allBooks.stream()
                .filter(book ->
                        book.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                                book.getAuthor().toLowerCase().contains(keyword.toLowerCase())
                )
                .toList();
    }

    public List<Book> getBooksSortedByPrice(boolean ascending) {
        List<Book> books = bookRepository.getAllBooksWithCategory();

        if (ascending) {
            return books.stream()
                    .sorted((b1, b2) -> Double.compare(b1.getPrice(), b2.getPrice()))
                    .toList();
        } else {
            return books.stream()
                    .sorted((b1, b2) -> Double.compare(b2.getPrice(), b1.getPrice()))
                    .toList();
        }
    }

    public String buyBook(int bookId, int quantity, String customerName) {
        if (!authService.hasPermission("CUSTOMER")) {
            return "Please login to make a purchase.";
        }

        Book book = bookRepository.getBook(bookId);
        if (book == null) return "Book not found!";
        if (book.getQuantity() < quantity) return "Insufficient stock!";
        if (quantity <= 0) return "Quantity must be at least 1";

        double discountedPrice = discountStrategy.applyDiscount(book.getPrice(), quantity);
        double total = discountedPrice * quantity;

        User currentUser = authService.getCurrentUser();
        Order order = new Order(customerName, total, currentUser.getId());

        OrderItem item = new OrderItem(0, bookId, quantity, discountedPrice);
        order.addItem(item);

        boolean success = orderRepository.createOrderWithItems(order);

        if (success) {
            book.setQuantity(book.getQuantity() - quantity);
            bookRepository.updateBook(book);

            StringBuilder response = new StringBuilder();
            response.append(" PURCHASE SUCCESSFUL!\n");
            response.append("────────────────────────────────────────\n");
            response.append(String.format("Order ID: %d\n", order.getId()));
            response.append(String.format("Book: %s\n", book.getTitle()));
            response.append(String.format("Author: %s\n", book.getAuthor()));
            response.append(String.format("Quantity: %d\n", quantity));
            response.append(String.format("Price per book: $%.2f\n", discountedPrice));
            response.append(String.format("Total: $%.2f\n", total));
            response.append(String.format("Customer: %s\n", customerName));
            response.append(String.format("Order Date: %s\n", order.getOrderDate()));
            response.append(String.format("Status: %s\n", order.getStatus()));

            if (book.getQuantity() < 5) {
                response.append("\n  WARNING: Low stock remaining!\n");
                response.append(String.format("Only %d copies of '%s' left in stock.\n",
                        book.getQuantity(), book.getTitle()));
            }

            response.append("\n Order Tracking:\n");
            response.append("  • Order placed: Immediately\n");
            response.append("  • Processing: 1-2 business days\n");
            response.append("  • You can cancel while status is 'PENDING'\n");

            return response.toString();
        } else {
            return " Purchase failed! Please try again or contact support.";
        }
    }

    public List<Order> getMyOrders() {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }

        List<Order> orders = orderRepository.getOrdersByUser(currentUser.getId());

        orders.forEach(order -> {
            List<OrderItem> items = getOrderItems(order.getId());
            order.setItems(items);
        });

        return orders;
    }

    public List<Order> getAllOrders() {
        if (!authService.hasPermission("MANAGER")) {
            return List.of();
        }

        List<Order> orders = orderRepository.getAllOrders();

        orders.forEach(order -> {
            List<OrderItem> items = getOrderItems(order.getId());
            order.setItems(items);
        });

        return orders;
    }

    private List<OrderItem> getOrderItems(int orderId) {
        return orderRepository.getOrderItems(orderId);
    }

    public String cancelOrder(int orderId) {
        if (!authService.hasPermission("CUSTOMER")) {
            return "Access denied!";
        }

        Order order = orderRepository.getOrder(orderId);
        if (order == null) {
            return "Order not found!";
        }

        User currentUser = authService.getCurrentUser();
        if (!currentUser.hasPermission("MANAGER") && order.getUserId() != currentUser.getId()) {
            return "Access denied! You can only cancel your own orders.";
        }

        if (!"PENDING".equals(order.getStatus())) {
            return "Cannot cancel order. Current status: " + order.getStatus();
        }

        boolean cancelled = orderRepository.cancelOrder(orderId);
        if (cancelled) {
            return "Order cancelled successfully!";
        }
        return "Failed to cancel order!";
    }

    public List<Book> getAllBooks() {
        return bookRepository.getAllBooksWithCategory();
    }

    public String updateBook(int bookId, String title, String author, double price, int quantity, int categoryId) {
        if (!authService.hasPermission("EDITOR")) {
            return "Access denied! Editor role required.";
        }

        Book book = bookRepository.getBook(bookId);
        if (book == null) {
            return "Book not found!";
        }

        BookValidator validator = new BookValidator();
        if (!validator.validate(title, author, price, quantity)) {
            return validator.getErrorMessage();
        }

        book.setTitle(title);
        book.setAuthor(author);
        book.setPrice(price);
        book.setQuantity(quantity);
        book.setCategoryId(categoryId);

        boolean updated = bookRepository.updateBook(book);
        return updated ? "Book updated successfully!" : "Failed to update book.";
    }

    public String addCategory(String name, String description) {
        if (!authService.hasPermission("EDITOR")) {
            return "Access denied! Editor role required.";
        }

        if (name == null || name.trim().isEmpty()) {
            return "Category name is required!";
        }

        Category category = new Category(name.trim(), description != null ? description.trim() : "");
        boolean created = categoryRepository.createCategory(category);
        return created ? "Category added successfully!" : "Failed to add category. Name might already exist.";
    }

    public List<Category> getAllCategories() {
        return categoryRepository.getAllCategories();
    }

    public Category getCategory(int id) {
        return categoryRepository.getCategory(id);
    }

    public List<Category> searchCategories(String keyword) {
        return categoryRepository.searchCategories(keyword);
    }

    public String getStatistics() {
        if (!authService.hasPermission("ADMIN")) {
            return "Access denied! Admin role required.";
        }

        int bookCount = bookRepository.getAllBooks().size();
        int orderCount = orderRepository.getAllOrders().size();
        int userCount = 0;
        int categoryCount = categoryRepository.getAllCategories().size();

        return String.format("""
                System Statistics:
                Total Books: %d
                Total Orders: %d
                Total Categories: %d
                Total Users: %d (user count coming soon)""",
                bookCount, orderCount, categoryCount, userCount);
    }
}