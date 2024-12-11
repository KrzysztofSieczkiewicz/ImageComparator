package org.example.mismatchMarker;

import org.example.comparator.ExcludedAreas;
import org.example.comparator.Mismatches;
import org.example.config.ExcludedMarkingType;
import org.example.config.MismatchMarkingType;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface MismatchMarker {


    static BufferedImage markMismatches(Mismatches mismatches, BufferedImage bufferedImage) {
        // TODO: REPLACE WITH CONFIG
        MismatchMarkingType markingType = MismatchMarkingType.RECTANGLE;

        switch(markingType) {
            case RECTANGLE -> bufferedImage = new RectangleDraw().drawRectangle(mismatches.getPixels(), bufferedImage, Color.BLUE);
            case PAINT_OVER -> bufferedImage = new RectangleDraw().paintPixels(mismatches.getPixels(), bufferedImage, Color.BLUE);
        }

        return bufferedImage;
    }

    static BufferedImage markExcluded(ExcludedAreas excludedAreas, BufferedImage bufferedImage) {
        ExcludedMarkingType markingType = ExcludedMarkingType.OUTLINE;

        switch(markingType) {
            case OUTLINE -> bufferedImage = new RectangleDraw().drawShape(excludedAreas.getExcluded(), bufferedImage, Color.YELLOW);
            case PAINT_OVER -> bufferedImage = new RectangleDraw().paintPixels(excludedAreas.getExcluded(), bufferedImage, Color.YELLOW);
        }

        return bufferedImage;
    }
}
