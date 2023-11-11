package rasterize;

import model.Line;
import model.Point;
import model.Polygon;

import java.util.List;

public class PolygonRasterizer {
    private LineRasterizer lineRasterizer;

    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(Polygon polygon, Point movingPoint) {

        // vypsat!
        List<Point> points = polygon.getPoints();

        if(points.size() == 1 && movingPoint != null) {
            Point first = points.get(0);

            lineRasterizer.rasterize(
                    new Line(first, movingPoint, 0xFF0000)
            );
            return;
        }


        for(int i = 0; i < points.size(); i++) {
            Point current = points.get(i);
//            this.raster.setPixel(current.x, current.y, 0x0000FF);
            if(i + 1 < points.size()) {

                Point next = points.get(i + 1);

                lineRasterizer.rasterize(
                        new Line(current, next, 0x000000)
                );
            }
        }


        if(points.size() > 1 && movingPoint != null) {
            Point first = points.get(0);
            Point last = points.get(points.size() - 1);

            lineRasterizer.rasterize(
                    new Line(movingPoint, first, 0xFF0000)
            );

            lineRasterizer.rasterize(
                    new Line(last, movingPoint, 0xFF0000)
            );
        }else if(points.size() > 1) {

            Point first = points.get(0);
            Point last = points.get(points.size() - 1);

            lineRasterizer.rasterize(
                    new Line(last, first, 0x000000)
            );
        }
    }
}
