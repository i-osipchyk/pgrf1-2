package model;

import java.awt.*;
import java.util.ArrayList;

public class Polygon {
    private ArrayList<Point> points;
    public Color color;

    public Polygon(Color color) {
        this.points = new ArrayList<>();
        this.color = color;
    }
    public Polygon(ArrayList<Point> points, Color color) {
        this.points = new ArrayList<>(points);
        this.color = color;
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
    public void setPoint(int index, Point point) {
        if(index >= 0 && index < points.size())
            points.set(index, point);
    }
    public int findNearestPointIndex(Point point, double tolerance) {
        int nearest = -1;
        for(int i = 0; i < points.size(); i++) {
            Point current = points.get(i);

            Point relative = new Point(point.x - current.x, point.y - current.y);
            double length  = Math.sqrt((float)Math.pow(relative.x, 2) + Math.pow(relative.y, 2));

            if(length < tolerance) {

                if(nearest < 0)
                    nearest = i;

                Point nearestPoint = points.get(nearest);
                Point tmp = new Point(point.x - nearestPoint.x, point.y - nearestPoint.y);
                double nearestLength = Math.sqrt((float)Math.pow(tmp.x, 2) + Math.pow(tmp.y, 2));

                if(length < nearestLength)
                    nearest = i;
            }
        }

        return nearest;
    }
}
