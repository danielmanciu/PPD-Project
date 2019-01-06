import com.google.common.base.Stopwatch;
import ppm.PpmFile;
import processor.ImageProcessor;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final String PPM_FILE_PATH = System.getProperty("user.dir") + File.separator + "image.ppm";
    private static final String NEW_PPM_FILE_PATH = System.getProperty("user.dir") + File.separator + "processed-image.ppm";

    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        PpmFile ppmFile = new PpmFile(PPM_FILE_PATH);
        System.out.println("Read time: " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + " ms");

        stopwatch = Stopwatch.createStarted();

        PpmFile processedPpmFile = ImageProcessor.process(ppmFile);
        System.out.println("Process time: " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + " ms");

        stopwatch = Stopwatch.createStarted();

        processedPpmFile.writeToFile(NEW_PPM_FILE_PATH);
        System.out.println("Write time: " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + " ms");
    }

}
