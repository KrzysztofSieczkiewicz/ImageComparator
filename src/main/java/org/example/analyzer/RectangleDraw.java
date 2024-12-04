package org.example.analyzer;

import org.example.accessor.ImageAccessor;
import org.example.utils.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class RectangleDraw {

    private int offset = 3;
    private int thickness = 4;
    private Color lineColor = Color.BLUE;

    public BufferedImage draw(List<Rectangle> groups, BufferedImage checkedImage) {
        BufferedImage mismatchedImage = ImageUtil.copy(checkedImage);
        Graphics2D g2d = mismatchedImage.createGraphics();

        g2d.setColor(lineColor);
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
}