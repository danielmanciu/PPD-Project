import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
@ToString
class Rgb implements Serializable {

    private int R;
    private int G;
    private int B;

    Rgb applySepia() {
        final int newR = clamp((int) ((R * 0.393) + (G * 0.769) + (B * 0.189)));
        final int newG = clamp((int) ((R * 0.349) + (G * 0.686) + (B * 0.168)));
        final int newB = clamp((int) ((R * 0.272) + (G * 0.534) + (B * 0.131)));

        R = newR;
        G = newG;
        B = newB;

        return new Rgb(R, G, B);
    }

    private static int clamp(int val) {
        return (val < 0) ? 0 : (val > 255) ? 255 : val;
    }

}
