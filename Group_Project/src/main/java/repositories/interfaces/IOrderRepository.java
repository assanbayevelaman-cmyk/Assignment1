package org.example.repositories.interfaces;

import org.example.entities.Order;
import org.example.entities.OrderItem;
import java.util.List;
import java.time.LocalDateTime;

public interface IOrderRepository {
    boolean createOrder(Order order);
    Order getOrder(int id);
    List<Order> getAllOrders();
    boolean updateOrderStatus(int id, String status);
    List<Order> getOrdersByUser(int userId);
    List<Order> getOrdersByStatus(String status);
    List<Order> getOrdersByDateRange(LocalDateTime start, LocalDateTime end);
    Order getFullOrderDescription(int orderId);
    List<Order> getAllOrdersWithDetails();
    boolean createOrderWithItems(Order order);
    boolean cancelOrder(int orderId);
    double getTotalRevenue();
    double getRevenueByUser(int userId);
    int getTotalOrderCount();
    int getOrderCountByStatus(String status);
    List<OrderItem> getOrderItems(int orderId);
}