package model;

import java.util.ArrayList;

public class Polygon {
    private ArrayList<Point> points;

    public Polygon() {
        this.points = new ArrayList<>();
    }
    public Polygon(ArrayList<Point> points) {
        this.points = new ArrayList<>(points);
    }

    public void addPoint(Point p) {
        this.points.add(p);
    }

    public int size() {
        return this.points.size();
    }

    public Point getPoint(int index) {
        return this.points.get(index);
    }

    public ArrayList<Point> getPoints() {
        return this.points;
    }
    public boolean isClockwise() {
        int sum = 0;
        for (int i = 0; i < points.size(); i++) {
            Point current = points.get(i);
            Point next = points.get((i + 1) % points.size());

            sum += (next.x - current.x) * (next.y + current.y);
        }
        return sum > 0;
    }
    public void removePoints(ArrayList<Point> points) {
        this.points.removeAll(points);
    }

    public void addIntersections(ArrayList<Point> points) {
        ArrayList<Point> currentPoints = new ArrayList<>(this.points);
        for (Point point: points) {
            if (!currentPoints.contains(point)) {
                this.points.add(point);
            }
        }
    }
}
