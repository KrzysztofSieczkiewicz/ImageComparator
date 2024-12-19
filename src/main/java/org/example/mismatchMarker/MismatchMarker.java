package org.example.mismatchMarker;

import org.example.analyzers.ExcludedAreas;
import org.example.analyzers.Mismatches;
import org.example.analyzers.MismatchesGroup;
import org.example.config.DirectCompareConfig;
import org.example.config.ExcludedMarkingType;
import org.example.config.MismatchMarkingType;

import java.awt.*;
import java.awt.image.BufferedImage;

// TODO: CONSIDER CHANGING THIS FROM INTERFACE TO A CLASS WITH CONSTRUCTOR THAT ACCEPTS CONFIG INSTEAD
// MIGHT LOOK LESS PROFESSIONAL, BUT THERE IS NO POINT IN ADDING AN ABSTRACTION AND PROVIDING CONFIG TO EACH METHOD CALL
// METHODS HAVE NO REASON TO BE STATIC AS WELL
public interface MismatchMarker {

    static BufferedImage markMismatches(Mismatches mismatches, BufferedImage bufferedImage, DirectCompareConfig config) {
        MismatchMarkingType markingType = config.getMismatchedAreasMarking();
        //int offset = 3; // TODO: ADD TO A CONFIG

        switch(markingType) {
            case RECTANGLE -> {
                Rectangle[] boundingRectangles = mismatches
                        .groupMismatches()
                        .stream()
                        .map(MismatchesGroup::getBoundingRectangle)
                        .toArray(Rectangle[]::new);

                // TODO - CURRENT -> no need to move offset calculation logic from rectangleDraw (without that it's a single line call)
                // But You can consider moving offset variable to this class and provide it as an argument?

                bufferedImage = new RectangleDraw().drawRectangles(boundingRectangles, bufferedImage, Color.BLUE);
            }
            case PAINT_OVER -> bufferedImage = new RectangleDraw().paintPixels(mismatches.getPixels(), bufferedImage, Color.BLUE);
        }

        return bufferedImage;
    }

    static BufferedImage markExcluded(ExcludedAreas excludedAreas, BufferedImage bufferedImage, DirectCompareConfig config) {
        ExcludedMarkingType markingType = config.getExcludedAreasMarking();

        switch(markingType) {
            case OUTLINE -> bufferedImage = new RectangleDraw().drawShape(excludedAreas.getExcluded(), bufferedImage, Color.YELLOW);
            case PAINT_OVER -> bufferedImage = new RectangleDraw().paintPixels(excludedAreas.getExcluded(), bufferedImage, Color.YELLOW);
        }

        return bufferedImage;
    }
}
