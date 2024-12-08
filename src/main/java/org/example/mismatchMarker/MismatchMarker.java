package org.example.mismatchMarker;

import org.example.comparator.ExcludedAreas;
import org.example.comparator.Mismatches;
import org.example.config.MarkingType;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface MismatchMarker {


    static BufferedImage markMismatches(Mismatches mismatches, BufferedImage bufferedImage) {
        // TODO: REPLACE WITH CONFIG
        MarkingType markingType = MarkingType.PAINT_OVER;
        
        BufferedImage markedImage = null;
        
        switch(markingType) {
            case RECTANGLE -> markedImage = new RectangleDraw().draw(mismatches.getPixels(), bufferedImage, Color.BLUE);
            case PAINT_OVER -> markedImage = new RectangleDraw().paintPixels(mismatches.getPixels(), bufferedImage, Color.BLUE);
        }
        
        return markedImage;
    }

    static BufferedImage markExcluded(ExcludedAreas excludedAreas, BufferedImage bufferedImage) {
        return new RectangleDraw().paintPixels(excludedAreas.getPixels(), bufferedImage, Color.YELLOW);
    }
}
