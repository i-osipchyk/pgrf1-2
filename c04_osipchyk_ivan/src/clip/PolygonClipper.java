package clip;

import model.Edge;
import model.Point;
import model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class PolygonClipper {

    private static boolean inside(Point point, Edge edge) {
        return (edge.getX2() - edge.getX1()) * (point.y - edge.getY1()) > (edge.getY2() - edge.getY1()) * (point.x - edge.getX1());
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

    public static Polygon clip(Polygon subjectPolygon, Polygon clipPolygon) {
        Polygon outputPolygon = new Polygon(subjectPolygon.getPoints());
        Point cp1 = clipPolygon.getPoint(clipPolygon.size() - 1);
        Polygon inputPolygon;

        for (Point clipVertex : clipPolygon.getPoints()) {
            Point cp2 = clipVertex;
            inputPolygon = outputPolygon;
            outputPolygon = new Polygon();

            if (inputPolygon.getPoints().isEmpty())
                break;
            Point s = inputPolygon.getPoint(inputPolygon.size() - 1); // this line

            for (Point subjectVertex : inputPolygon.getPoints()) {
                Point e = subjectVertex;
                if (inside(e, new Edge(cp1, cp2))) {
                    if (!inside(s, new Edge(cp1, cp2))) {
                        outputPolygon.addPoint(computeIntersection(new Edge(cp1, cp2), s, e));
                    }
                    outputPolygon.addPoint(e);
                } else if (inside(s, new Edge(cp1, cp2))) {
                    outputPolygon.addPoint(computeIntersection(new Edge(cp1, cp2), s, e));
                }
                s = e;
            }
            cp1 = cp2;
        }
        return outputPolygon;
    }
}