package rasterize;

import model.Point;
import model.Rectangle;

import java.awt.*;

public class EllipseRasterizer {
    LineRasterizer lineRasterizer;
    private int a, b;
    private Point centerPoint;

    public EllipseRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void setEquationValues(Rectangle rectangle) {
        if (!rectangle.getPoints().isEmpty()) {
            int maxX = Math.max(rectangle.getPoint(0).x, rectangle.getPoint(2).x);
            int minX = Math.min(rectangle.getPoint(0).x, rectangle.getPoint(2).x);

            int maxY = Math.max(rectangle.getPoint(0).y, rectangle.getPoint(2).y);
            int minY = Math.min(rectangle.getPoint(0).y, rectangle.getPoint(2).y);

            a = Math.round((float) (maxX - minX) / 2);
            b = Math.round((float) (maxY - minY) / 2);

            // Calculate the center point as the midpoint of the major and minor axes.
            this.centerPoint = new Point((maxX + minX) / 2, (maxY + minY) / 2);
        }
    }

    public void rasterize()
    {
        // center of the
        int cx, cy;

        // center of the ellipse
        cx = centerPoint.x;
        cy = centerPoint.y;

        // major and minor axis
        double A = a, B = b, px = 0, py = 0;

        // draw the ellipse
        for (int i = 0; i <= 360; i++) {
            double x, y;
            x = A * Math.sin(Math.toRadians(i));
            y = B * Math.cos(Math.toRadians(i));

            if (i != 0) {
                // draw a line joining previous and new point .
                lineRasterizer.rasterize((int)px + cx, (int)py + cy,
                        (int)x + cx, (int)y + cy, Color.GREEN);
            }

            // store the previous points
            px = x;
            py = y;
        }
    }
}
