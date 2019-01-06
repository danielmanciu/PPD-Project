package processor;

import lombok.Getter;
import lombok.Setter;
import pixel.Rgb;
import ppm.PpmFile;

@Getter
@Setter
public class ImageProcessor {

    private ImageProcessor() {}

    public static PpmFile process(final PpmFile ppmFile) {
        String format = ppmFile.getFormat();
        int width = ppmFile.getWidth();
        int height = ppmFile.getHeight();
        int maximumValue = ppmFile.getMaximumValue();
        Rgb[][] pixels = ppmFile.getRgbPixels();

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                pixels[i][j].applySepia();
            }

        return new PpmFile(format, width, height, maximumValue, pixels);
    }

}
