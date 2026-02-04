package org.example.strategies;

public class NoDiscountStrategy implements DiscountStrategy {
    @Override
    public double applyDiscount(double originalPrice, int quantity) {
        return originalPrice;
    }
}