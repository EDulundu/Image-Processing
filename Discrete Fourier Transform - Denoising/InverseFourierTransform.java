import vpt.DoubleImage;
import vpt.Image;
import vpt.algorithms.display.Display2D;
import vpt.util.Tools;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Emre on 14.04.2017.
 */
public class InverseFourierTransform {

    public static DoubleImage execute(Image img, DoubleImage real, DoubleImage imag) {

        DoubleImage output = (DoubleImage) real.newInstance(false);

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for (int i = 0; i < img.getXDim(); i++){

            ArrayList<Point> p = new ArrayList<Point>();
            int count = 0;
            for (int j = 0; j < img.getYDim(); j++) {

                p.add(new Point(i,j));
                ++count;
                if(count == 5) {
                    executorService.submit(new InverseFourierTransformPack(img, real, imag, output, p));
                    count = 0;
                    p = new ArrayList<Point>();
                }
            }

            if(count > 0 && count < 5){
                executorService.submit(new InverseFourierTransformPack(img, real, imag, output, p));
            }
        }
        executorService.shutdown();
        while(!executorService.isTerminated());

        return output;
    }
}
