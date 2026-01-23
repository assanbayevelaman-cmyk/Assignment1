package org.example.repositories;

import org.example.data.PostgresDB;
import org.example.entities.Order;
import org.example.repositories.interfaces.IOrderRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository implements IOrderRepository {
    private final PostgresDB db;

    public OrderRepository(PostgresDB db) {
        this.db = db;
    }

    @Override
    public boolean createOrder(Order order) {
        try (Connection con = db.getConnection()) {
            String sql = "INSERT INTO orders(customer_name, total_amount, status, order_date) VALUES (?,?,?,?)";
            PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, order.getCustomerName());
            st.setDouble(2, order.getTotalAmount());
            st.setString(3, order.getStatus());
            st.setTimestamp(4, Timestamp.valueOf(order.getOrderDate()));

            int affected = st.executeUpdate();
            if (affected > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    order.setId(rs.getInt(1));
                }
            }
            return affected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Order getOrder(int id) {
        try (Connection con = db.getConnection()) {
            PreparedStatement st = con.prepareStatement("SELECT * FROM orders WHERE id=?");
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new Order(
                        rs.getInt("id"),
                        rs.getTimestamp("order_date").toLocalDateTime(),
                        rs.getString("customer_name"),
                        rs.getDouble("total_amount"),
                        rs.getString("status")
                );
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        try (Connection con = db.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM orders ORDER BY order_date DESC");
            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("id"),
                        rs.getTimestamp("order_date").toLocalDateTime(),
                        rs.getString("customer_name"),
                        rs.getDouble("total_amount"),
                        rs.getString("status")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return orders;
    }

    @Override
    public boolean updateOrderStatus(int id, String status) {
        try (Connection con = db.getConnection()) {
            PreparedStatement st = con.prepareStatement("UPDATE orders SET status=? WHERE id=?");
            st.setString(1, status);
            st.setInt(2, id);
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}