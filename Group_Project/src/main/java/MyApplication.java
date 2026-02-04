package org.example;

import org.example.entities.User;
import org.example.entities.Order;
import org.example.entities.OrderItem;
import org.example.entities.Book;
import org.example.entities.Category;
import org.example.services.*;
import org.example.repositories.*;
import org.example.validators.UserValidator;
import org.example.strategies.*;

import java.util.Scanner;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

public class MyApplication {
    private final Scanner scanner;
    private AuthenticationService authService;
    private BookStoreService bookStoreService;
    private CategoryRepository categoryRepository;
    private UserRepository userRepository;
    private OrderRepository orderRepository;
    private boolean isRunning;

    public MyApplication() {
        this.scanner = new Scanner(System.in);
        this.isRunning = true;
        initializeServices();
    }

    private void initializeServices() {
        try {
            BookRepository bookRepo = new BookRepository();
            orderRepository = new OrderRepository();
            categoryRepository = new CategoryRepository();
            userRepository = new UserRepository();

            authService = new AuthenticationService(userRepository);
            bookStoreService = new BookStoreService(bookRepo, orderRepository, categoryRepository, authService);

            System.out.println("Services initialized successfully!");

        } catch (Exception e) {
            System.out.println("Failed to initialize services: " + e.getMessage());
            isRunning = false;
        }
    }

    public void start() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║     WELCOME TO ONLINE BOOKSTORE       ║");
        System.out.println("╚════════════════════════════════════════╝");

        while (isRunning) {
            if (authService.getCurrentUser() == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
        scanner.close();
        System.out.println("Thank you for using Online Bookstore!");
    }

    private void showLoginMenu() {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("            LOGIN MENU");
        System.out.println("════════════════════════════════════════");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. View Books (Guest)");
        System.out.println("4. Exit");
        System.out.print("Choose option: ");

        try {
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> login();
                case 2 -> register();
                case 3 -> browseBooksAsGuest();
                case 4 -> isRunning = false;
                default -> System.out.println("Invalid option! Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error: Please enter a valid number.");
            scanner.nextLine();
        }
    }

    private void browseBooksAsGuest() {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("          BROWSE BOOKS (GUEST)");
        System.out.println("════════════════════════════════════════");

        List<Book> books = bookStoreService.searchBooks("");
        if (books.isEmpty()) {
            System.out.println("No books available.");
        } else {
            books.forEach(book -> {
                System.out.printf("ID: %d | %s by %s | $%.2f | Stock: %d%n",
                        book.getId(), book.getTitle(), book.getAuthor(),
                        book.getPrice(), book.getQuantity());
            });
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (authService.login(username, password)) {
            User currentUser = authService.getCurrentUser();
            System.out.println("\n✓ Login successful! Welcome, " + currentUser.getUsername());
            System.out.println("  Role: " + currentUser.getRole());
        } else {
            System.out.println("\n✗ Invalid credentials! Please try again.");
        }
    }

    private void register() {
        UserValidator validator = new UserValidator();

        System.out.println("\n════════════════════════════════════════");
        System.out.println("            REGISTRATION");
        System.out.println("════════════════════════════════════════");

        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Role (CUSTOMER/EDITOR/MANAGER/ADMIN): ");
        String role = scanner.nextLine().toUpperCase();

        if (!role.matches("CUSTOMER|EDITOR|MANAGER|ADMIN")) {
            System.out.println("\n✗ Invalid role! Defaulting to CUSTOMER.");
            role = "CUSTOMER";
        }

        if (!validator.validate(username, password, email)) {
            System.out.println("\n✗ Invalid input!");
            System.out.println("  • Username: 3-20 characters (letters, numbers, underscore)");
            System.out.println("  • Password: At least 6 characters");
            System.out.println("  • Email: Valid email format");
            return;
        }

        if (authService.register(username, password, email, role)) {
            System.out.println("\n✓ Registration successful! Please login.");
        } else {
            System.out.println("\n✗ Registration failed! Username or email may already exist.");
        }
    }

    private void showMainMenu() {
        User currentUser = authService.getCurrentUser();
        String role = currentUser.getRole();

        System.out.println("\n════════════════════════════════════════");
        System.out.printf("        MAIN MENU (%s: %s)%n", role, currentUser.getUsername());
        System.out.println("════════════════════════════════════════");
        System.out.println("1. Browse All Books");
        System.out.println("2. Search Books");
        System.out.println("3. Buy Book");
        System.out.println("4. View Categories");

        if (authService.hasPermission("CUSTOMER")) {
            System.out.println("5. View My Orders");
            System.out.println("6. View Profile");
        }

        if (authService.hasPermission("EDITOR")) {
            System.out.println("7. Manage Books (Editor)");
            System.out.println("8. Manage Categories (Editor)");
        }

        if (authService.hasPermission("MANAGER")) {
            System.out.println("9. View All Orders (Manager)");
            System.out.println("10. Get Order Details (Manager)");
            System.out.println("11. Sales Reports (Manager)");
        }

        if (authService.hasPermission("ADMIN")) {
            System.out.println("12. User Management (Admin)");
            System.out.println("13. System Statistics (Admin)");
        }

        System.out.println("0. Logout");
        System.out.println("00. Exit Application");
        System.out.print("Choose option: ");

        try {
            String input = scanner.nextLine();

            if (input.equals("00")) {
                isRunning = false;
                return;
            }

            int option = Integer.parseInt(input);

            switch (option) {
                case 1 -> browseBooks();
                case 2 -> searchBooks();
                case 3 -> buyBook();
                case 4 -> viewCategories();
                case 5 -> {
                    if (authService.hasPermission("CUSTOMER")) viewMyOrders();
                    else System.out.println("✗ Access denied! CUSTOMER role required.");
                }
                case 6 -> {
                    if (authService.hasPermission("CUSTOMER")) viewProfile();
                    else System.out.println("✗ Access denied!");
                }
                case 7 -> {
                    if (authService.hasPermission("EDITOR")) manageBooks();
                    else System.out.println("✗ Access denied! EDITOR role required.");
                }
                case 8 -> {
                    if (authService.hasPermission("EDITOR")) manageCategories();
                    else System.out.println("✗ Access denied! EDITOR role required.");
                }
                case 9 -> {
                    if (authService.hasPermission("MANAGER")) viewAllOrdersManager();
                    else System.out.println("✗ Access denied! MANAGER role required.");
                }
                case 10 -> {
                    if (authService.hasPermission("MANAGER")) getFullOrderDetails();
                    else System.out.println("✗ Access denied! MANAGER role required.");
                }
                case 11 -> {
                    if (authService.hasPermission("MANAGER")) showSalesReportsManager();
                    else System.out.println("✗ Access denied! MANAGER role required.");
                }
                case 12 -> {
                    if (authService.hasPermission("ADMIN")) manageUsers();
                    else System.out.println("✗ Access denied! ADMIN role required.");
                }
                case 13 -> {
                    if (authService.hasPermission("ADMIN")) showSystemStatistics();
                    else System.out.println("✗ Access denied! ADMIN role required.");
                }
                case 0 -> {
                    authService.logout();
                    System.out.println("✓ Logged out successfully!");
                }
                default -> System.out.println("✗ Invalid option!");
            }
        } catch (NumberFormatException e) {
            System.out.println("✗ Please enter a valid number!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void browseBooks() {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("          ALL AVAILABLE BOOKS");
        System.out.println("════════════════════════════════════════");

        List<Book> books = bookStoreService.getBooksSortedByPrice(true);
        if (books.isEmpty()) {
            System.out.println("No books available.");
        } else {
            books.forEach(System.out::println);
            System.out.printf("\nTotal books: %d%n", books.size());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void searchBooks() {
        System.out.print("\nEnter search keyword (title/author): ");
        String keyword = scanner.nextLine();

        System.out.println("\n════════════════════════════════════════");
        System.out.println("          SEARCH RESULTS");
        System.out.println("════════════════════════════════════════");

        List<Book> results = bookStoreService.searchBooks(keyword);
        if (results.isEmpty()) {
            System.out.println("No books found matching: " + keyword);
        } else {
            results.forEach(book -> {
                String categoryInfo = book.getCategory() != null ?
                        " | Category: " + book.getCategory().getName() : "";
                System.out.printf("ID: %d | %s by %s | $%.2f | Stock: %d%s%n",
                        book.getId(), book.getTitle(), book.getAuthor(),
                        book.getPrice(), book.getQuantity(), categoryInfo);
            });
            System.out.printf("\nFound %d book(s)%n", results.size());
        }
    }

    private void buyBook() {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("            PURCHASE BOOK");
        System.out.println("════════════════════════════════════════");

        System.out.print("Enter book ID: ");
        int bookId = scanner.nextInt();
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter your name for order: ");
        String customerName = scanner.nextLine();

        String result = bookStoreService.buyBook(bookId, quantity, customerName);
        System.out.println("\n" + result);
    }

    private void viewCategories() {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("            CATEGORIES");
        System.out.println("════════════════════════════════════════");

        List<Category> categories = categoryRepository.getAllCategories();
        if (categories.isEmpty()) {
            System.out.println("No categories available.");
        } else {
            categories.forEach(category -> {
                int bookCount = categoryRepository.getCategoryCount();
                System.out.printf("ID: %d | %s | %s%n",
                        category.getId(), category.getName(),
                        category.getDescription() != null ? category.getDescription() : "No description");
            });
        }
    }

    private void viewMyOrders() {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("            MY ORDERS");
        System.out.println("════════════════════════════════════════");

        List<Order> orders = bookStoreService.getMyOrders();
        if (orders.isEmpty()) {
            System.out.println("You have no orders yet.");
        } else {
            System.out.printf("You have %d order(s):\n", orders.size());
            orders.forEach(order -> {
                System.out.printf("\nOrder ID: %d\n", order.getId());
                System.out.printf("Date: %s\n", order.getOrderDate());
                System.out.printf("Customer: %s\n", order.getCustomerName());
                System.out.printf("Total: $%.2f\n", order.getTotalAmount());
                System.out.printf("Status: %s\n", order.getStatus());

                if (order.getItems() != null && !order.getItems().isEmpty()) {
                    System.out.println("Items:");
                    order.getItems().forEach(item -> {
                        String bookInfo = item.getBook() != null ?
                                item.getBook().getTitle() : "Book ID: " + item.getBookId();
                        System.out.printf("  - %s (Qty: %d, $%.2f each, Subtotal: $%.2f)\n",
                                bookInfo, item.getQuantity(), item.getUnitPrice(), item.getSubtotal());
                    });
                }
                System.out.println("────────────────────────────────────────");
            });
        }
    }

    private void viewAllOrdersManager() {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("          ALL ORDERS (MANAGER VIEW)");
        System.out.println("════════════════════════════════════════");

        List<Order> orders = bookStoreService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders found in the system.");
        } else {
            System.out.printf("Total orders: %d\n\n", orders.size());

            Map<String, List<Order>> ordersByStatus = orders.stream()
                    .collect(Collectors.groupingBy(Order::getStatus));

            ordersByStatus.forEach((status, statusOrders) -> {
                System.out.printf("%s Orders (%d):\n", status, statusOrders.size());
                System.out.println("────────────────────────────────────────");

                statusOrders.forEach(order -> {
                    System.out.printf("ID: %d | Date: %s | Customer: %s | User ID: %d\n",
                            order.getId(), order.getOrderDate().toLocalDate(),
                            order.getCustomerName(), order.getUserId());
                    System.out.printf("  Total: $%.2f | Status: %s\n",
                            order.getTotalAmount(), order.getStatus());

                    int totalItems = order.getItems() != null ?
                            order.getItems().stream()
                                    .mapToInt(OrderItem::getQuantity)
                                    .sum() : 0;
                    System.out.printf("  Total Items: %d\n", totalItems);

                    if ("PENDING".equals(order.getStatus())) {
                        System.out.println("  [Can be cancelled]");
                    }
                    System.out.println();
                });
            });

            double totalRevenue = orders.stream()
                    .filter(o -> !"CANCELLED".equals(o.getStatus()))
                    .mapToDouble(Order::getTotalAmount)
                    .sum();
            System.out.printf("\nTotal Revenue (excluding cancelled): $%.2f\n", totalRevenue);
        }
    }

    private void showSalesReportsManager() {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("          SALES REPORTS");
        System.out.println("════════════════════════════════════════");

        List<Order> allOrders = orderRepository.getAllOrders();

        if (allOrders.isEmpty()) {
            System.out.println("No sales data available.");
            return;
        }

        System.out.println("\n1. DAILY SALES (Last 7 days):");
        System.out.println("────────────────────────────────────────");

        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        List<Order> recentOrders = allOrders.stream()
                .filter(order -> order.getOrderDate().isAfter(weekAgo))
                .filter(order -> !"CANCELLED".equals(order.getStatus()))
                .collect(Collectors.toList());

        Map<LocalDate, List<Order>> ordersByDate = recentOrders.stream()
                .collect(Collectors.groupingBy(order -> order.getOrderDate().toLocalDate()));

        ordersByDate.entrySet().stream()
                .sorted(Map.Entry.<LocalDate, List<Order>>comparingByKey().reversed())
                .forEach(entry -> {
                    LocalDate date = entry.getKey();
                    List<Order> dailyOrders = entry.getValue();
                    double dailyTotal = dailyOrders.stream()
                            .mapToDouble(Order::getTotalAmount)
                            .sum();
                    System.out.printf("%s: %d orders, $%.2f\n",
                            date, dailyOrders.size(), dailyTotal);
                });

        System.out.println("\n2. ORDER STATUS BREAKDOWN:");
        System.out.println("────────────────────────────────────────");

        Map<String, Long> statusCount = allOrders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));

        statusCount.forEach((status, count) -> {
            System.out.printf("%s: %d orders\n", status, count);
        });

        System.out.println("\n3. TOP CUSTOMERS (by total spending):");
        System.out.println("────────────────────────────────────────");

        Map<String, Double> customerSpending = allOrders.stream()
                .filter(order -> !"CANCELLED".equals(order.getStatus()))
                .collect(Collectors.groupingBy(
                        Order::getCustomerName,
                        Collectors.summingDouble(Order::getTotalAmount)
                ));

        customerSpending.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    System.out.printf("%s: $%.2f\n", entry.getKey(), entry.getValue());
                });

        System.out.println("\n4. OVERALL STATISTICS:");
        System.out.println("────────────────────────────────────────");

        long totalOrders = allOrders.size();
        long completedOrders = allOrders.stream()
                .filter(order -> "COMPLETED".equals(order.getStatus()))
                .count();
        double totalRevenue = allOrders.stream()
                .filter(order -> !"CANCELLED".equals(order.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();

        System.out.printf("Total Orders: %d\n", totalOrders);
        System.out.printf("Completed Orders: %d\n", completedOrders);
        System.out.printf("Total Revenue: $%.2f\n", totalRevenue);
        System.out.printf("Average Order Value: $%.2f\n",
                totalRevenue / (completedOrders > 0 ? completedOrders : 1));
    }

    private void viewProfile() {
        User currentUser = authService.getCurrentUser();
        System.out.println("\n════════════════════════════════════════");
        System.out.println("            MY PROFILE");
        System.out.println("════════════════════════════════════════");
        System.out.println("Username: " + currentUser.getUsername());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Role: " + currentUser.getRole());
        System.out.println("Permissions: " +
                (currentUser.hasPermission("ADMIN") ? "Full Access" :
                        currentUser.hasPermission("MANAGER") ? "Manager Access" :
                                currentUser.hasPermission("EDITOR") ? "Editor Access" : "Customer Access"));
    }

    private void manageBooks() {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("          BOOK MANAGEMENT");
        System.out.println("════════════════════════════════════════");
        System.out.println("1. Add New Book");
        System.out.println("2. Update Book");
        System.out.println("3. Delete Book");
        System.out.println("4. View All Books with Details");
        System.out.println("5. Set Discount Strategy");
        System.out.print("Choose option: ");

        int option = scanner.nextInt();
        scanner.nextLine();

        switch (option) {
            case 1 -> addNewBook();
            case 4 -> viewAllBooksWithDetails();
            case 5 -> setDiscountStrategy();
            default -> System.out.println("Feature coming soon...");
        }
    }

    private void addNewBook() {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("          ADD NEW BOOK");
        System.out.println("════════════════════════════════════════");

        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();
        System.out.print("Price: ");
        double price = scanner.nextDouble();
        System.out.print("Quantity: ");
        int quantity = scanner.nextInt();
        System.out.print("Category ID (0 for none): ");
        int categoryId = scanner.nextInt();
        scanner.nextLine();

        String result = bookStoreService.createBook(title, author, price, quantity, categoryId);
        System.out.println("\n" + result);
    }

    private void viewAllBooksWithDetails() {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("      BOOKS WITH DETAILS");
        System.out.println("════════════════════════════════════════");

        bookStoreService.searchBooks("")
                .stream()
                .filter(book -> book.getCategory() != null)
                .forEach(book -> {
                    System.out.printf("ID: %d | %s by %s | Category: %s | $%.2f | Stock: %d%n",
                            book.getId(), book.getTitle(), book.getAuthor(),
                            book.getCategory().getName(), book.getPrice(), book.getQuantity());
                });
    }

    private void setDiscountStrategy() {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("      SET DISCOUNT STRATEGY");
        System.out.println("════════════════════════════════════════");
        System.out.println("1. No Discount");
        System.out.println("2. Bulk Discount (5+ books: 5%, 10+ books: 10%)");
        System.out.println("3. Category-based Discount");
        System.out.print("Choose strategy: ");

        int option = scanner.nextInt();
        scanner.nextLine();

        switch (option) {
            case 1 -> {
                bookStoreService.setDiscountStrategy(new NoDiscountStrategy());
                System.out.println("✓ No discount strategy set");
            }
            case 2 -> {
                bookStoreService.setDiscountStrategy(new BulkDiscountStrategy());
                System.out.println("✓ Bulk discount strategy set");
            }
            case 3 -> {
                System.out.print("Enter category for discount: ");
                String category = scanner.nextLine();
                System.out.print("Enter discount rate (0.1 for 10%): ");
                double rate = scanner.nextDouble();
                scanner.nextLine();
                bookStoreService.setDiscountStrategy(new CategoryDiscountStrategy(category, rate));
                System.out.println("✓ Category discount strategy set");
            }
            default -> System.out.println("✗ Invalid option!");
        }
    }

    private void manageCategories() {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("      CATEGORY MANAGEMENT");
        System.out.println("════════════════════════════════════════");
        System.out.println("1. Add New Category");
        System.out.println("2. View All Categories");
        System.out.println("3. Search Categories");
        System.out.print("Choose option: ");

        int option = scanner.nextInt();
        scanner.nextLine();

        switch (option) {
            case 1 -> addNewCategory();
            case 2 -> {
                List<Category> categories = categoryRepository.getAllCategories();
                categories.forEach(System.out::println);
            }
            case 3 -> searchCategories();
            default -> System.out.println("✗ Invalid option!");
        }
    }

    private void addNewCategory() {
        System.out.print("Category Name: ");
        String name = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();

        Category category = new Category(name, description);
        boolean success = categoryRepository.createCategory(category);

        if (success) {
            System.out.println("✓ Category added successfully! ID: " + category.getId());
        } else {
            System.out.println("✗ Failed to add category!");
        }
    }

    private void searchCategories() {
        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine();

        List<Category> results = categoryRepository.searchCategories(keyword);
        if (results.isEmpty()) {
            System.out.println("No categories found.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private void getFullOrderDetails() {
        System.out.print("\nEnter Order ID: ");
        int orderId = scanner.nextInt();
        scanner.nextLine();

        String result = bookStoreService.getFullOrderDescription(orderId);
        System.out.println("\n════════════════════════════════════════");
        System.out.println("      FULL ORDER DETAILS");
        System.out.println("════════════════════════════════════════");
        System.out.println(result);
    }

    private void manageUsers() {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("          USER MANAGEMENT");
        System.out.println("════════════════════════════════════════");
        System.out.println("1. View All Users");
        System.out.println("2. Search Users");
        System.out.println("3. Change User Role");
        System.out.println("4. View User Statistics");
        System.out.print("Choose option: ");

        int option = scanner.nextInt();
        scanner.nextLine();

        switch (option) {
            case 1 -> {
                List<User> users = userRepository.getAllUsers();
                users.forEach(user -> {
                    System.out.printf("ID: %d | Username: %s | Role: %s | Email: %s%n",
                            user.getId(), user.getUsername(), user.getRole(), user.getEmail());
                });
            }
            case 2 -> searchUsers();
            case 4 -> showUserStatistics();
            default -> System.out.println("Feature coming soon...");
        }
    }

    private void searchUsers() {
        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine();

        List<User> results = userRepository.searchUsers(keyword);
        if (results.isEmpty()) {
            System.out.println("No users found.");
        } else {
            results.forEach(user -> {
                System.out.printf("ID: %d | %s (%s) - %s%n",
                        user.getId(), user.getUsername(), user.getRole(), user.getEmail());
            });
        }
    }

    private void showUserStatistics() {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("          USER STATISTICS");
        System.out.println("════════════════════════════════════════");

        int totalUsers = userRepository.getUserCount();
        int admins = userRepository.getUserCountByRole("ADMIN");
        int managers = userRepository.getUserCountByRole("MANAGER");
        int editors = userRepository.getUserCountByRole("EDITOR");
        int customers = userRepository.getUserCountByRole("CUSTOMER");

        System.out.printf("Total Users: %d%n", totalUsers);
        System.out.printf("Admins: %d%n", admins);
        System.out.printf("Managers: %d%n", managers);
        System.out.printf("Editors: %d%n", editors);
        System.out.printf("Customers: %d%n", customers);
    }

    private void showSystemStatistics() {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("       SYSTEM STATISTICS");
        System.out.println("════════════════════════════════════════");

        int totalUsers = userRepository.getUserCount();
        int totalCategories = categoryRepository.getCategoryCount();

        System.out.printf("Total Users: %d%n", totalUsers);
        System.out.printf("Total Categories: %d%n", totalCategories);
        System.out.println("\nAdditional statistics coming soon...");
    }
}