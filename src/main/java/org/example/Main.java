package org.example;

import org.example.analyzers.ssim.SSIMAnalyzer;
import org.example.comparators.SSIMComparatorConfig;
import org.example.utils.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedImage image1 = ImageIO.read(new File("src/image.png"));
        BufferedImage image2 = ImageIO.read(new File("src/image.png"));

        //image2 = ImageUtil.resize(image2, image1.getWidth(), image1.getHeight());

        long startTime = System.nanoTime();
        new SSIMAnalyzer(new SSIMComparatorConfig()).compareImages(image1, image2);
        long endTime = System.nanoTime();
        System.out.println("Time taken: " + (endTime - startTime));
    }

}