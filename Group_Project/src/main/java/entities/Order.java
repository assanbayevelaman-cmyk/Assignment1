package org.example.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private LocalDateTime orderDate;
    private String customerName;
    private double totalAmount;
    private String status;
    private int userId;
    private List<OrderItem> items;

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(String customerName, double totalAmount, int userId) {
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.userId = userId;
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
        this.items = new ArrayList<>();
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public double calculateTotal() {
        return items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order{id=").append(id)
                .append(", date=").append(orderDate)
                .append(", customer='").append(customerName).append("'")
                .append(", userId=").append(userId)
                .append(", total=").append(totalAmount)
                .append(", status='").append(status).append("'");

        if (items != null && !items.isEmpty()) {
            sb.append(", items=[");
            items.forEach(item -> sb.append("\n  ").append(item.toString()));
            sb.append("\n]");
        }
        sb.append("}");
        return sb.toString();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}