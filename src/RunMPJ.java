import com.google.common.base.Stopwatch;
import mpi.MPI;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class RunMPJ {

    private static final String PPM_FILE_PATH = System.getProperty("user.dir") + File.separator + "image-small.ppm";
    private static final String PROCESSED_PPM_FILE_PATH = System.getProperty("user.dir") + File.separator + "processed-image-small.ppm";

    private static final int FORMAT_TAG = 1;
    private static final int HEIGHT_TAG = 2;
    private static final int WIDTH_TAG = 3;
    private static final int MAX_VALUE_TAG = 4;
    private static final int PIXEL_ROW_TAG = 1;

    public static void main(String[] args) {

        MPI.Init(args);
        int me = MPI.COMM_WORLD.Rank();

        if (me == 0) {
            Stopwatch stopwatch = Stopwatch.createStarted();

            final PpmFile ppmFile = new PpmFile(PPM_FILE_PATH);
            System.out.println("Read time: " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + " ms");

            final String format = ppmFile.getFormat();
            final int height = ppmFile.getHeight();
            final int width = ppmFile.getWidth();
            final int maxValue = ppmFile.getMaximumValue();
            final Rgb[][] pixels = ppmFile.getRgbPixels();

            char[] format_send = format.toCharArray();
            MPI.COMM_WORLD.Ssend(format_send, 0, 2, MPI.CHAR, 1, FORMAT_TAG);


            int[] height_send = {height};
            MPI.COMM_WORLD.Ssend(height_send, 0, 1, MPI.INT, 1, HEIGHT_TAG);


            int[] width_send = {width};
            MPI.COMM_WORLD.Ssend(width_send, 0, 1, MPI.INT, 1, WIDTH_TAG);


            int[] maxValue_send = {maxValue};
            MPI.COMM_WORLD.Ssend(maxValue_send, 0, 1, MPI.INT, 1, MAX_VALUE_TAG);

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
            char[] format_recv = new char[2];
            MPI.COMM_WORLD.Recv(format_recv, 0, 2, MPI.CHAR, 0, FORMAT_TAG);

            int[] height_recv = new int[1];
            MPI.COMM_WORLD.Recv(height_recv, 0, 1, MPI.INT, 0, HEIGHT_TAG);

            int[] width_recv = new int[1];
            MPI.COMM_WORLD.Recv(width_recv, 0, 1, MPI.INT, 0, WIDTH_TAG);

            int[] maxValue_recv = new int[1];
            MPI.COMM_WORLD.Recv(maxValue_recv, 0, 1, MPI.INT, 0, MAX_VALUE_TAG);

            final String format = new String(format_recv);
            final int height = height_recv[0];
            final int width = width_recv[0];
            final int maxValue = maxValue_recv[0];

            Rgb[][] pixels = new Rgb[height][width];

            Stopwatch stopwatch = Stopwatch.createStarted();

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    final short[] pixel = new short[3];

                    MPI.COMM_WORLD.Recv(pixel, 0,3, MPI.SHORT, 0, PIXEL_ROW_TAG);

                    final short R = pixel[0];
                    final short G = pixel[1];
                    final short B = pixel[2];
                    pixels[i][j] = new Rgb(R, G, B);
                }
            }

            final PpmFile processedPpmFile = new PpmFile(format, width, height, maxValue, pixels);
            System.out.println("Process time: " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + " ms");

            stopwatch = Stopwatch.createStarted();

            processedPpmFile.writeToFile(PROCESSED_PPM_FILE_PATH);
            System.out.println("Write time: " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + " ms");
        }

        MPI.Finalize();
    }

}