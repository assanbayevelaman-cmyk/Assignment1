package Database;
import Database.Interface.IDB;
import java.sql.*;

public class BookstoreDB implements IDB {
    @Override
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        String connectionUrl = "jdbc:postgresql://localhost:5432/OnlineBookstoreSystem";
        try {
            Class.forName("org.postgresql.Driver");
            Connection con  = DriverManager.getConnection(connectionUrl, "postgres", "13072008");
            return con;
        } catch (Exception e) {
            System.out.print(e);
            return null;
        }
    }
}