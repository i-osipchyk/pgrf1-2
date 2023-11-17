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
    // determine the direction of polygon
    public boolean isClockwise() {
        int sum = 0;
        for (int i = 0; i < points.size(); i++) {
            Point current = points.get(i);
            Point next = points.get((i + 1) % points.size());

            sum += (next.x - current.x) * (next.y + current.y);
        }
        return sum > 0;
    }
    // remove all points from polygon
    public void removePoints(ArrayList<Point> points) {
        this.points.removeAll(points);
    }
    // add intersections of given points and polygon points
    public void addIntersections(ArrayList<Point> points) {
        ArrayList<Point> currentPoints = new ArrayList<>(this.points);
        for (Point point: points) {
            if (!currentPoints.contains(point)) {
                this.points.add(point);
            }
        }
    }
    // change selected points coordinates
    public void setPoint(int index, Point point) {
        if(index >= 0 && index < points.size())
            points.set(index, point);
    }
    // find nearest point to a given point
    public int findNearestPointIndex(Point point, double tolerance) {
        int nearest = -1;
        for(int i = 0; i < points.size(); i++) {
            Point current = points.get(i);

            // calculate euclidean distance using relative point
            Point relative = new Point(point.x - current.x, point.y - current.y);
            double length  = Math.sqrt((float)Math.pow(relative.x, 2) + Math.pow(relative.y, 2));

            // if the distance is less than chosen tolerance
            if(length < tolerance) {

                if(nearest < 0)
                    nearest = i;

                // calculate current nearest distance
                Point nearestPoint = points.get(nearest);
                Point tmp = new Point(point.x - nearestPoint.x, point.y - nearestPoint.y);
                double nearestLength = Math.sqrt((float)Math.pow(tmp.x, 2) + Math.pow(tmp.y, 2));

                // choose the closest point if 2 are available
                if(length < nearestLength)
                    nearest = i;
            }
        }

        return nearest;
    }
}
