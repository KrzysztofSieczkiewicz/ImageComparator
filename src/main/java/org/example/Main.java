package org.example;

import org.example.analyzers.ssim.SSIMAnalyzer;
import org.example.comparators.SSIMComparatorConfig;
import org.example.utils.ImageUtil;
import org.example.utils.accessor.ImageAccessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedImage image1 = ImageIO.read(new File("src/image.png"));
        BufferedImage image2 = ImageIO.read(new File("src/image.png"));

        long startTime = System.nanoTime();
        image2 = ImageUtil.resizeBilinear(image2, image1.getWidth(), image1.getHeight());

        double ssim = new SSIMAnalyzer(new SSIMComparatorConfig()).calculateImagesSSIM(image1, image2);
        long endTime = System.nanoTime();
        System.out.println("Time taken: " + (endTime - startTime));
        System.out.println("SSIM: " + ssim);
    }

}