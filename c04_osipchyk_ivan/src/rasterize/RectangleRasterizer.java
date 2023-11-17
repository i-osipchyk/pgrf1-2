package rasterize;

import model.Line;
import model.Point;
import model.Rectangle;

import java.awt.*;

public class RectangleRasterizer {
    private LineRasterizer lineRasterizer;
    public RectangleRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(Rectangle rectangle, Point startingPoint, Point movingPoint) {
        // rasterize rectangle if all 4 points are available
        if (rectangle.getPoints().size() == 4) {
            for (int i = 0; i < rectangle.getPoints().size(); i++) {
                int indexA = i;
                int indexB = 0;

                if (indexA != rectangle.getPoints().size() - 1)
                    indexB = i + 1;

                Point pA = rectangle.getPoint(indexA);
                Point pB = rectangle.getPoint(indexB);

                lineRasterizer.rasterize(pA.x, pA.y, pB.x, pB.y, Color.BLUE);
            }
        } else {
            // rasterize 2 moving lines if rectangle is not set yet
            lineRasterizer.rasterize(startingPoint.x, startingPoint.y, movingPoint.x, startingPoint.y, Color.RED);
            lineRasterizer.rasterize(startingPoint.x, startingPoint.y, startingPoint.x, movingPoint.y, Color.RED);
        }
    }
}
