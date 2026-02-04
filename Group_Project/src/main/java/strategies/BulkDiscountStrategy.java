package org.example.strategies;

public class BulkDiscountStrategy implements DiscountStrategy {
    @Override
    public double applyDiscount(double originalPrice, int quantity) {
        if (quantity >= 10) {
            return originalPrice * 0.9;
        } else if (quantity >= 5) {
            return originalPrice * 0.95;
        }
        return originalPrice;
    }
}