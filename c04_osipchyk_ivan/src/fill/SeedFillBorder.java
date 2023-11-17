package fill;

import model.Point;
import rasterize.Raster;

import java.awt.*;
import java.util.Objects;

public class SeedFillBorder implements Filler {
    private Raster raster;
    private int x, y, xMax, yMax;
    private Color borderColor;

    public SeedFillBorder(Raster raster, Color borderColor, int x, int y, int xMax, int yMax) {
        this.raster = raster;
        this.borderColor = borderColor;
        this.x = x;
        this.y = y;
        this.xMax = xMax;
        this.yMax = yMax;
    }

    @Override
    public void fill() {
        seedFill(x, y);
    }

    private void seedFill(int x, int y) {
        // if x is within canvas boundaries
        if (x > 0 && x < this.xMax && y > 0 && y < this.yMax) {
            int pixelColor = raster.getPixel(x, y);

            // skip the point if on the border or on already rasterized point
            if (Objects.equals(this.borderColor, new Color(pixelColor)) || Objects.equals(new Color(pixelColor), new Color(0xff00ff)))
                return;

            // rasterize pixel
            raster.setPixel(x, y, 0xff00ff);

            // call for neighbouring points
            seedFill(x + 1, y);
            seedFill(x - 1, y);
            seedFill(x, y + 1);
            seedFill(x, y - 1);
        }
    }
}
