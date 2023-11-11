package rasterize;

import model.Line;
import model.Point;
import model.Polygon;

import java.awt.*;
import java.util.List;

public class PolygonRasterizer {
    private LineRasterizer lineRasterizer;

    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(Polygon polygon, Point movingPoint) {

        List<Point> points = polygon.getPoints();

        if(points.size() == 1 && movingPoint != null) {
            Point first = points.get(0);

            lineRasterizer.rasterize(
                    new Line(first, movingPoint), Color.RED
            );
            return;
        }


        for(int i = 0; i < points.size(); i++) {
            Point current = points.get(i);
            if(i + 1 < points.size()) {

                Point next = points.get(i + 1);

                lineRasterizer.rasterize(
                        new Line(current, next), Color.GREEN
                );
            }
        }


        if(points.size() > 1 && movingPoint != null) {
            Point first = points.get(0);
            Point last = points.get(points.size() - 1);

            lineRasterizer.rasterize(
                    new Line(movingPoint, first), Color.RED
            );

            lineRasterizer.rasterize(
                    new Line(last, movingPoint), Color.RED
            );
        }else if(points.size() > 1) {

            Point first = points.get(0);
            Point last = points.get(points.size() - 1);

            lineRasterizer.rasterize(
                    new Line(last, first), Color.GREEN
            );
        }
    }
}
