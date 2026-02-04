package org.example.strategies;

public class CategoryDiscountStrategy implements DiscountStrategy {
    private String category;
    private double discountRate;

    public CategoryDiscountStrategy(String category, double discountRate) {
        this.category = category;
        this.discountRate = discountRate;
    }

    @Override
    public double applyDiscount(double originalPrice, int quantity) {
        return originalPrice * (1 - discountRate);
    }
}