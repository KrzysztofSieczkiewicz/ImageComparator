package org.example.mismatchMarker;

import org.example.config.MarkingType;

import java.awt.image.BufferedImage;

public interface MismatchMarker {


    static BufferedImage mark(boolean[][] mismatches, BufferedImage bufferedImage) {
        // TODO: REPLACE WITH CONFIG
        MarkingType markingType = MarkingType.RECTANGLE;
        
        BufferedImage markedImage = null;
        
        switch(markingType) {
            case RECTANGLE -> markedImage = new RectangleDraw().draw(mismatches, bufferedImage);
            case PAINT_OVER -> markedImage = new RectangleDraw().paintPixels(mismatches, bufferedImage);
        }
        
        return markedImage;
    }
}
