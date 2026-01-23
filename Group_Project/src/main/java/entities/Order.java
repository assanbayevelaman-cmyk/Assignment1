package org.example.entities;

import java.time.LocalDateTime;

public class Order {
    private int id;
    private LocalDateTime orderDate;
    private String customerName;
    private double totalAmount;
    private String status;

    public Order(int id, LocalDateTime orderDate, String customerName, double totalAmount, String status) {
        this.id = id;
        this.orderDate = orderDate;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Order(String customerName, double totalAmount) {
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
    }

    public int getId() { return id; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public String getCustomerName() { return customerName; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public void setId(int id) { this.id = id; }

    @Override
    public String toString() {
        return "Order{id=" + id + ", date=" + orderDate + ", customer='" + customerName + "', total=" + totalAmount + ", status=" + status + "}";
    }
}