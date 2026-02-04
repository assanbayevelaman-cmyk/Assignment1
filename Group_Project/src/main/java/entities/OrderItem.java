package org.example.entities;

public class OrderItem {
    private int id;
    private int orderId;
    private int bookId;
    private int quantity;
    private double unitPrice;
    private double subtotal;
    private Book book;

    public OrderItem() {}

    public OrderItem(int orderId, int bookId, int quantity, double unitPrice) {
        this.orderId = orderId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = quantity * unitPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.subtotal = this.quantity * this.unitPrice;
    }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.subtotal = this.quantity * this.unitPrice;
    }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    @Override
    public String toString() {
        String bookInfo = book != null ?
                ", book=" + book.getTitle() + " (by " + book.getAuthor() + ")" :
                ", bookId=" + bookId;
        return "OrderItem{id=" + id + bookInfo + ", quantity=" + quantity +
                ", unitPrice=" + unitPrice + ", subtotal=" + subtotal + "}";
    }
}