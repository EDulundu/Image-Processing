import vpt.DoubleImage;
import vpt.Image;
import vpt.algorithms.arithmetic.Multiplication;
import vpt.algorithms.display.Display2D;
import vpt.algorithms.io.Load;
import vpt.algorithms.io.Save;
import vpt.util.Tools;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Emre on 15.04.2017.
 */
public class FourierTransform {

    public static DoubleImage[] execute(Image img) {

        DoubleImage[] output = new DoubleImage[3];

        output[0] = new DoubleImage(img, false);
        output[1] = new DoubleImage(img, false);
        output[2] = new DoubleImage(img, false);

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for (int i = 0; i < img.getXDim(); i++) {

            ArrayList<Point> p = new ArrayList<Point>();
            int count = 0;
            for (int j = 0; j < img.getYDim(); j++) {

                p.add(new Point(i,j));
                ++count;
                if(count == 5) {
                    executorService.submit(new FourierTransformPack(img, output[0], output[1], output[2], p));
                    count = 0;
                    p = new ArrayList<Point>();
                }
            }

            if(count > 0 && count < 5){
                executorService.submit(new FourierTransformPack(img, output[0], output[1], output[2], p));
            }
        }
        executorService.shutdown();
        while(!executorService.isTerminated());

        output[2] = (DoubleImage) Tools.shiftOrigin(output[2]);
        return output;
    }

    public static void main(String[] args) throws IOException {

        Image img = Load.invoke("pompeii.png");

        // real ve imag.txt leri okuyarak kısa yoldan real ve imag kısımları okunur.
        // Double image okunamadıgı iicin kendi dosyaya yazdim ve okudum.


/*
        DoubleImage[] imgDFT = FourierTransform.execute(img);

        Save.invoke(imgDFT[0], "pompeii_real.png");
        Save.invoke(imgDFT[1], "pompeii_imag.png");
        Save.invoke(imgDFT[2], "pompeii_mag.png");

        BufferedWriter realwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("real.txt")));
        BufferedWriter imagwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("imag.txt")));

        for (int i = 0; i < img.getYDim(); i++) {
            for (int j = 0; j < img.getXDim(); j++) {
                realwriter.write("" + imgDFT[0].getXYDouble(j,i) + " ");
                imagwriter.write("" + imgDFT[1].getXYDouble(j,i) + " ");
            }
            realwriter.write("\n");
            imagwriter.write("\n");
        }

        realwriter.close();
        imagwriter.close();
*/

        // resmi doyaya yazdım çünkü load fonksiyonu double resim okumuyor sapıtıyor değerler. onun için.
        BufferedReader realreader = new BufferedReader(new InputStreamReader(new FileInputStream("real.txt")));
        BufferedReader imagreader = new BufferedReader(new InputStreamReader(new FileInputStream("imag.txt")));

        DoubleImage real = new DoubleImage(img, false);
        DoubleImage imag = new DoubleImage(img, false);

        for (int i = 0; i < img.getYDim(); i++) {
            StringTokenizer tokenizer = new StringTokenizer(realreader.readLine(), " ");
            StringTokenizer tokenizer1 = new StringTokenizer(imagreader.readLine(), " ");
            for (int j = 0; j < img.getXDim(); j++) {
                real.setXYDouble(j,i, Double.parseDouble(tokenizer.nextToken()));
                imag.setXYDouble(j,i, Double.parseDouble(tokenizer1.nextToken()));
            }
        }

        realreader.close();
        imagreader.close();

        DoubleImage bandreject = ButterworthFilter.execute(img, 100, 15 , 10);
        bandreject = (DoubleImage) Tools.shiftOrigin(bandreject);

        // bu değer 0.25 farklı değerde alabilirdi ama başka bir arkadaş bu değer iyi çıkarıyor diye bunu aldım.
        for (int i = 0; i < bandreject.getXDim(); i++) {
            for (int j = 0; j < bandreject.getYDim(); j++) {
                if(bandreject.getXYDouble(i,j) <= 0.25){
                    real.setXYDouble(i,j,0.0);
                    imag.setXYDouble(i,j,0.0);
                }
            }
        }

        DoubleImage result = InverseFourierTransform.execute(img, real, imag);
        Save.invoke(result, "pompeii_result.png");
        Display2D.invoke(result);
    }
}
