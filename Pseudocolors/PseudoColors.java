import vpt.DoubleImage;
import vpt.Image;
import vpt.algorithms.display.Display2D;
import vpt.algorithms.io.Load;
import vpt.algorithms.io.Save;

/**
 * Created by Emre on 11.05.2017.
 */
public class PseudoColors {

    public static Image grayLevelToColorTransformation(Image input) {

        Image output = new DoubleImage(input.getXDim(), input.getYDim(), 3);

        for (int i = 0; i < output.getXDim(); i++) {
            for (int j = 0; j < output.getYDim(); j++) {
                int pixelValue = input.getXYByte(i,j);
                output.setXYCByte(i, j, 0, redTransform(pixelValue));
                output.setXYCByte(i, j, 1, greenTransfrom(pixelValue));
                output.setXYCByte(i, j, 2, blueTransform(pixelValue));
            }
        }

        return output;
    }

    private static int redTransform(int value) {

        return value / 2 + 128;
    }

    private static int greenTransfrom(int value) {

        return value / 2 + 1;
    }

    private static int blueTransform(int value) {

        return Math.abs(value / 2 - 128) + 1;
    }

    public static void main(String[] args) {

        Image img = Load.invoke("hamdibey.jpg");

        Image out = PseudoColors.grayLevelToColorTransformation(img);

        Display2D.invoke(out, true);

//        Save.invoke(out, "out_question1.png");
    }
}
