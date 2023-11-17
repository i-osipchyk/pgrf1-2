package rasterize;

import model.Line;
import model.Point;
import model.Polygon;

import java.awt.*;
import java.util.List;

public class PolygonRasterizer {
    private LineRasterizer lineRasterizer;
    private Color movingLineColor;

    public PolygonRasterizer(LineRasterizer lineRasterizer, Color movingLineColor) {
        this.lineRasterizer = lineRasterizer;
        this.movingLineColor = movingLineColor;
    }

    public void rasterize(Polygon polygon, Point movingPoint, Color finalLineColor) {

        List<Point> points = polygon.getPoints();

        // draw line only between first point and moving point
        if(points.size() == 1 && movingPoint != null) {
            Point first = points.get(0);

            lineRasterizer.rasterize(new Line(first, movingPoint), this.movingLineColor);
            return;
        }

        // rasterize lines between already defined points
        for(int i = 0; i < points.size(); i++) {
            Point current = points.get(i);
            if(i + 1 < points.size()) {

                Point next = points.get(i + 1);

                lineRasterizer.rasterize(new Line(current, next), finalLineColor);
            }
        }

        // rasterize moving lines between moving point and both first and last points
        if(points.size() > 1 && movingPoint != null) {
            Point first = points.get(0);
            Point last = points.get(points.size() - 1);

            lineRasterizer.rasterize(new Line(movingPoint, first), this.movingLineColor);

            lineRasterizer.rasterize(new Line(last, movingPoint), this.movingLineColor);
        }
        // rasterize line between first and last point
        else if(points.size() > 1) {

            Point first = points.get(0);
            Point last = points.get(points.size() - 1);

            lineRasterizer.rasterize(new Line(last, first), finalLineColor);
        }
    }
}
