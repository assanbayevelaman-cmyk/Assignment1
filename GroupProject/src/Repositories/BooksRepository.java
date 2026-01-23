package Repositories;
import Database.Interface.IDB;
import Entities.Books;
import Repositories.Interface.IBooksRepository;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BooksRepository implements IBooksRepository {
    private final IDB db;
    public BooksRepository(IDB db) {
        this.db = db;
    }
    @Override
    public boolean createBooks(Books books) {
        Connection con = null;
        PreparedStatement st = null;
        try {
            con = db.getConnection();
            String sql = "INSERT INTO books(title, author, publication_year) VALUES (?, ?, ?)";
            st = con.prepareStatement(sql);
            st.setString(1, books.getTitle());
            st.setString(2, books.getAuthor());
            st.setInt(3, books.getPublicationYear());
            st.execute();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public Books getBooks(int id) {
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            con = db.getConnection();
            String sql = "SELECT id, title, author, publication_year FROM books WHERE id = ?";
            st = con.prepareStatement(sql);
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                return new Books(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("publication_year")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public List<Books> getAllBooks() {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        List<Books> books = new ArrayList<>();
        try {
            con = db.getConnection();
            st = con.createStatement();
            String sql = "SELECT id, title, author, publication_year FROM books";
            rs = st.executeQuery(sql);
            while (rs.next()) {
                Books book = new Books(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("publication_year")
                );
                books.add(book);
            }
            } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return books;
    }
}
