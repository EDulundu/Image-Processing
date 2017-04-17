import vpt.DoubleImage;
import vpt.Image;
import java.util.ArrayList;

/**
 * Created by Emre on 15.04.2017.
 */
public class FourierTransformPack implements Runnable {

    private ArrayList<Point> pixels = new ArrayList<Point>();

    private Image input = null;

    private DoubleImage realImage = null;

    private DoubleImage imaginaryImage = null;

    private DoubleImage magnitudeImage = null;

    public FourierTransformPack(Image img, DoubleImage realImage, DoubleImage imaginaryImage, DoubleImage magnitudeImage, ArrayList<Point> p) {

        this.input = img;
        this.realImage = realImage;
        this.imaginaryImage = imaginaryImage;
        this.magnitudeImage = magnitudeImage;
        this.pixels = p;
    }

    @Override
    public void run() {

        int xdim = input.getXDim();
        int ydim = input.getYDim();

        for (Point point: pixels) {
            double real = 0.0;
            double imag = 0.0;

            for(int u = 0; u < xdim; ++u) {
                for(int v = 0; v < ydim; ++v) {
                    double tmp = ((double)u * (double) point.getX() / (double)xdim + (double)v * (double)point.getY() / (double)ydim) * -6.283185307179586D;
                    real += input.getXYDouble(u, v) * Math.cos(tmp);
                    imag += input.getXYDouble(u, v) * Math.sin(tmp);
                }
            }

            real /= Math.sqrt((double)(xdim * ydim));
            imag /= Math.sqrt((double)(xdim * ydim));

            realImage.setXYDouble(point.getX(), point.getY(), real);
            imaginaryImage.setXYDouble(point.getX(), point.getY(), imag);
            magnitudeImage.setXYDouble(point.getX(), point.getY(), Math.sqrt(real * real + imag * imag));
        }

    }
}
