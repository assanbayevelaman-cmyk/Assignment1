package org.example.entities;

public class Book {
    private int id;
    private String title;
    private String author;
    private double price;
    private int quantity;
    private int categoryId;
    private Category category;

    public Book() {}

    public Book(String title, String author, double price, int quantity, int categoryId) {
        this.title = title; this.author = author; this.price = price; this.quantity = quantity; this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        String categoryInfo = category != null ?
                ", category=" + category.getName() :
                ", categoryId=" + categoryId;
        return "Book{" + "id=" + id + ", title='" + title + "', author='" + author +
                "', price=" + price + ", quantity=" + quantity + categoryInfo + '}';
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

}