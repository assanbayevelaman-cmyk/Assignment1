package org.example.strategies;

public interface DiscountStrategy {
    double applyDiscount(double originalPrice, int quantity);
}