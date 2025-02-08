package org.example;

import org.example.analyzers.feature.MatrixGaussianHelper;
import org.example.analyzers.feature.BIGaussianHelper;
import org.example.analyzers.feature.MatrixSIFTAnalyzer;
import org.example.utils.ImageUtil;
import org.example.utils.accessor.ImageAccessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    // TODO: test all ImageUtil method:
    //  check greyscale
    //  check resize
    //  check gaussian

    // TODO: Comparison should  be accessed via separate ComparatorObjects
    //  ORBComparator, HashComparator and DirectComparator, all should be able to accept Config and Images
    //  Excluded areas and should be accepted by "compare()" method

    public static void main(String[] args) throws IOException {
        BufferedImage testImage = ImageIO.read(new File("src/TestImage.jpg"));
        BufferedImage actualImage = ImageIO.read(new File("src/image3.png"));
        BufferedImage checkedImage = ImageIO.read(new File("src/image4.png"));

        testImage = ImageUtil.resize(testImage, 1024, 512);
        File file = new File("src/baseImage.png");
        try {
            ImageIO.write(testImage, "PNG", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ImageAccessor accessor = ImageAccessor.create(testImage);
        int[][] raster2D = accessor.getPixels();

        BIGaussianHelper helper = new BIGaussianHelper();
        MatrixGaussianHelper arrHelper = new MatrixGaussianHelper(1.6);

        long start = System.nanoTime();

        new MatrixSIFTAnalyzer().constructScaleSpace(raster2D);
        //new BISIFTAnalyzer().constructScaleSpace(actualImage);

        //new HashComparator().comparePHash(actualImage, checkedImage);
        //new SIFTAnalyzer().constructScaleSpace(actualImage);

        long end = System.nanoTime();
        System.out.println("Time taken: " + (end-start) + "ns");
    }
}