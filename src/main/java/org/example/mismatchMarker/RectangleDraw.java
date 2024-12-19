package org.example.mismatchMarker;

import org.example.accessor.ImageAccessor;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class RectangleDraw {

    private int offset = 5;
    private int thickness = 2;

    public BufferedImage drawRectangle(ArrayList<PixelPoint> pixels, BufferedImage image, Color lineColor) {
        List<Rectangle> groups = new MismatchManager().groupMismatches(pixels);
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

    public BufferedImage paintPixels(ArrayList<PixelPoint> pixels, BufferedImage image, Color lineColor) {
        ImageAccessor mismatchedAccessor = ImageAccessor.create(image);

        int red = lineColor.getRed();
        int green = lineColor.getGreen();
        int blue = lineColor.getBlue();

        for (PixelPoint pixel : pixels) {
            mismatchedAccessor.setPixel(pixel.getX(), pixel.getY(), 255, red, green, blue);
        }

        return image;
    }

    public BufferedImage paintPixels(Area area, BufferedImage image, Color lineColor) {
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(lineColor);
        g2d.setStroke(new BasicStroke(thickness));
        g2d.fill(area);
        g2d.dispose();

        return image;
    }

    public BufferedImage drawShape(Area area, BufferedImage image, Color lineColor) {
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(lineColor);
        g2d.setStroke(new BasicStroke(thickness));
        g2d.draw(area);
        g2d.dispose();

        return image;
    }

}