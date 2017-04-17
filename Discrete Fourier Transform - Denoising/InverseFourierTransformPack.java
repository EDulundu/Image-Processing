import vpt.DoubleImage;
import vpt.Image;

import java.util.ArrayList;

/**
 * Created by Emre on 15.04.2017.
 */
public class InverseFourierTransformPack implements Runnable {

    private ArrayList<Point> pixels = new ArrayList<Point>();

    private Image input = null;

    private DoubleImage realImage = null;

    private DoubleImage imaginaryImage = null;

    private DoubleImage output = null;

    public InverseFourierTransformPack(Image img, DoubleImage realImage, DoubleImage imaginaryImage, DoubleImage output, ArrayList<Point> p) {

        this.input = img;
        this.realImage = realImage;
        this.imaginaryImage = imaginaryImage;
        this.output = output;
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
                    double ans = ((double)u * (double) point.getX() / (double)xdim + (double)v * (double) point.getY() / (double)ydim) * 6.283185307179586D;
                    double cr = realImage.getXYDouble(u, v);
                    double ci = imaginaryImage.getXYDouble(u, v);
                    real += cr * Math.cos(ans) - ci * Math.sin(ans);
                    imag += ci * Math.cos(ans) + cr * Math.sin(ans);
                }
            }

            real /= Math.sqrt((double)(xdim * ydim));
            imag /= Math.sqrt((double)(xdim * ydim));
            output.setXYDouble(point.getX(), point.getY(), Math.sqrt(real * real + imag * imag));
        }
    }
}
