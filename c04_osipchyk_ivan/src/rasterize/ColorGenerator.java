package rasterize;
import java.awt.Color;
import java.util.Random;

public class ColorGenerator {

    public ColorGenerator() {

    }

    // generate random light color
    public Color generate() {
        Random random = new Random();
        int maxAttempts = 1000;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int red = random.nextInt(256);
            int green = random.nextInt(256);
            int blue = random.nextInt(256);

            Color randomColor = new Color(red, green, blue);

            if (!isTooDark(randomColor, 100)) {
                return randomColor;
            }
        }

        return Color.WHITE;
    }

    // determine if color is too dark
    private boolean isTooDark(Color color, int darknessThreshold) {
        double brightness = (0.2126 * color.getRed()) + (0.7152 * color.getGreen()) + (0.0722 * color.getBlue());
        return brightness < darknessThreshold;
    }
}
