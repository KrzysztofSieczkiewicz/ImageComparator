package org.example.mismatchMarker;

import org.example.accessor.ImageAccessor;
import org.example.comparator.Mismatches;
import org.example.utils.ImageUtil;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

public class RectangleDraw {

    private int offset = 3;
    private int thickness = 4;
    private Color lineColor = Color.BLUE;

    // TODO: REPLACE MISMATCHES WITH BOOLEAN MATRIX, THE SAME FOR paintPixels() - ALLOW TO BE REUSED FOR EXCLUDED AREAS AND MISMATCHES
    public BufferedImage draw(boolean[][] pixels, BufferedImage checkedImage, Color color) {
        List<Rectangle> groups = new MismatchManager(5).groupMismatches(pixels);
        BufferedImage mismatchedImage = ImageUtil.deepCopy(checkedImage);
        Graphics2D g2d = mismatchedImage.createGraphics();

        g2d.setColor(color);
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

    // TODO: Move result image deep copy from this class - it should not be recopied each time sth is painted
    public BufferedImage paintPixels(boolean[][] pixels, BufferedImage checkedImage, Color color) {
        BufferedImage mismatchedImage = ImageUtil.deepCopy(checkedImage);
        ImageAccessor mismatchedAccessor = ImageAccessor.create(mismatchedImage);

        int width = pixels.length -1;
        int height = pixels[0].length -1;

        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                if(pixels[x][y]) mismatchedAccessor.setPixel(x,y, 255, color.getRed(), color.getGreen(), color.getBlue());
            }
        }

        return mismatchedImage;
    }

}