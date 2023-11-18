package fill;

import rasterize.Raster;

public class SeedFill implements Filler{
    private Raster raster;
    private int x, y, xMax, yMax;
    private int backgroundColor;

    public SeedFill(Raster raster, int backgroundColor, int x, int y, int xMax, int yMax) {
        this.raster = raster;
        this.backgroundColor = backgroundColor;
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

            // skip the point if it doesn't have background color
            if (this.backgroundColor != pixelColor)
                return;

            // rasterize pixel
            raster.setPixel(x, y, 0xffffff);

            // call for neighbouring points
            seedFill(x+1, y);
            seedFill(x-1, y);
            seedFill(x, y+1);
            seedFill(x, y-1);
        }
    }
}
