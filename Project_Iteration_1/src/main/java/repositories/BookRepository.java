package org.example.repositories;

import org.example.data.PostgresDB;
import org.example.entities.Book;
import org.example.repositories.interfaces.IBookRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepository implements IBookRepository {
    private final PostgresDB db;

    public BookRepository(PostgresDB db) {
        this.db = db;
    }

    @Override
    public boolean createBook(Book book) {
        try (Connection con = db.getConnection()) {
            String sql = "INSERT INTO books(title, author, price, quantity) VALUES (?,?,?,?)";
            PreparedStatement st = con.prepareStatement(sql);
            st.setString(1, book.getTitle());
            st.setString(2, book.getAuthor());
            st.setDouble(3, book.getPrice());
            st.setInt(4, book.getQuantity());
            return st.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    @Override
    public Book getBook(int id) {
        try (Connection con = db.getConnection()) {
            PreparedStatement st = con.prepareStatement("SELECT * FROM books WHERE id=?");
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getDouble("price"), rs.getInt("quantity"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        try (Connection con = db.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM books");
            while (rs.next()) {
                books.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getDouble("price"), rs.getInt("quantity")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return books;
    }

    @Override
    public boolean updateBookQuantity(int id, int newQuantity) {
        try (Connection con = db.getConnection()) {
            PreparedStatement st = con.prepareStatement("UPDATE books SET quantity=? WHERE id=?");
            st.setInt(1, newQuantity);
            st.setInt(2, id);
            return st.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }
    @Override
    public boolean updateBook(Book book) {
        try (Connection con = db.getConnection()) {
            PreparedStatement st = con.prepareStatement("UPDATE books SET title=?, author=?, price=?, quantity=? WHERE id=?");
            st.setString(1, book.getTitle());
            st.setString(2, book.getAuthor());
            st.setDouble(3, book.getPrice());
            st.setInt(4, book.getQuantity());
            st.setInt(5, book.getId());
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}