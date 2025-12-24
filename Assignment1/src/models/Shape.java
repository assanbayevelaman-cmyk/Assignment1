package models;

import models.Point;
import java.util.ArrayList;

public class Shape {
    private ArrayList<Point> points;
    public Shape() {
        points = new ArrayList<>();
    }
    public void addPoint(Point point) {
        points.add(point);
    }
    public double calculatePerimeter() {
        double perimeter = 0;
        for(int i = 0; i < points.size(); i++) {
            Point current = points.get(i);
            Point next = points.get((i + 1) % points.size());
            perimeter += current.distance(next);
        }
        return perimeter;
    }
    public double getAverageSide() {
        if (points.size() < 2) {
            return 0;
        }
        return calculatePerimeter() / points.size();
    }
    public double getLongestSide() {
        if (points.size() < 2) {
            return 0;
        }
        double longest = 0;
        for (int i = 0; i < points.size(); i++) {
            Point current = points.get(i);
            Point next = points.get((i + 1) % points.size());
            double distance = current.distance(next);
            if (distance > longest) {
                longest = distance;
            }
        }
        return longest;
    }
}
