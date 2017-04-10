import vpt.ByteImage;
import vpt.Image;
import vpt.algorithms.display.Display2D;
import vpt.algorithms.io.Load;
import vpt.algorithms.io.Save;

/**
 * Created by Safa Emre DULUNDU on 2/27/17.
 */
public class Resizer {

    public static Image scale(Image img, double sx, double sy, int strat) {

        Image output;

        if(strat == 0) {
            output = nearestNeighborInterpolation(img, sx, sy);
        } else if(strat == 1) {
            output = bilinearInterpolation(img, sx, sy);
        } else if(strat == 2) {
            output = null;
        } else {
            System.err.println("Do not find such Method!!!");
            return null;
        }

        return output;
    }

    private static Image nearestNeighborInterpolation(Image img, double sx, double sy) {

        int newWidth = (int)(img.getXDim() * sx);
        int newHeight = (int)(img.getYDim() * sy);

        // tek resimli yeni bir resim olusturur.
        Image output = new ByteImage(newWidth, newHeight, 1);

        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {

                int v = (int) (i / sx);
                int w = (int) (j / sy);

                output.setXYByte(i,j, img.getXYByte(v,w));
            }
        }

        return output;
    }

    private static Image bilinearInterpolation(Image img, double sx, double sy) {

        int newWidth = (int)(img.getXDim() * sx);
        int newHeight = (int)(img.getYDim() * sy);

        Image output = new ByteImage(newWidth, newHeight, 1);

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {

                float ax = x / (float)(newWidth) * (img.getXDim()-1);
                float ay = y / (float)(newHeight) * (img.getYDim()-1);
                int axi = (int)ax;
                int ayi = (int)ay;

                // elimizdeki bilinen 4 deger
                int a00 = img.getXYByte(axi, ayi);
                int a10 = img.getXYByte(axi+1, ayi);
                int a01 = img.getXYByte(axi, ayi+1);
                int a11 = img.getXYByte(axi+1, ayi+1);

                // denklem cozulur 4 komsu
                int result = 0;
                for (int i = 0; i < 3; i++) {
                    result |= (int)bilinear(a00 >> (i * 8) & 0xFF, a10 >> (i * 8) & 0xFF,
                                         a01 >> (i * 8) & 0xFF, a11 >> (i * 8) & 0xFF,
                                         ax - axi, ay - ayi) << (i * 8);
                }

                output.setXYByte(x,y, result);
            }
        }

        return output;
    }

    private static float bilinear(float a00, float a10, float a01, float a11, float sx, float sy) {

        float result = (a00 + (a10 - a00) * sx);
        float result2 = (a01 + (a11 - a01) * sx);
        float ans = (result + (result2 - result) * sy);

        return ans;
    }

    public static Image equalize(Image img) {

        Image output = new ByteImage(img.getXDim(), img.getYDim(), 1);

        int[] intensityCount = new int[256];
        for (int i = 0; i < img.getXDim(); i++)
            for (int j = 0; j < img.getYDim(); j++)
                ++intensityCount[img.getXYByte(i,j)];

        double[] cumulativeProb = new double[256];
        for (int i = 0; i < 256; i++)
            cumulativeProb[i] = ((double)intensityCount[i]) / ((double)img.getSize());

        for (int i = 1; i < 256; i++)
            cumulativeProb[i] = cumulativeProb[i-1] + cumulativeProb[i];

        for (int i = 0; i < 256; i++)
            cumulativeProb[i] = Math.round(255 * cumulativeProb[i]);

        for (int i = 0; i < img.getXDim(); i++)
            for (int j = 0; j < img.getYDim(); j++)
                output.setXYByte(i,j, (int)cumulativeProb[img.getXYByte(i,j)]);

        return output;
    }

    public static Image equalizeAdaptively(Image img) {

        int col = img.getXDim() / 8;
        int row = img.getYDim() / 8;

        Image output = new ByteImage(img.getXDim(), img.getYDim(), 1);

        int[][] tilesHistogram = new int[64][256];
        double[][] tilesCumulative = new double[64][256];

        for (int i = 0; i < img.getXDim(); i++) {
            for (int j = 0; j < img.getYDim(); j++) {
                int p = (i / col) + (8 * (j / row));
                ++tilesHistogram[p][img.getXYByte(i, j)];
            }
        }

        for (int i = 0; i < tilesHistogram.length; i++) {
            for (int j = 0; j < 256; j++) {
                tilesCumulative[i][j] = ((double)tilesHistogram[i][j]) / ((double) (row * col));
            }
        }

        for (int i = 0; i < tilesCumulative.length; i++) {
            for (int j = 1; j < 256; j++) {
                tilesCumulative[i][j] = tilesCumulative[i][j-1] + tilesCumulative[i][j];
            }
        }

        for (int i = 0; i < tilesCumulative.length; i++) {
            for (int j = 0; j < 256; j++) {
                tilesCumulative[i][j] = Math.round(255 * tilesCumulative[i][j]);
            }
        }

        for (int i = 0; i < img.getXDim(); i++) {
            for (int j = 0; j < img.getYDim(); j++) {
                int p = (i / col) + (8 * (j / row));
                output.setXYByte(i, j, (int)tilesCumulative[p][img.getXYByte(i, j)]);
            }
        }

        return output;
    }

    public static Image equalizeTogether(Image img) {

        int col = img.getXDim() / 8;
        int row = img.getYDim() / 8;

        Image output = new ByteImage(img.getXDim(), img.getYDim(), 1);

        int[][] tilesHistogram = new int[64][256];
        double[][] tilesCumulative = new double[64][256];

        for (int i = 0; i < img.getXDim(); i++) {
            for (int j = 0; j < img.getYDim(); j++) {
                int p = (i / col) + (8 * (j / row));
                ++tilesHistogram[p][img.getXYByte(i, j)];
            }
        }

        for (int i = 0; i < tilesHistogram.length; i++) {
            for (int j = 0; j < 256; j++) {
                int east = i + 1;
                int south = i + 8;
                int southEast = i + 9;

                if(i % 8 == 7) {
                    if(i == 63)
                        continue;
                    else
                        tilesHistogram[i][j] += tilesHistogram[south][j];
                } else if( i >= 56 && i < 63)
                    tilesHistogram[i][j] += tilesHistogram[east][j];
                else
                    tilesHistogram[i][j] += tilesHistogram[east][j] + tilesHistogram[south][j] + tilesHistogram[southEast][j];
            }
        }

        for (int i = 0; i < tilesHistogram.length; i++) {
            for (int j = 0; j < 256; j++) {
                if(i % 8 == 7) {
                    if(i == 63)
                        tilesCumulative[i][j] = ((double)tilesHistogram[i][j]) / ((double) (row * col));
                    else
                        tilesCumulative[i][j] = ((double)tilesHistogram[i][j]) / ((double) (row * col * 2));
                } else if( i >= 56 && i < 63)
                    tilesCumulative[i][j] = ((double)tilesHistogram[i][j]) / ((double) (row * col * 2));
                else
                    tilesCumulative[i][j] = ((double)tilesHistogram[i][j]) / ((double) (row * col * 4));
            }
        }

        for (int i = 0; i < tilesCumulative.length; i++) {
            for (int j = 1; j < 256; j++) {
                tilesCumulative[i][j] = tilesCumulative[i][j-1] + tilesCumulative[i][j];
            }
        }

        for (int i = 0; i < tilesCumulative.length; i++) {
            for (int j = 0; j < 256; j++) {
                tilesCumulative[i][j] = Math.round(255 * tilesCumulative[i][j]);
            }
        }

        for (int i = 0; i < img.getXDim(); i++) {
            for (int j = 0; j < img.getYDim(); j++) {
                int p = (i / col) + (8 * (j / row));
                output.setXYByte(i, j, (int)tilesCumulative[p][img.getXYByte(i, j)]);
            }
        }

        return output;
    }

    public static void main(String[] args) {

        Image img = Load.invoke("valve.png");

        Display2D.invoke(img, "Original");


        Image output = Resizer.scale(img, 2.0, 2.0, 0);

        Display2D.invoke(output, "NNI");

        Save.invoke(output, "outputImage/NNIValve.png");


        Image output2 = Resizer.scale(img, 2.0, 2.0, 1);

        Display2D.invoke(output2, "BLI");

        Save.invoke(output2, "outputImage/BLIValve.png");

        Image output3 = Resizer.equalize(img);

        Display2D.invoke(output3, "Equalize");

        Save.invoke(output3, "outputImage/EqualizeValve.png");


        Image output4 = Resizer.equalizeAdaptively(img);

        Display2D.invoke(output4, "Equalize Adaptively");

        Save.invoke(output4, "outputImage/EqualizeAdaptivelyValve.png");


        Image output5 = Resizer.equalizeTogether(img);

        Display2D.invoke(output5, "Equalize Together");

        Save.invoke(output5, "outputImage/EqualizeTogetherValve.png");
    }
}
