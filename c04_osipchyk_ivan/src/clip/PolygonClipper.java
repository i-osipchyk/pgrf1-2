package clip;

import model.Edge;
import model.Point;
import model.Polygon;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PolygonClipper {

    private static boolean isInside(Point cp1, Point cp2, Point p) {
        return (cp1.x - p.x) * (cp2.y - p.y) > (cp1.y - p.y) * (cp2.x - p.x);
    }

    private static Point computeIntersection(Edge edge, Point p1, Point p2) {
        List<Integer> dc = new ArrayList<>();
        dc.add(edge.getX1() - edge.getX2());
        dc.add(edge.getY1() - edge.getY2());

        List<Integer> dp = new ArrayList<>();
        dp.add(p1.x - p2.x);
        dp.add(p1.y - p2.y);

        double n1 = edge.getX1() * edge.getY2() - edge.getY1() * edge.getX2();
        double n2 = p1.x * p2.y - p1.y * p2.x;
        double n3 = 1.0 / (dc.get(0) * dp.get(1) - dc.get(1) * dp.get(0));

        int x = (int) ((n1 * dp.get(0) - n2 * dc.get(0)) * n3);
        int y = (int) ((n1 * dp.get(1) - n2 * dc.get(1)) * n3);

        return new Point(x, y);
    }

    public Polygon clip(Polygon subjectPolygon, Polygon clipPolygon) {
        Polygon result = new Polygon(subjectPolygon.getPoints(), Color.GREEN);
        Polygon inputPolygon;

        boolean isClockwise = clipPolygon.isClockwise();

        int len = clipPolygon.size();
        for (int i = 0; i < len; i++) {

            int len2 = result.size();
            inputPolygon = result;
            result = new Polygon(Color.GREEN);

            Point cp1, cp2;
            if(isClockwise) {
                cp1 = clipPolygon.getPoint((i + len - 1) % len);
                cp2 = clipPolygon.getPoint(i);
            } else {
                cp2 = clipPolygon.getPoint((i + len - 1) % len);
                cp1 = clipPolygon.getPoint(i);
            }

            for (int j = 0; j < len2; j++) {

                Point v1 = inputPolygon.getPoint((j + len2 - 1) % len2);
                Point v2 = inputPolygon.getPoint(j);

                if (isInside(cp2, cp1, v2)) {
                    if (!isInside(cp2, cp1, v1)) {
                        result.addPoint(computeIntersection(new Edge(cp1, cp2), v1, v2));
                    }
                    result.addPoint(v2);
                } else if (isInside(cp2, cp1, v1)) {
                    result.addPoint(computeIntersection(new Edge(cp1, cp2), v1, v2));
                }
            }
        }
        return result;
    }
    public Polygon clipReverse(Polygon subjectPolygon, Polygon clipPolygon) {
        Polygon result = new Polygon(subjectPolygon.getPoints(), Color.GREEN);
        Polygon inputPolygon;

        boolean isClockwise = clipPolygon.isClockwise();

        int len = clipPolygon.size();
        for (int i = 0; i < len; i++) {

            int len2 = result.size();
            inputPolygon = result;
            result = new Polygon(Color.GREEN);

            Point cp1, cp2;
            if(isClockwise) {
                cp1 = clipPolygon.getPoint((i + len - 1) % len);
                cp2 = clipPolygon.getPoint(i);
            } else {
                cp2 = clipPolygon.getPoint((i + len - 1) % len);
                cp1 = clipPolygon.getPoint(i);
            }

            for (int j = 0; j < len2; j++) {

                Point v1 = inputPolygon.getPoint((j + len2 - 1) % len2);
                Point v2 = inputPolygon.getPoint(j);

                if (isInside(cp2, cp1, v2)) {
                    if (!isInside(cp2, cp1, v1)) {
                        result.addPoint(computeIntersection(new Edge(cp1, cp2), v1, v2));
                    }
                    result.addPoint(v2);
                } else if (isInside(cp2, cp1, v1)) {
                    result.addPoint(computeIntersection(new Edge(cp1, cp2), v1, v2));
                }
            }
        }
        subjectPolygon.removePoints(result.getPoints());
        subjectPolygon.addIntersections(result.getPoints());
        return subjectPolygon;
    }
}