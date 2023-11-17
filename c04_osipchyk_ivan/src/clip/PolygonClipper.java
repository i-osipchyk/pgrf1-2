package clip;

import model.Edge;
import model.Point;
import model.Polygon;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PolygonClipper {

    // checks if point is on the right side of the edge
    private static boolean isInside(Point cp1, Point cp2, Point p) {
        return (cp1.x - p.x) * (cp2.y - p.y) > (cp1.y - p.y) * (cp2.x - p.x);
    }

//    calculate the intersection between two edges
    private static Point computeIntersection(Edge edge, Point p1, Point p2) {
        // calculate the differences in coordinates for the edge
        List<Integer> dc = new ArrayList<>();
        dc.add(edge.getX1() - edge.getX2());
        dc.add(edge.getY1() - edge.getY2());

        // calculate the differences in coordinates for the two points
        List<Integer> dp = new ArrayList<>();
        dp.add(p1.x - p2.x);
        dp.add(p1.y - p2.y);

        // calculate components of the determinants for intersection calculation
        double n1 = edge.getX1() * edge.getY2() - edge.getY1() * edge.getX2();
        double n2 = p1.x * p2.y - p1.y * p2.x;

        // calculate the inverse of the determinant
        double n3 = 1.0 / (dc.get(0) * dp.get(1) - dc.get(1) * dp.get(0));

        // calculate x and y coordinates of the intersection point using determinants
        int x = (int) ((n1 * dp.get(0) - n2 * dc.get(0)) * n3);
        int y = (int) ((n1 * dp.get(1) - n2 * dc.get(1)) * n3);

        return new Point(x, y);
    }

    // clip the points that are inside clipping polygon
    public Polygon clip(Polygon subjectPolygon, Polygon clipPolygon) {
        // create a result Polygon based on subjectPolygon
        Polygon result = new Polygon(subjectPolygon.getPoints(), Color.GREEN);
        Polygon inputPolygon;

        // determine the orientation of the clipPolygon
        boolean isClockwise = clipPolygon.isClockwise();

        int len = clipPolygon.size();

        // iterate through each point in the clipPolygon
        for (int i = 0; i < len; i++) {

            int len2 = result.size();

            // refresh input and result polygons
            inputPolygon = result;
            result = new Polygon(Color.GREEN);

            // set vector direction depending on the polygon direction
            Point cp1, cp2;
            if(isClockwise) {
                cp1 = clipPolygon.getPoint((i + len - 1) % len);
                cp2 = clipPolygon.getPoint(i);
            } else {
                cp2 = clipPolygon.getPoint((i + len - 1) % len);
                cp1 = clipPolygon.getPoint(i);
            }

            // iterate through each point in the current result Polygon
            for (int j = 0; j < len2; j++) {

                Point v1 = inputPolygon.getPoint((j + len2 - 1) % len2);
                Point v2 = inputPolygon.getPoint(j);

                // check if v2 is inside the clip edge defined by cp1 and cp2
                if (isInside(cp2, cp1, v2)) {
                    // if v2 is inside, but v1 is outside, compute intersection and add to result
                    if (!isInside(cp2, cp1, v1)) {
                        result.addPoint(computeIntersection(new Edge(cp1, cp2), v1, v2));
                    }
                    // add v2 to result
                    result.addPoint(v2);
                } else if (isInside(cp2, cp1, v1)) {
                    // if v1 is inside but v2 is outside, compute intersection and add to result
                    result.addPoint(computeIntersection(new Edge(cp1, cp2), v1, v2));
                }
            }
        }
        return result;
    }

    // clip the points that are inside clipping polygon
    public Polygon clipReverse(Polygon subjectPolygon, Polygon clipPolygon) {
        // create a result Polygon based on subjectPolygon
        Polygon result = new Polygon(subjectPolygon.getPoints(), Color.GREEN);
        Polygon inputPolygon;

        // determine the orientation of the clipPolygon
        boolean isClockwise = clipPolygon.isClockwise();

        int len = clipPolygon.size();

        // iterate through each point in the clipPolygon
        for (int i = 0; i < len; i++) {

            int len2 = result.size();

            // refresh input and result polygons
            inputPolygon = result;
            result = new Polygon(Color.GREEN);

            // set vector direction depending on the polygon direction
            Point cp1, cp2;
            if(isClockwise) {
                cp1 = clipPolygon.getPoint((i + len - 1) % len);
                cp2 = clipPolygon.getPoint(i);
            } else {
                cp2 = clipPolygon.getPoint((i + len - 1) % len);
                cp1 = clipPolygon.getPoint(i);
            }

            // iterate through each point in the current result Polygon
            for (int j = 0; j < len2; j++) {

                Point v1 = inputPolygon.getPoint((j + len2 - 1) % len2);
                Point v2 = inputPolygon.getPoint(j);

                // check if v2 is inside the clip edge
                if (isInside(cp2, cp1, v2)) {
                    // if v2 is inside, but v1 is outside, compute intersection and add to result
                    if (!isInside(cp2, cp1, v1)) {
                        result.addPoint(computeIntersection(new Edge(cp1, cp2), v1, v2));
                    }
                    // add v2 to result
                    result.addPoint(v2);
                } else if (isInside(cp2, cp1, v1)) {
                    // if v1 is inside but v2 is outside, compute intersection and add to result
                    result.addPoint(computeIntersection(new Edge(cp1, cp2), v1, v2));
                }
            }
        }
        // remove points that are inside
        subjectPolygon.removePoints(result.getPoints());
        // add intersections
        subjectPolygon.addIntersections(result.getPoints());
        return subjectPolygon;
    }
}