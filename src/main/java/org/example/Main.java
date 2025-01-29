package org.example;

import org.example.analyzers.feature.BISIFTAnalyzer;
import org.example.analyzers.feature.MatrixGaussianHelper;
import org.example.analyzers.feature.BIGaussianHelper;
import org.example.analyzers.feature.MatrixSIFTAnalyzer;
import org.example.utils.ImageUtil;
import org.example.utils.accessor.ImageAccessor;
import org.example.utils.accessor.ImageDataUtil;

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

        //actualImage = ImageUtil.resize(testImage, 1024/2, 512/2);

        ImageAccessor accessor = ImageAccessor.create(actualImage);
        int[][] raster2D = accessor.getPixels();

        BIGaussianHelper helper = new BIGaussianHelper();
        MatrixGaussianHelper arrHelper = new MatrixGaussianHelper(1.6, 6);

        long start = System.nanoTime();

        new MatrixSIFTAnalyzer().constructScaleSpace(raster2D);
        //new BISIFTAnalyzer().constructScaleSpace(actualImage);

        //new HashComparator().comparePHash(actualImage, checkedImage);
        //new SIFTAnalyzer().constructScaleSpace(actualImage);

        long end = System.nanoTime();
        System.out.println("Time taken: " + (end-start) + "ns");
    }
}