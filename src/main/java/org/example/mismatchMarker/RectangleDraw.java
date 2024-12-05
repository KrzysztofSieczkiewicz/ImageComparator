package org.example.mismatchMarker;

import org.example.accessor.ImageAccessor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class RectangleDraw {

    private int offset = 3;
    private int thickness = 4;
    private Color lineColor = Color.BLUE;

    public BufferedImage draw(boolean[][] mismatches, BufferedImage checkedImage) {
        List<Rectangle> groups = new MismatchManager(5).groupMismatches(mismatches);
        Graphics2D g2d = checkedImage.createGraphics();

        g2d.setColor(lineColor);
        g2d.setStroke(new BasicStroke(thickness));

        groups.forEach( group -> g2d.drawRect(
                    group.x-offset,
                    group.y-offset,
                    group.width+2*offset,
                    group.height+2*offset)
        );

        g2d.dispose();

        return checkedImage;
    }

    public BufferedImage paintPixels(boolean[][] mismatches, BufferedImage checkedImage) {
        ImageAccessor mismatchedAccessor = ImageAccessor.create(checkedImage);
        int width = mismatches.length -1;
        int height = mismatches[0].length -1;

        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                if(mismatches[x][y]) mismatchedAccessor.setPixel(x,y, 255, 0, 0, 255);
            }
        }

        return checkedImage;
    }
}