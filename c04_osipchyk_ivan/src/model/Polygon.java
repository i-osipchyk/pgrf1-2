package model;

import java.util.ArrayList;

public class Polygon {
    private ArrayList<Point> points;

    public Polygon() {
        this.points = new ArrayList<>();
    }
    public Polygon(ArrayList<Point> points) {
        this.points = points;
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
}
