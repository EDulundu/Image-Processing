import vpt.DoubleImage;
import vpt.Image;

/**
 * Created by Emre on 14.04.2017.
 */
public class ButterworthFilter {

    public static DoubleImage execute(Image mag, int D, int n, int W) {

        DoubleImage temp = new DoubleImage(mag, false);

        Point centre = new Point(mag.getXDim()/2, mag.getYDim()/2);
        double radius;

        for (int i = 0; i < mag.getXDim(); i++) {
            for (int j = 0; j < mag.getYDim(); j++) {
                radius = (double) Math.sqrt( Math.pow(i - centre.getX(), 2.0) + Math.pow(j - centre.getY(), 2.0));

                double result = (1 / (1 + Math.pow((radius * W) / (Math.pow(radius, 2.0) - D * D), (double) (2 * n))));
                temp.setXYDouble(i, j, result);
            }
        }

        return temp;
    }
}
