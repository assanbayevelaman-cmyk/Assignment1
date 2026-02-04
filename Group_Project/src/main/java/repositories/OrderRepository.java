package org.example.repositories;

import org.example.data.DatabaseConnection;
import org.example.entities.Order;
import org.example.entities.OrderItem;
import org.example.entities.Book;
import org.example.entities.User;
import org.example.repositories.interfaces.IOrderRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository implements IOrderRepository {

    @Override
    public boolean createOrder(Order order) {
        String sql = "INSERT INTO orders (customer_name, total_amount, status, user_id, order_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, order.getCustomerName());
            stmt.setDouble(2, order.getTotalAmount());
            stmt.setString(3, order.getStatus());
            stmt.setInt(4, order.getUserId());
            stmt.setTimestamp(5, Timestamp.valueOf(order.getOrderDate()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating order: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Order getOrder(int id) {
        String sql = "SELECT * FROM orders WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractOrderFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all orders: " + e.getMessage());
        }
        return orders;
    }

    @Override
    public boolean updateOrderStatus(int id, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Order> getOrdersByUser(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(extractOrderFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by user: " + e.getMessage());
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = ? ORDER BY order_date DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(extractOrderFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by status: " + e.getMessage());
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE order_date BETWEEN ? AND ? ORDER BY order_date DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(start));
            stmt.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(extractOrderFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by date range: " + e.getMessage());
        }
        return orders;
    }

    @Override
    public Order getFullOrderDescription(int orderId) {
        Order order = getOrder(orderId);
        if (order == null) {
            return null;
        }

        List<OrderItem> items = getOrderItems(orderId);
        order.setItems(items);

        return order;
    }

    @Override
    public List<Order> getAllOrdersWithDetails() {
        List<Order> orders = getAllOrders();

        for (Order order : orders) {
            List<OrderItem> items = getOrderItems(order.getId());
            order.setItems(items);
        }

        return orders;
    }

    @Override
    public boolean createOrderWithItems(Order order) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            String orderSql = "INSERT INTO orders (customer_name, total_amount, status, user_id, order_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);

            orderStmt.setString(1, order.getCustomerName());
            orderStmt.setDouble(2, order.getTotalAmount());
            orderStmt.setString(3, order.getStatus());
            orderStmt.setInt(4, order.getUserId());
            orderStmt.setTimestamp(5, Timestamp.valueOf(order.getOrderDate()));

            int orderAffected = orderStmt.executeUpdate();
            if (orderAffected == 0) {
                conn.rollback();
                return false;
            }

            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
            int orderId;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
                order.setId(orderId);
            } else {
                conn.rollback();
                return false;
            }

            for (OrderItem item : order.getItems()) {
                String itemSql = "INSERT INTO order_items (order_id, book_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement itemStmt = conn.prepareStatement(itemSql);

                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, item.getBookId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getUnitPrice());
                itemStmt.setDouble(5, item.getSubtotal());

                int itemAffected = itemStmt.executeUpdate();
                if (itemAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error creating order with items: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error resetting auto-commit: " + e.getMessage());
                }
            }
        }
        return false;
    }

    @Override
    public boolean cancelOrder(int orderId) {

        String sql = "UPDATE orders SET status = 'CANCELLED' WHERE id = ? AND status = 'PENDING'";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error cancelling order: " + e.getMessage());
        }
        return false;
    }

    @Override
    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status != 'CANCELLED'";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total revenue: " + e.getMessage());
        }
        return 0.0;
    }

    @Override
    public double getRevenueByUser(int userId) {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE user_id = ? AND status != 'CANCELLED'";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting revenue by user: " + e.getMessage());
        }
        return 0.0;
    }

    @Override
    public int getTotalOrderCount() {
        String sql = "SELECT COUNT(*) FROM orders";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total order count: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int getOrderCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM orders WHERE status = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order count by status: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = """
        SELECT oi.*, b.title, b.author 
        FROM order_items oi 
        JOIN books b ON oi.book_id = b.id 
        WHERE oi.order_id = ?
        """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getInt("id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setBookId(rs.getInt("book_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getDouble("unit_price"));
                    item.setSubtotal(rs.getDouble("subtotal"));

                    Book book = new Book();
                    book.setId(rs.getInt("book_id"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(rs.getString("author"));
                    item.setBook(book);

                    items.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order items: " + e.getMessage());
        }
        return items;
    }

    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
        order.setCustomerName(rs.getString("customer_name"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setStatus(rs.getString("status"));
        order.setUserId(rs.getInt("user_id"));
        return order;
    }
}