package org.example.mismatchMarker;

import org.example.comparator.ExcludedAreas;
import org.example.comparator.Mismatches;
import org.example.config.MarkingType;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface MismatchMarker {


    static BufferedImage markMismatches(Mismatches mismatches, BufferedImage bufferedImage) {
        // TODO: REPLACE WITH CONFIG
        MarkingType markingType = MarkingType.RECTANGLE;

        switch(markingType) {
            case RECTANGLE -> bufferedImage = new RectangleDraw().draw(mismatches.getPixels(), bufferedImage, Color.BLUE);
            case PAINT_OVER -> bufferedImage = new RectangleDraw().paintPixels(mismatches.getPixels(), bufferedImage, Color.BLUE);
        }

        return bufferedImage;
    }

    static BufferedImage markExcluded(ExcludedAreas excludedAreas, BufferedImage bufferedImage) {
        return new RectangleDraw().draw(excludedAreas.getPixels(), bufferedImage, Color.YELLOW);
    }
}
