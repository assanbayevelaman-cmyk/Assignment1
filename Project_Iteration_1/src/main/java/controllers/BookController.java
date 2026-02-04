package controllers;

import org.example.entities.Book;
import org.example.entities.Order;
import org.example.repositories.interfaces.IBookRepository;
import org.example.repositories.interfaces.IOrderRepository;
import java.util.List;

public class BookController {
    private final IBookRepository bookRepo;
    private final IOrderRepository orderRepo;

    public BookController(IBookRepository bookRepo, IOrderRepository orderRepo) {
        this.bookRepo = bookRepo;
        this.orderRepo = orderRepo;
    }

    public String createBook(String t, String a, double p, int q) {
        boolean created = bookRepo.createBook(new Book(t, a, p, q));
        return created ? "Book created!" : "Failed!";
    }

    public String getBook(int id) {
        Book b = bookRepo.getBook(id);
        return b == null ? "Not found" : b.toString();
    }

    public String getAllBooks() {
        List<Book> books = bookRepo.getAllBooks();
        StringBuilder sb = new StringBuilder();
        for (Book b : books) sb.append(b.toString()).append("\n");
        return sb.toString();
    }

    public String buyBook(int id, int qty, String customerName) {
        Book book = bookRepo.getBook(id);
        if (book == null) return "Book not found!";
        if (book.getQuantity() < qty) return "Insufficient stock! Available: " + book.getQuantity();
        if (qty <= 0) return "Quantity must be at least 1";
        double pricePerBook = book.getPrice();
        if (qty >= 10) {
            pricePerBook *= 0.9;
        } else if (qty >= 5) {
            pricePerBook *= 0.95;
        }
        double total = pricePerBook * qty;
        int newQuantity = book.getQuantity() - qty;
        book.setQuantity(newQuantity);
        bookRepo.updateBook(book);
        Order order = new Order(customerName, total);
        boolean orderCreated = orderRepo.createOrder(order);
        if (orderCreated) {
            String lowStockWarning = "";
            if (newQuantity < 5) {
                lowStockWarning = "\nWarning: Low stock remaining for '" + book.getTitle() + "'!";
            }
            return String.format("""
                    Purchase Successful!
                    Order ID: %d
                    Book: %s
                    Quantity: %d
                    Price per book: $%.2f
                    Total: $%.2f
                    Customer: %s%s""",
                    order.getId(), book.getTitle(), qty, pricePerBook, total, customerName, lowStockWarning);
        } else {
            book.setQuantity(book.getQuantity() + qty);
            bookRepo.updateBook(book);
            return "Order failed! Please try again.";
        }
    }
    public String getAllOrders() {
        List<Order> orders = orderRepo.getAllOrders();
        if (orders.isEmpty()) return "No orders found.";
        StringBuilder sb = new StringBuilder();
        for(Order order : orders) {
            sb.append(order.toString()).append("\n");
        }
        return sb.toString();
    }
    public String cancelOrder(int orderId) {
        Order order = orderRepo.getOrder(orderId);
        if (order == null) return "Order not found!";
        if (!"PENDING".equals(order.getStatus())) {
            return "Cannot cancel order. Current status: " + order.getStatus();
        }
        boolean updated = orderRepo.updateOrderStatus(orderId, "CANCELLED");
        return updated ? "Order cancelled successfully!" : "Failed to cancel order!";
    }
}