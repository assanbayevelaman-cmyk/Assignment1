package models;
public class Point {
    private double x;
    private double y;
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public double distance(Point dest) {
        if (dest == null) {
            throw new IllegalArgumentException("Destination point cannot be null");
        }
        double dx = dest.x - this.x;
        double dy = dest.y - this.y;
        return Math.sqrt(dx*dx + dy*dy);
    }
    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
    }
}
