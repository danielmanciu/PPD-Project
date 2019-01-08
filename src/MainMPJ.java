import com.google.common.base.Stopwatch;
import mpi.MPI;
import pixel.Rgb;
import ppm.PpmFile;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class MainMPJ {

    private static final String PPM_FILE_PATH = System.getProperty("user.dir") + File.separator + "image-small.ppm";
    private static final String NEW_PPM_FILE_PATH = System.getProperty("user.dir") + File.separator + "processed-image-small.ppm";
    private static final int HEIGHT_WIDTH_TAG = 1;
    private static final int PIXEL_ROW_TAG = 1;

    public static void main(String[] args) {

        MPI.Init(args);
        int me = MPI.COMM_WORLD.Rank();

        if (me == 0) {
            Stopwatch stopwatch = Stopwatch.createStarted();

            final PpmFile ppmFile = new PpmFile(PPM_FILE_PATH);
            System.out.println("Read time: " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + " ms");

            final int height = ppmFile.getHeight();
            final int width = ppmFile.getWidth();
            final Rgb[][] pixels = ppmFile.getRgbPixels();

            int[] heightAndWidth = {height, width};

            MPI.COMM_WORLD.Ssend(heightAndWidth, 0, 2, MPI.INT, 1, HEIGHT_WIDTH_TAG);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    final Rgb rgb = pixels[i][j].applySepia();

                    final short R = (short)rgb.getR();
                    final short G = (short)rgb.getG();
                    final short B = (short)rgb.getB();

                    final short[] rgbArray = {R, G, B};
                    MPI.COMM_WORLD.Ssend(rgbArray, 0,3, MPI.SHORT, 1, PIXEL_ROW_TAG);
                }
            }
        } else {
            int[] heightAndWidth = new int[2];
            MPI.COMM_WORLD.Recv(heightAndWidth, 0, 2, MPI.INT, 0, HEIGHT_WIDTH_TAG);

            final int height = heightAndWidth[0];
            final int width = heightAndWidth[1];

            Rgb[][] pixels = new Rgb[height][width];

            Stopwatch stopwatch = Stopwatch.createStarted();

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    final short[] rgbArray = new short[3];

                    MPI.COMM_WORLD.Recv(rgbArray, 0,3, MPI.SHORT, 0, PIXEL_ROW_TAG);
                    pixels[i][j] = new Rgb(rgbArray[0], rgbArray[1], rgbArray[2]);
                }
            }

            final PpmFile processedPpmFile = new PpmFile("P3", width, height, 255, pixels);
            System.out.println("Process time: " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + " ms");

            stopwatch = Stopwatch.createStarted();

            processedPpmFile.writeToFile(NEW_PPM_FILE_PATH);
            System.out.println("Write time: " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + " ms");
        }

        MPI.Finalize();
    }

}