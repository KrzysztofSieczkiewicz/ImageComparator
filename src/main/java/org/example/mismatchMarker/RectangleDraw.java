package org.example.mismatchMarker;

import org.example.accessor.ImageAccessor;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class RectangleDraw {

    public BufferedImage drawRectangles(Rectangle[] rectangles, BufferedImage image, Color lineColor, int offset, int lineThickness) {
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(lineColor);
        g2d.setStroke(new BasicStroke(lineThickness));

        for (Rectangle rectangle : rectangles) {
            g2d.drawRect(
                    rectangle.x - offset,
                    rectangle.y - offset,
                    rectangle.width + 2 * offset,
                    rectangle.height + 2 * offset
            );
        }

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

    public BufferedImage paintPixels(Area area, BufferedImage image, Color lineColor, int lineThickness) {
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(lineColor);
        g2d.setStroke(new BasicStroke(lineThickness));
        g2d.fill(area);
        g2d.dispose();

        return image;
    }

    public BufferedImage drawShape(Area area, BufferedImage image, Color lineColor, int lineThickness) {
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(lineColor);
        g2d.setStroke(new BasicStroke(lineThickness));
        g2d.draw(area);
        g2d.dispose();

        return image;
    }

}