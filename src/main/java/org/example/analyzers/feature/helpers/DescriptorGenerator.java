package org.example.analyzers.feature.helpers;

import org.example.utils.VectorUtil;

public class DescriptorGenerator {

    /**
     * Generates 128D descriptor vector by creating magnitudes histogram in different orientations (4x4 cells).
     * Then concatenates histograms into single 128D vector.
     *
     * @param magnitudes matrix of gradient magnitudes in a window around keypoint
     * @param orientations matrix of gradient orientations in a window around keypoint
     *
     * @return float[128] vector containing keypoint descriptor.
     */
    public float[] constructDescriptor(float[][] magnitudes, float[][] orientations) {
        float binSize = 45f; // 360 degrees / 8 bins
        int descriptorLength = 128; // 16 cells x 8 bins
        float[] descriptor = new float[descriptorLength];

        int index = 0;
        for (int cellX=0; cellX<4; cellX++) {
            for (int cellY=0; cellY<4; cellY++) {
                float[] localHistogram = new float[8];

                for (int pixelX=0; pixelX<4; pixelX++) {
                    for (int pixelY=0; pixelY<4; pixelY++) {
                        int x = cellX + 4 * pixelX;
                        int y = cellY + 4 * pixelY;

                        float magnitude = magnitudes[x][y];
                        float orientation = orientations[x][y];

                        int bin = (int) Math.floor(orientation / binSize) % 8;
                        localHistogram[bin] += magnitude;
                    }
                }

                System.arraycopy(localHistogram, 0, descriptor, index, 8);
                index += 8;
            }
        }

        // normalize, clip and normalize the descriptor
        descriptor = VectorUtil.normalizeL2(descriptor);
        descriptor = VectorUtil.clip(descriptor, 0, 0.2f);
        descriptor = VectorUtil.normalizeL2(descriptor);

        return descriptor;
    }
}
