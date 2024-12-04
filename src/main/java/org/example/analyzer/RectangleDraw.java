package org.example.analyzer;

import org.example.accessor.ImageAccessor;
import org.example.utils.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class RectangleDraw {

    private int offset = 3;
    private int thickness = 2;

    public BufferedImage draw(List<Rectangle> groups, BufferedImage checkedImage) {
        BufferedImage mismatchedImage = ImageUtil.copy(checkedImage);
        Graphics2D g2d = mismatchedImage.createGraphics();

        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(thickness));

        groups.forEach( group -> g2d.drawRect(
                    group.x-offset,
                    group.y-offset,
                    group.width+2*offset,
                    group.height+2*offset)
        );

        g2d.dispose();

        return mismatchedImage;
    }

    public BufferedImage drawWithAccessor(List<Rectangle> groups, BufferedImage checkedImage) {
        BufferedImage mismatchedImage = ImageUtil.copy(checkedImage);
        ImageAccessor imageAccessor = ImageAccessor.create(mismatchedImage);
//        Graphics2D g2d = mismatchedImage.createGraphics();
//
//        g2d.setColor(Color.BLUE);
//        g2d.setStroke(new BasicStroke(thickness));

        groups.forEach( group -> drawRectangle(
                imageAccessor,
                group.x-offset,
                group.y-offset,
                group.width+2*offset,
                group.height+2*offset,
                thickness)

        );

        return mismatchedImage;
    }

    private void drawRectangle(ImageAccessor imageAccessor, int X, int Y, int width, int height, int thickness) {
        int imageWidth = imageAccessor.getWidth();
        int imageHeight = imageAccessor.getHeight();
        int startX = Math.max(X, 0);
        int startY = Math.max(Y, 0);
        int endX = Math.min(X + width, imageWidth - 1);
        int endY = Math.min(Y + height, imageHeight - 1);

        for (int t = 0; t < thickness; t++) {
            int topY = Math.min(startY + t, imageHeight - 1);
            int bottomY = Math.max(endY - t, 0);
            for (int x = startX; x <= endX; x++) {
                imageAccessor.setPixel(x, topY, 255, 255, 255, 0); // Top edge
                imageAccessor.setPixel(x, bottomY, 255, 255, 255, 0); // Bottom edge
            }

            int leftX = Math.min(startX + t, imageWidth - 1);
            int rightX = Math.max(endX - t, 0);
            for (int y = startY; y <= endY; y++) {
                imageAccessor.setPixel(leftX, y, 255, 255, 255, 0); // Left edge
                imageAccessor.setPixel(rightX, y, 255, 255, 255, 0); // Right edge
            }
        }
    }
}