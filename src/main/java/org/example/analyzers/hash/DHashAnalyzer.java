package org.example.analyzers.hash;

import org.example.utils.ImageUtil;
import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;
import java.util.BitSet;

public class DHashAnalyzer {
        private int reducedImageSize;

    public DHashAnalyzer(int size) {
        this.reducedImageSize = size;
    }


    public BitSet dHash(BufferedImage image) {
        BufferedImage resized = ImageUtil.resize(image, reducedImageSize, reducedImageSize);
        BufferedImage greyscaled = ImageUtil.greyscale(resized);

        ImageAccessor accessor = ImageAccessor.create(greyscaled);

        int[] values = accessor.getBlueArray();
        int length = values.length;

        BitSet hash = new BitSet(length);
        for (int i = 0; i < length; i++) {
            hash.set(i, values[i] >= values[i+1]);
        }

        return hash;
    }
}
