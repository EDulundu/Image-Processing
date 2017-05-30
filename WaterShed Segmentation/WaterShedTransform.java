import vpt.Image;
import vpt.algorithms.display.Display2D;
import vpt.algorithms.io.Load;
import vpt.algorithms.segmentation.Watershed;

import java.util.*;

/**
 * Created by Emre on 12.05.2017.
 */
public class WaterShedTransform {

    private static final int MASK = -2;

    private static final int WSHED = 0;

    private static final int INIT = -1;

    private static final int INQUEUE = -3;

    public static Image applyWaterShed(Image input) {

        // initializations
        Image output = input.newInstance(true);
        output.fill(INIT);
        int currentLabel = 0;
        boolean flag = false;

        // set of neighbours
//        HashMap<Point, ArrayList<Point>> neighbours = calculateTheNeighbours(input);

        // fifo
        Queue waterShedFifo = new LinkedList();
        Image temp = output.newInstance(true);
        //
        int HMIN = 0;
        int HMAX = 90;
        for (int h = HMIN; h < HMAX; h++) {

            // loop 1
            for (int i = 0; i < input.getXDim(); i++) {
                for (int j = 0; j < input.getYDim(); j++) {

                    int pixelValue = input.getXYByte(i,j);
                    if(pixelValue == h) {

                        output.setXYInt(i, j, MASK);
                        if(areThereLabelledNeighboursWithWshed(i, j, output)) {
                            output.setXYInt(i, j, INQUEUE);
                            waterShedFifo.add(new Point(i, j));
                        }
                    }
                }
            }

            // loop 2
            while (!waterShedFifo.isEmpty()) {

                Point p = (Point) waterShedFifo.poll();
                int x = p.getX(), y = p.getY();

                for(int j = y - 1; j <= y + 1; ++j) {
                    for(int i = x - 1; i <= x + 1; ++i) {

                        if(i >= 0 && i < output.getXDim() && j >= 0 && j < output.getYDim()) {

                            if((i != x || j != y) && output.getXYInt(i, j) > 0) {

                                if(output.getXYInt(x, y) == INQUEUE || (output.getXYInt(x, y) == WSHED && flag)) {
                                    output.setXYInt(x, y, output.getXYInt(i, j));
                                } else if(output.getXYInt(x, y) > 0 && output.getXYInt(x, y) != output.getXYInt(i, j)) {
                                    output.setXYInt(x, y, WSHED);
                                    flag = false;
                                }

                            } else if(output.getXYInt(i, j) == WSHED) {

                                if(output.getXYInt(x, y) == INQUEUE) {
                                    output.setXYInt(x, y, WSHED);
                                    flag = true;
                                }

                            } else if(output.getXYInt(i, j) == MASK) {
                                output.setXYInt(i,j, INQUEUE);
                                waterShedFifo.add(new Point(i, j));
                            }
                        }
                    }
                }
            }

            // loop 3
            for (int i = 0; i < input.getXDim(); i++) {
                for (int j = 0; j < input.getYDim(); j++) {

                    int pixelValue = input.getXYByte(i,j);
                    if(pixelValue == h) {

                        if(output.getXYInt(i, j) == MASK) {

                            currentLabel = currentLabel + 1;
                            waterShedFifo.add(new Point(i, j));
                            output.setXYInt(i, j, currentLabel);

                            while (!waterShedFifo.isEmpty()) {

                                Point p = (Point) waterShedFifo.poll();
                                int x = p.getX(), y = p.getY();

                                for(int l = y - 1; l <= y + 1; ++l) {
                                    for(int k = x - 1; k <= x + 1; ++k) {
                                        if(k >= 0 && k < output.getXDim() && l >= 0 && l < output.getYDim()
                                                && (k != x || l != y) && output.getXYInt(k, l) == MASK) {
                                            waterShedFifo.add(new Point(k, l));
                                            output.setXYInt(k, l, currentLabel);
                                        }
                                    }
                                }

                            }

                        }

                    }
                    else {

                    }

                }
            }

//            Display2D.invoke(output);
        }
        System.out.println("asd");
        return output;
    }

    private static boolean areThereLabelledNeighboursWithWshed(int x, int y, Image output) {

        for(int j = y - 1; j <= y + 1; ++j) {
            for(int i = x - 1; i <= x + 1; ++i) {
                if(i >= 0 && i < output.getXDim() && j >= 0
                        && j < output.getYDim()
                        && (i != x || j != y)
                        && output.getXYInt(i, j) >= WSHED) {
                    return true;
                }
            }
        }

        return false;
    }

    private static HashMap<Point, ArrayList<Point>> calculateTheNeighbours(Image img) {

        HashMap<Point, ArrayList<Point>> neighbours = new HashMap<>(img.getSize());

        for (int i = 0; i < img.getXDim(); i++) {
            for (int j = 0; j < img.getYDim(); j++) {

                Point pixel = new Point(i,j);
                ArrayList<Point> komsular = new ArrayList<>();
                addNeighbour(komsular, img, i - 1, j - 1);
                addNeighbour(komsular, img, i, j - 1);
                addNeighbour(komsular, img, i + 1, j - 1);

                addNeighbour(komsular, img, i - 1, j);
                addNeighbour(komsular, img, i + 1, j);

                addNeighbour(komsular, img, i - 1, j + 1);
                addNeighbour(komsular, img, i , j + 1);
                addNeighbour(komsular, img, i + 1, j + 1);

                neighbours.put(pixel, komsular);
            }
        }

        return neighbours;
    }

    private static void addNeighbour(ArrayList<Point> komsular, Image img, int i, int j) {

        if(i >= 0 && j >= 0 && i < img.getXDim() && j < img.getYDim())
            komsular.add(new Point(i, j));
    }

    private static int[] sortPixel(Image input) {

        int size = input.getXDim() * input.getYDim();
        int[] arrays = new int[size];

        int k = 0;
        for (int i = 0; i < input.getXDim(); i++)
            for (int j = 0; j < input.getYDim(); j++)
                arrays[k++] = input.getXYByte(i,j);

        Arrays.sort(arrays);

        return arrays;
    }

    public static void main(String[] args) {

        Image img = Load.invoke("question_3_watershed_coins.jpg");

        Image out = WaterShedTransform.applyWaterShed(img);

        Display2D.invoke(img);
        Display2D.invoke(out);
    }
}
