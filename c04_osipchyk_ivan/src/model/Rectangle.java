package model;

import java.awt.*;
import java.util.ArrayList;

public class Rectangle extends Polygon {

    public Rectangle(Color color) {
        super(color);
    }

    public void addAllPoints(Point p1, Point p3) {
        Point p2 = new Point(p1.x, p3.y);
        Point p4 = new Point(p3.x, p1.y);

        addPoint(p1);
        addPoint(p2);
        addPoint(p3);
        addPoint(p4);
    }
}
