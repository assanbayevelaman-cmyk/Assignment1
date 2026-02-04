package org.example.repositories;

import org.example.data.DatabaseConnection;
import org.example.entities.Category;
import org.example.repositories.interfaces.ICategoryRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository implements ICategoryRepository {

    @Override
    public boolean createCategory(Category category) {
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        category.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating category: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Category getCategory(int id) {
        String sql = "SELECT * FROM categories WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCategoryFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting category: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Category getCategoryByName(String name) {
        String sql = "SELECT * FROM categories WHERE LOWER(name) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCategoryFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting category by name: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY name";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(extractCategoryFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all categories: " + e.getMessage());
        }
        return categories;
    }

    @Override
    public boolean updateCategory(Category category) {
        String sql = "UPDATE categories SET name = ?, description = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, category.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteCategory(int id) {
        String checkSql = "SELECT COUNT(*) FROM books WHERE category_id = ?";
        String deleteSql = "DELETE FROM categories WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, id);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.err.println("Cannot delete category: Books are using it");
                        return false;
                    }
                }
            }

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, id);
                return deleteStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
        }
        return false;
    }

    @Override
    public int getCategoryCount() {
        String sql = "SELECT COUNT(*) FROM categories";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting category count: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public boolean categoryExists(String name) {
        String sql = "SELECT COUNT(*) FROM categories WHERE LOWER(name) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if category exists: " + e.getMessage());
        }
        return false;
    }

    private Category extractCategoryFromResultSet(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt("id"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        return category;
    }

    public List<Category> searchCategories(String keyword) {
        return getAllCategories().stream()
                .filter(category ->
                        category.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                                (category.getDescription() != null &&
                                        category.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                )
                .toList();
    }

    public List<Category> getCategoriesSortedByName(boolean ascending) {
        List<Category> categories = getAllCategories();

        if (ascending) {
            categories.sort((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));
        } else {
            categories.sort((c1, c2) -> c2.getName().compareToIgnoreCase(c1.getName()));
        }

        return categories;
    }
}