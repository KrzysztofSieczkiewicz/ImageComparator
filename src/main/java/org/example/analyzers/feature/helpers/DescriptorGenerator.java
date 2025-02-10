package org.example.analyzers.feature.helpers;

import org.example.utils.VectorUtil;

// TODO: parametrize bins size?
public class DescriptorGenerator {
    /**
     * Generates 128D normalized descriptor vector by creating magnitudes histogram in different orientations.
     * Then concatenates 16 histograms into single 128D vector. Size of each cell is determined dynamically.
     * Descriptor is normalized, clamped and normalized again.
     *
     * @param gradients matrix of gradient values {dx,dy} of pixels around described keypoint
     *
     * @return float[128] vector containing keypoint descriptor.
     */
    public float[] constructDescriptor(float[][][] gradients) {
        float[][] magnitudes = computeKeypointLocalMagnitudes(gradients);
        float keypointOrientation = findKeypointDominantOrientation(gradients, magnitudes);
        float[][] orientations = computeKeypointOrientations(gradients, keypointOrientation);

        int gradientSize = gradients.length;
        int cellWidth = gradientSize / 4;
        float binSize = 45f; // 360 degrees / 8 bins
        int descriptorLength = 128; // 16 cells x 8 bins
        float[] descriptor = new float[descriptorLength];

        int index = 0;
        for (int cellX=0; cellX<4; cellX++) {
            for (int cellY=0; cellY<4; cellY++) {
                float[] localHistogram = new float[8];

                for (int pixelX=0; pixelX<4; pixelX++) {
                    for (int pixelY=0; pixelY<4; pixelY++) {
                        int x = cellX * cellWidth + pixelX;
                        int y = cellY * cellWidth + pixelY;

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

    /**
     * Computes magnitudes matrix for all entries in the local gradients matrix
     *
     * @param localGradients matrix containing {dx, dy} entries for each pixel
     *
     * @return matrix of gradient magnitudes
     */
    private float[][] computeKeypointLocalMagnitudes(float[][][] localGradients) {
        float[][] magnitudes = new float[localGradients.length][localGradients[0].length];

        for (int x=0; x<localGradients.length; x++) {
            for (int y=0; y<localGradients[0].length; y++) {
                magnitudes[x][y] = VectorUtil.getVectorNorm(localGradients[x][y]);
            }
        }

        return magnitudes;
    }

    /**
     * Iterates through gradients matrix, calculates magnitude of each gradient and returns orientation of the largest magnitude
     *
     * @param localGradients matrix of {dx, dy} gradients
     * @param localMagnitudes matrix of gradients magnitudes computed from localGradients
     *
     * @return dominant orientation in degrees
     */
    private float findKeypointDominantOrientation(float[][][] localGradients, float[][] localMagnitudes) {
        float maxMagnitude = 0;
        int maxX=0, maxY=0;
        for (int x=0; x<localMagnitudes.length; x++) {
            for (int y=0; y<localMagnitudes[0].length; y++) {
                float magnitude = localMagnitudes[x][y];
                if (magnitude > maxMagnitude) {
                    maxMagnitude = magnitude;
                    maxX = x;
                    maxY = y;
                }
            }
        }
        return VectorUtil.getVectorDegreesOrientation2D( localGradients[maxX][maxY] );
    }


    /**
     * Computes orientations of the entire gradients matrix and subtracts keypoint's dominant orientation from each value.
     *
     * @param localGradients matrix containing {dx,dy} values
     * @param keypointOrientation keypoint dominant orientation based on highest gradient magnitude
     *
     * @return new matrix containing orientations in degrees
     */
    private float[][] computeKeypointOrientations(float[][][] localGradients, float keypointOrientation) {
        float[][] orientations = new float[localGradients.length][localGradients[0].length];

        for (int x=0; x<localGradients.length; x++) {
            for (int y = 0; y < localGradients[0].length; y++) {
                float localOrientation = VectorUtil.getVectorDegreesOrientation2D( localGradients[x][y] );
                float orientation = localOrientation - keypointOrientation;
                if (orientation < 0) orientation += 360;
                orientations[x][y] = orientation;
            }
        }
        return orientations;
    }
}
