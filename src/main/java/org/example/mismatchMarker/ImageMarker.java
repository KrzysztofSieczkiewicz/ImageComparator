package org.example.mismatchMarker;

import org.example.utils.accessor.ImageAccessor;
import org.example.analyzers.ExcludedAreas;
import org.example.config.DirectComparatorConfig;
import org.example.config.MarkingType;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class ImageMarker {
    private final int rectangleOffset;
    private final int lineThickness;

    MarkingType mismatchedMarkingType;
    Color mismatchMarkingColor;

    MarkingType excludedMarkingType;
    Color excludedMarkingColor;

    public ImageMarker(DirectComparatorConfig config) {
        this.rectangleOffset = config.getRectangleMarkingOffset();
        this.lineThickness = config.getMarkingLineThickness();

        this.mismatchedMarkingType = config.getMismatchedAreasMarking();
        this.mismatchMarkingColor = config.getMismatchMarkingColor();

        this.excludedMarkingType = config.getExcludedAreasMarking();
        this.excludedMarkingColor = config.getExcludedMarkingColor();
    }

    public BufferedImage mark(BufferedImage bufferedImage, Mismatches mismatches) {

        switch(mismatchedMarkingType) {
            case OUTLINE -> {
                Rectangle[] boundingRectangles = mismatches
                        .groupMismatches()
                        .stream()
                        .map(MismatchesGroup::getBoundingRectangle)
                        .toArray(Rectangle[]::new);

                drawRectangles(boundingRectangles, bufferedImage, mismatchMarkingColor, rectangleOffset, lineThickness);
            }
            case PAINT_OVER -> {
                paintPixels(mismatches.getPixels(), bufferedImage, mismatchMarkingColor);
            }
        }

        return bufferedImage;
    }

    public BufferedImage mark(BufferedImage bufferedImage, ExcludedAreas excludedAreas) {

        switch(excludedMarkingType) {
            case OUTLINE -> drawShape(excludedAreas.getExcluded(), bufferedImage, excludedMarkingColor, lineThickness);
            case PAINT_OVER -> paintPixels(excludedAreas.getExcluded(), bufferedImage, excludedMarkingColor, lineThickness);
        }

        return bufferedImage;
    }


    private void drawRectangles(Rectangle[] rectangles, BufferedImage image, Color lineColor, int offset, int lineThickness) {
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
    }

    private void paintPixels(ArrayList<PixelPoint> pixels, BufferedImage image, Color lineColor) {
        ImageAccessor mismatchedAccessor = ImageAccessor.create(image);

        int red = lineColor.getRed();
        int green = lineColor.getGreen();
        int blue = lineColor.getBlue();

        for (PixelPoint pixel : pixels) {
            mismatchedAccessor.setPixel(pixel.getX(), pixel.getY(), 255, red, green, blue);
        }
    }

    private void paintPixels(Area area, BufferedImage image, Color lineColor, int lineThickness) {
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(lineColor);
        g2d.setStroke(new BasicStroke(lineThickness));
        g2d.fill(area);
        g2d.dispose();
    }

    private void drawShape(Area area, BufferedImage image, Color lineColor, int lineThickness) {
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(lineColor);
        g2d.setStroke(new BasicStroke(lineThickness));
        g2d.draw(area);
        g2d.dispose();
    }
}