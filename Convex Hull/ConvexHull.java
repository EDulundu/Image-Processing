import vpt.Image;
import vpt.algorithms.display.Display2D;
import vpt.algorithms.io.Load;
import vpt.algorithms.io.Save;

/**
 * Created by Safa Emre DULUNDU on 13.03.2017.
 */
public class ConvexHull {

    public static Image morphologicalConvexHullOperator(Image img) {

        Image frame = img.newInstance(true);

        applyLeftMask(frame);

        applyTopMask(frame);

        applyRightMask(frame);

        applyBottomMask(frame);

        return frame;
    }

    private static void applyLeftMask(Image img) {

        boolean ans = true;

        while(ans) {

            ans = false;
            for (int i = 0; i < img.getXDim(); i++) {
                for (int j = 0; j < img.getYDim(); j++) {

                    if(i <= 0 || j <= 0 || j == img.getYDim()-1)
                        continue;

                    if(img.getXYByte(i, j) == 0) {
                        if(img.getXYByte(i-1, j-1) == 255 &&
                            img.getXYByte(i-1, j) == 255 &&
                                img.getXYByte(i-1, j+1) == 255) {
                            img.setXYByte(i, j, 255);
                            ans = true;
                        }
                    }
                }
            }
        }
    }

    private static void applyTopMask(Image img) {

        boolean ans = true;

        while(ans) {

            ans = false;
            for (int i = 0; i < img.getXDim(); i++) {
                for (int j = 0; j < img.getYDim(); j++) {

                    if(i <= 0 || j <= 0 || i == img.getXDim()-1)
                        continue;

                    if(img.getXYByte(i, j) == 0) {
                        if(img.getXYByte(i-1, j-1) == 255 &&
                                img.getXYByte(i, j-1) == 255 &&
                                img.getXYByte(i+1, j-1) == 255) {
                            img.setXYByte(i, j, 255);
                            ans = true;
                        }
                    }
                }
            }
        }
    }

    private static void applyRightMask(Image img) {

        boolean ans = true;

        while(ans) {

            ans = false;
            for (int i = 0; i < img.getXDim(); i++) {
                for (int j = 0; j < img.getYDim(); j++) {

                    if (j <= 0 ||j == img.getYDim()-1 || i == img.getXDim() - 1)
                        continue;

                    if (img.getXYByte(i, j) == 0) {
                        if (img.getXYByte(i + 1, j - 1) == 255 &&
                                img.getXYByte(i + 1, j) == 255 &&
                                img.getXYByte(i + 1, j + 1) == 255) {
                            img.setXYByte(i, j, 255);
                            ans = true;
                        }
                    }
                }
            }
        }
    }

    private static void applyBottomMask(Image img) {

        boolean ans = true;

        while(ans) {

            ans = false;
            for (int i = 0; i < img.getXDim(); i++) {
                for (int j = 0; j < img.getYDim(); j++) {

                    if (i <= 0 || j == img.getYDim() - 1 || i == img.getXDim() - 1)
                        continue;

                    if (img.getXYByte(i, j) == 0) {
                        if (img.getXYByte(i + 1, j + 1) == 255 &&
                                img.getXYByte(i, j + 1) == 255 &&
                                img.getXYByte(i - 1, j + 1) == 255) {
                            img.setXYByte(i, j, 255);
                            ans = true;
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {

        Image img = Load.invoke("perfectly_painted_cat.png");

        Image output = ConvexHull.morphologicalConvexHullOperator(img);

        Display2D.invoke(output, "Morphological Convex Hull Operator");

        Save.invoke(output, "output_cat.png");
    }
}
