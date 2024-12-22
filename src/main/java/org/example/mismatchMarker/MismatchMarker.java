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
public class MismatchMarker {

    // Rectangle and Shape drawing
    private final int rectangleOffset;
    private final int lineThickness;

    MismatchMarkingType mismatchMarkingType;
    Color mismatchMarkingColor;

    ExcludedMarkingType excludedMarkingType;
    Color excludedMarkingColor;

    public MismatchMarker(DirectCompareConfig config) {
        this.rectangleOffset = config.getRectangleMarkingOffset();
        this.lineThickness = config.getMarkingLineThickness();

        this.mismatchMarkingType = config.getMismatchedAreasMarking();
        this.mismatchMarkingColor = config.getMismatchMarkingColor();

        this.excludedMarkingType = config.getExcludedAreasMarking();
        this.excludedMarkingColor = config.getExcludedMarkingColor();
    }

    public BufferedImage markMismatches(Mismatches mismatches, BufferedImage bufferedImage, DirectCompareConfig config) {

        switch(mismatchMarkingType) {
            case RECTANGLE -> {
                Rectangle[] boundingRectangles = mismatches
                        .groupMismatches()
                        .stream()
                        .map(MismatchesGroup::getBoundingRectangle)
                        .toArray(Rectangle[]::new);

                // TODO - CURRENT
                //  no need to move offset calculation logic from rectangleDraw (without that it's a single line call)
                //  this class can accept config input in the constructor
                //  dissolve RectangleDraw class and move the methods here - drop a layer

                bufferedImage = new RectangleDraw().drawRectangles(boundingRectangles, bufferedImage, mismatchMarkingColor, rectangleOffset, lineThickness);
            }
            case PAINT_OVER -> bufferedImage = new RectangleDraw().paintPixels(mismatches.getPixels(), bufferedImage, mismatchMarkingColor);
        }

        return bufferedImage;
    }

    public BufferedImage markExcluded(ExcludedAreas excludedAreas, BufferedImage bufferedImage) {

        switch(excludedMarkingType) {
            case OUTLINE -> bufferedImage = new RectangleDraw().drawShape(excludedAreas.getExcluded(), bufferedImage, excludedMarkingColor, lineThickness);
            case PAINT_OVER -> bufferedImage = new RectangleDraw().paintPixels(excludedAreas.getExcluded(), bufferedImage, excludedMarkingColor, lineThickness);
        }

        return bufferedImage;
    }
}
