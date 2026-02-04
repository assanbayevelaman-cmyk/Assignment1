package org.example.repositories.interfaces;

import org.example.entities.Order;
import java.util.List;

public interface IOrderRepository {
    boolean createOrder(Order order);
    Order getOrder(int id);
    List<Order> getAllOrders();
    boolean updateOrderStatus(int id, String status);
}