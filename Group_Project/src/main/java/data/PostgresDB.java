package org.example.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresDB {
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        String connectionUrl = "jdbc:postgresql://localhost:5432/bookshop";
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(connectionUrl, "postgres", "13072008");
    }
}