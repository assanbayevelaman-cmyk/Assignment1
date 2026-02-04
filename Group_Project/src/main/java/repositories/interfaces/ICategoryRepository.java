package org.example.repositories.interfaces;

import org.example.entities.Category;
import java.util.List;

public interface ICategoryRepository {
    boolean createCategory(Category category);
    Category getCategory(int id);
    Category getCategoryByName(String name);
    List<Category> getAllCategories();
    boolean updateCategory(Category category);
    boolean deleteCategory(int id);
    int getCategoryCount();
    boolean categoryExists(String name);
}