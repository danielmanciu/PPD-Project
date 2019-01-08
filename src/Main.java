import com.google.common.base.Stopwatch;
import pixel.Rgb;
import ppm.PpmFile;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final String PPM_FILE_PATH = System.getProperty("user.dir") + File.separator + "image.ppm";
    private static final String NEW_PPM_FILE_PATH = System.getProperty("user.dir") + File.separator + "processed-image.ppm";

    private static PpmFile processPpmFile(final PpmFile ppmFile, final ExecutorService executor) {
        String format = ppmFile.getFormat();
        int width = ppmFile.getWidth();
        int height = ppmFile.getHeight();
        int maximumValue = ppmFile.getMaximumValue();
        Rgb[][] pixels = ppmFile.getRgbPixels();

        for (int row = 0; row < height; row++) {
            final int i = row;
            executor.submit(() -> {
                for (int j = 0; j < width; j++) {
                    pixels[i][j].applySepia();
                }
            });
        }

        return new PpmFile(format, width, height, maximumValue, pixels);
    }

    public static void main(String[] args) {
        final ExecutorService executor = Executors.newFixedThreadPool(4);

        Stopwatch stopwatch = Stopwatch.createStarted();

        final PpmFile ppmFile = new PpmFile(PPM_FILE_PATH);
        System.out.println("Read time: " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + " ms");

        stopwatch = Stopwatch.createStarted();

        final PpmFile processedPpmFile = processPpmFile(ppmFile, executor);
        System.out.println("Process time: " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + " ms");

        stopwatch = Stopwatch.createStarted();

        processedPpmFile.writeToFile(NEW_PPM_FILE_PATH);
        System.out.println("Write time: " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + " ms");

        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
            executor.shutdown();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
