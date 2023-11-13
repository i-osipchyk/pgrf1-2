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
        if (x > 0 && x < this.xMax && y > 0 && y < this.yMax) {
            int pixelColor = raster.getPixel(x, y);

            if (Objects.equals(this.borderColor, new Color(pixelColor)))
                return;

            raster.setPixel(x, y, 0xff00ff);

            seedFill(x + 1, y);
            seedFill(x - 1, y);
            seedFill(x, y + 1);
            seedFill(x, y - 1);
        }
    }
}
