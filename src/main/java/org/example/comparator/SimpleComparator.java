package org.example.comparator;

import org.example.accessor.ImageAccessor;
import org.example.utils.PixelColorUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class SimpleComparator implements ImageComparator {

    @Override
    public void compare(BufferedImage actual, BufferedImage checked) {
        float threshold = 20f;

        ImageAccessor expectedAccessor = ImageAccessor.create(actual);
        ImageAccessor actualAccessor = ImageAccessor.create(checked);

        int width = actual.getWidth();
        int height = actual.getHeight();

        BufferedImage mismatch = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        ImageAccessor mismatchAccessor = ImageAccessor.create(mismatch);

        for (int x=0; x<actual.getWidth(); x++) {
            for (int y=0; y<actual.getHeight(); y++) {

                double distance = PixelColorUtil.calculateDistanceRGB(
                        expectedAccessor.getPixel(x,y),
                        actualAccessor.getPixel(x,y) );

//                System.out.print(expectedAccessor.getPixel(x,y) + ", ");
//                System.out.print(actualAccessor.getPixel(x,y) + ", ");
//                System.out.print(distance + "\n");

//                System.out.print(expectedAccessor.getAlpha(x,y) + ", ");
//                System.out.print(expectedAccessor.getRed(x,y) + ", ");
//                System.out.print(expectedAccessor.getGreen(x,y) + ", ");
//                System.out.print(expectedAccessor.getBlue(x,y) + "\n");
//
//                System.out.print(actualAccessor.getAlpha(x,y) + ", ");
//                System.out.print(actualAccessor.getRed(x,y) + ", ");
//                System.out.print(actualAccessor.getGreen(x,y) + ", ");
//                System.out.print(actualAccessor.getBlue(x,y) + "\n");

                if (distance >= threshold)
                    mismatchAccessor.setPixel(x,y, 0, 255, 0, 0);
            }
        }

        File outputfile = new File("image.jpg");
        try {
            ImageIO.write(mismatch, "jpg", outputfile);
        } catch (Exception ignored) {
            System.out.println("Failed to write mismatch image");
        }
    }

}