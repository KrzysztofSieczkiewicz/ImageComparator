package org.example;

import org.example.analyzers.ssim.SSIMAnalyzer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedImage image1 = ImageIO.read(new File("src/image.png"));
        BufferedImage image2 = ImageIO.read(new File("src/image.png"));

        new SSIMAnalyzer().compareImages(image1, image2);

    }

}