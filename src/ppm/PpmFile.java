package ppm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pixel.Rgb;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PpmFile {

    private String format;
    private int width;
    private int height;
    private int maximumValue;
    private Rgb[][] rgbPixels;

    public PpmFile(String path) {
        File file = new File(path);
        loadDataFromFile(file);
    }

    public PpmFile(File file) {
        loadDataFromFile(file);
    }

    public void loadDataFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            format = reader.readLine();

            line = reader.readLine();

            width = Integer.parseInt(line.split(" ")[0]);
            height = Integer.parseInt(line.split(" ")[1]);

            maximumValue = Integer.parseInt(reader.readLine());
            rgbPixels = new Rgb[height][width];

            int pixelsRead = 0;

            do {
                line = reader.readLine();

                // skip blank lines
                if (line.length() == 0)
                    continue;

                final List<Short> bytes = Arrays.stream(line.split(" "))
                        .map(Short::parseShort)
                        .collect(Collectors.toList());

                // each line has a multiple of 3 number of bytes
                for (int i = 0; i < bytes.size(); i += 3) {
                    int R = bytes.get(i);
                    int G = bytes.get(i + 1);
                    int B = bytes.get(i + 2);

                    // calculate position in the RGB matrix based on the number of pixels read
                    int currentHeight = pixelsRead / width;
                    int currentWidth = pixelsRead % width;

                    rgbPixels[currentHeight][currentWidth] = new Rgb(R, G, B);

                    pixelsRead++;
                }

            // stop when all pixels are read
            } while (pixelsRead < width * height);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(String path) {
        File file = new File(path);
        writeToFile(file);
    }

    public void writeToFile(File file) {

        try {
            if (file.exists())
                file.delete();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String fileHeader = format + "\n" + width + " " + height + "\n" + maximumValue + "\n";
            writer.write(fileHeader);

            for (int i = 0; i < height; i++)
                for (int j = 0; j < width; j++) {
                    Rgb pixel = rgbPixels[i][j];
                    int R = pixel.getR();
                    int G = pixel.getG();
                    int B = pixel.getB();

                    String rgbPixel = R + "\n" + G + "\n" + B + "\n";
                    writer.append(rgbPixel);
                }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
