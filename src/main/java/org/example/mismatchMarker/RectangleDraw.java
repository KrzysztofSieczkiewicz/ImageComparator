package org.example.mismatchMarker;

import org.example.accessor.ImageAccessor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.List;

public class RectangleDraw {

    private int offset = 3;
    private int thickness = 4;

    // TODO: Move result image deep copy from this class - it should not be recopied each time sth is painted

    public BufferedImage draw(HashSet<int[]> pixels, BufferedImage image, Color lineColor) {
        List<Rectangle> groups = new MismatchManager(5).groupMismatches(pixels);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(lineColor);
        g2d.setStroke(new BasicStroke(thickness));

        groups.forEach( group -> g2d.drawRect(
                    group.x-offset,
                    group.y-offset,
                    group.width+2*offset,
                    group.height+2*offset)
        );

        g2d.dispose();

        return image;
    }

    public BufferedImage paintPixels(HashSet<int[]> pixels, BufferedImage image, Color lineColor) {
        ImageAccessor mismatchedAccessor = ImageAccessor.create(image);

        int red = lineColor.getRed();
        int green = lineColor.getGreen();
        int blue = lineColor.getBlue();

        for (int[] pixel : pixels) {
            mismatchedAccessor.setPixel(pixel[0], pixel[1], 255, red, green, blue);
        }

        return image;
    }

}