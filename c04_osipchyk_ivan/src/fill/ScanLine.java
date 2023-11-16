package fill;

import model.Edge;
import model.Line;
import model.Point;
import model.Polygon;
import rasterize.ColorGenerator;
import rasterize.LineRasterizer;
import rasterize.PolygonRasterizer;
import rasterize.Raster;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class ScanLine implements Filler{
    private LineRasterizer lineRasterizer;
    private Polygon polygon;
    private PolygonRasterizer polygonRasterizer;
    private ColorGenerator colorGenerator;

    public ScanLine(LineRasterizer lineRasterizer, PolygonRasterizer polygonRasterizer, Polygon polygon) {
        this.polygon = polygon;
        this.lineRasterizer = lineRasterizer;
        this.polygonRasterizer = polygonRasterizer;
    }
    @Override
    public void fill() {
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < polygon.size(); i++) {
            Point p1 = polygon.getPoint(i);

            int indexB = i + 1;
            if (indexB == polygon.size())
                indexB = 0;

            Point p2 = polygon.getPoint(indexB);

            Edge edge = new Edge(p1.x, p1.y, p2.x, p2.y);
            if (edge.isHorizontal())
                continue;

            edges.add(edge);
        }

        int yMin = polygon.getPoint(0).y;
        int yMax = yMin;

        for (Point point: polygon.getPoints()) {
            int currY = point.y;

            yMin = Math.min(currY, yMin);
            yMax = Math.max(currY, yMax);
        }

        for (int y = yMin; y <= yMax; y++) {
            ArrayList<Integer> intersections = new ArrayList<>();

            for (Edge edge: edges) {
                if (edge.getY1() < y && edge.getY2() >= y || edge.getY2() < y && edge.getY1() >= y) {
                    int x = (int) Math.round(edge.getX1() + (double) (y - edge.getY1()) / (edge.getY2() - edge.getY1()) * (edge.getX2() - edge.getX1()));
                    intersections.add(x);
                }
            }

            Collections.sort(intersections);

            for (int i = 0; i < intersections.size(); i+=2) {
                int x1 = intersections.get(i);
                int x2 = intersections.get(i+1);

                lineRasterizer.rasterize(x1, y, x2, y, Color.BLUE);
            }
        }

        polygonRasterizer.rasterize(polygon, null, polygon.color);
    }
}
