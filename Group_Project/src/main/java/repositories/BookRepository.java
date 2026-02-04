package org.example.repositories;

import org.example.data.DatabaseConnection;
import org.example.entities.Book;
import org.example.entities.Category;
import org.example.repositories.interfaces.IBookRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.Comparator;
import java.util.stream.Collectors;

public class BookRepository implements IBookRepository {

    @Override
    public boolean createBook(Book book) {
        String sql = "INSERT INTO books (title, author, price, quantity, category_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setDouble(3, book.getPrice());
            stmt.setInt(4, book.getQuantity());

            if (book.getCategoryId() > 0) {
                stmt.setInt(5, book.getCategoryId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating book: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Book getBook(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractBookFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting book: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all books: " + e.getMessage());
        }
        return books;
    }

    @Override
    public boolean updateBookQuantity(int id, int newQuantity) {
        String sql = "UPDATE books SET quantity = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book quantity: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, price = ?, quantity = ?, category_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setDouble(3, book.getPrice());
            stmt.setInt(4, book.getQuantity());

            if (book.getCategoryId() > 0) {
                stmt.setInt(5, book.getCategoryId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setInt(6, book.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Book> getBooksByCategory(int categoryId) {
        List<Book> books = new ArrayList<>();
        String sql = """
            SELECT b.*, c.name as category_name, c.description as category_description 
            FROM books b 
            LEFT JOIN categories c ON b.category_id = c.id 
            WHERE b.category_id = ? 
            ORDER BY b.title
            """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(extractBookWithCategory(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting books by category: " + e.getMessage());
        }
        return books;
    }

    @Override
    public List<Book> getAllBooksWithCategory() {
        List<Book> books = new ArrayList<>();
        String sql = """
            SELECT b.*, c.name as category_name, c.description as category_description 
            FROM books b 
            LEFT JOIN categories c ON b.category_id = c.id 
            ORDER BY b.title
            """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(extractBookWithCategory(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all books with category: " + e.getMessage());
        }
        return books;
    }

    @Override
    public List<Book> getBooksByAuthor(String author) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE LOWER(author) LIKE LOWER(?) ORDER BY title";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + author + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(extractBookFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting books by author: " + e.getMessage());
        }
        return books;
    }

    @Override
    public List<Book> searchBooksByTitle(String title) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE LOWER(?) ORDER BY title";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + title + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(extractBookFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching books by title: " + e.getMessage());
        }
        return books;
    }

    @Override
    public List<Book> searchBooksByAuthor(String author) {
        return getBooksByAuthor(author);
    }

    @Override
    public boolean bookExists(String title, String author) {
        String sql = "SELECT COUNT(*) FROM books WHERE LOWER(title) = LOWER(?) AND LOWER(author) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, author);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if book exists: " + e.getMessage());
        }
        return false;
    }

    @Override
    public int getTotalBookCount() {
        String sql = "SELECT COUNT(*) FROM books";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total book count: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int getBookCountByCategory(int categoryId) {
        String sql = "SELECT COUNT(*) FROM books WHERE category_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting book count by category: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public List<Book> filterBooks(Predicate<Book> predicate) {
        List<Book> allBooks = getAllBooksWithCategory();
        return allBooks.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> sortBooks(Comparator<Book> comparator) {
        List<Book> allBooks = getAllBooksWithCategory();
        return allBooks.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setPrice(rs.getDouble("price"));
        book.setQuantity(rs.getInt("quantity"));
        book.setCategoryId(rs.getInt("category_id"));
        return book;
    }

    private Book extractBookWithCategory(ResultSet rs) throws SQLException {
        Book book = extractBookFromResultSet(rs);

        String categoryName = rs.getString("category_name");
        if (categoryName != null && !rs.wasNull()) {
            Category category = new Category();
            category.setId(book.getCategoryId());
            category.setName(categoryName);
            category.setDescription(rs.getString("category_description"));
            book.setCategory(category);
        }

        return book;
    }
}