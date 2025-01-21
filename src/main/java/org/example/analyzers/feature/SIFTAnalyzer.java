package org.example.analyzers.feature;

import org.example.analyzers.common.LocalExtremes;
import org.example.analyzers.common.PixelPoint;
import org.example.utils.ImageUtil;
import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class SIFTAnalyzer {
    // TODO - CURRENT: test if DoG is handling edge cases and if there are aliasing issues with image downscaling

    // TODO: can be memory optimized by merging buildGaussianPyramid with buildDoGPyramid
    //  that'd work by discarding each gaussian images after necessary dog is computed

    /**
     * When to stop creating octaves
     */
    int minImageSizeThreshold = 16;

    /**
     * How many scales should be generated per one octave
     */
    int scalesAmount = 3;

    /**
     * Base sigma value determining initial image blur
     */
    double baseSigma = 1.6;

    /**
     * Downsampling factor by which the image is reduced between octaves
     */
    int downsamplingFactor = 2;


    private void constructScaleSpace(BufferedImage image) {
        // 0. Greyscale the image
        BufferedImage greyscaleImage = ImageUtil.greyscale(image);

        // 1. Octaves
        int octavesAmount = calculateOctavesNum(greyscaleImage, minImageSizeThreshold, downsamplingFactor);

        // 2. Scale intervals
        double sigmaInterval = calculateScaleIntervals(scalesAmount);

        // 3. Build Gaussian Pyramid
        BufferedImage[][] gaussianPyramid = buildGaussianPyramid(greyscaleImage, octavesAmount, scalesAmount, baseSigma, sigmaInterval, downsamplingFactor);

        // 4. Build DoG pyramid
        BufferedImage[][] dogPyramid = buildDoGPyramid(gaussianPyramid);

        // 5. Find keypoints in the DoG pyramid


    }

    private int calculateOctavesNum(BufferedImage image, int minSizeThreshold, int downsamplingFactor) {
        int currWidth = image.getWidth();
        int currHeight = image.getHeight();

        int octaves = 0;
        while((currWidth/downsamplingFactor >= minSizeThreshold) && (currHeight/downsamplingFactor >= minSizeThreshold)) {
            octaves++;
            currWidth /= downsamplingFactor;
            currHeight /= downsamplingFactor;
        }

        return octaves;
    }

    private double calculateScaleIntervals(int scalesAmount) {
        double p = 1d/scalesAmount;
        return Math.pow(2, p);
    }

    private BufferedImage[][] buildGaussianPyramid(BufferedImage image, int octavesNum, int scalesNum, double baseSigma, double sigmaInterval, int downsamplingFactor) {
        BufferedImage[][] pyramid = new BufferedImage[octavesNum][];


        for (int octave=0; octave<octavesNum; octave++) {
            pyramid[octave] = generateGaussianScales(image, scalesNum, baseSigma, sigmaInterval);

            image = ImageUtil.resize(
                    image,
                    image.getWidth()/downsamplingFactor,
                    image.getHeight()/downsamplingFactor );
        }

        return pyramid;
    }

    private BufferedImage[][] buildDoGPyramid(BufferedImage[][] gaussianPyramid) {
        int octavesNum = gaussianPyramid.length;
        int scalesNum = gaussianPyramid[0].length-1;
        BufferedImage[][] pyramid = new BufferedImage[octavesNum][scalesNum];

        for (int octave=0; octave<octavesNum; octave++) {
            for (int scale=0; scale<scalesNum; scale++) {
                pyramid[octave][scale] = calculateDoG(
                        gaussianPyramid[octave][scale+1],
                        gaussianPyramid[octave][scale] );
            }
        }

        return pyramid;
    }

    public BufferedImage calculateDoG(BufferedImage first, BufferedImage second) {
        int width = first.getWidth();
        int height = first.getHeight();
        BufferedImage result = new BufferedImage(width, height, first.getType());

        ImageAccessor firstAccessor = ImageAccessor.create(first);
        ImageAccessor secondAccessor = ImageAccessor.create(second);
        ImageAccessor resultAccessor = ImageAccessor.create(result);

        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                int red = Math.max( 0, Math.min(firstAccessor.getRed(x,y), secondAccessor.getRed(x,y)) );
                int blue = Math.max( 0, Math.min(firstAccessor.getBlue(x,y), secondAccessor.getBlue(x,y)) );
                int green = Math.max( 0, Math.min(firstAccessor.getGreen(x,y), secondAccessor.getGreen(x,y)) );

                resultAccessor.setPixel(x, y, 0, red, green, blue);
            }
        }

        return result;
    }

    private BufferedImage[] generateGaussianScales(BufferedImage baseImage, int scalesNum, double baseSigma, double scaleInterval) {
        int numberOfScales = scalesNum + 3;
        BufferedImage[] gaussianImages = new BufferedImage[numberOfScales];
        double baseScale = baseSigma;

        for (int i=0; i<numberOfScales; i++) {
            gaussianImages[i] = ImageUtil.gaussianBlur(baseImage, baseScale);
            baseScale *= scaleInterval;
        }

        return gaussianImages;
    }


    private void detectKeypoints(BufferedImage[][] dogPyramid) {
        int octavesNum = dogPyramid.length;
        int scalesNum = dogPyramid[0].length;

        for (int octave=0; octave<octavesNum; octave++) {

            for (int scale=1; scale<scalesNum-1; scale++) {
                LocalExtremes candidates = findLocalExtremes( dogPyramid[octave][scale] );

                LocalExtremes currentExtremes = findLocalExtremes(dogPyramid[octave][scale]);
            }

        }
    }

    // TODO: better to split maxima and minima to separate calls? (maybe add a boolean arg to determine which should be found)?
    /**
     * Finds local extremes in the provided greyscaled image. Searches only most direct neighbour.
     *
     * @param image to be searched through
     * @return LocalExtremes class containing minima and maxima
     */
    private LocalExtremes findLocalExtremes(BufferedImage image) {
        ImageAccessor accessor = ImageAccessor.create(image);

        LocalExtremes extremes = new LocalExtremes();
        int rows = image.getWidth();
        int cols = image.getHeight();
        int[] dRow = {-1, 1, 0, 0, -1, -1, 1, 1};
        int[] dCol = {0, 0, -1, 1, -1, 1, -1, 1};

        for (int x=1; x<rows; x++) {
            for (int y=1; y<cols; y++) {
                boolean isMinimum = true;
                boolean isMaximum = true;
                int value = accessor.getBlue(x, y);

                for (int k=0; k<dRow.length; k++) {
                    int currRow = x + dRow[k];
                    int currCol = y + dCol[k];
                    int currentValue = accessor.getBlue(currRow, currCol);

                    if (value >= currentValue) isMinimum = false;
                    if (value <= currentValue) isMaximum = false;
                    if (!isMinimum && !isMaximum) break;
                }

                if (isMinimum) extremes.addToMinima(x,y);
                if (isMaximum) extremes.addToMaxima(x,y);
            }
        }
        return extremes;
    }

    // Basic
    private ArrayList<PixelPoint> findKeypointCandidates(BufferedImage current, BufferedImage previous, BufferedImage next) {
        ArrayList<PixelPoint> keypointCandidates = new ArrayList<>();

        ImageAccessor currentAccessor = ImageAccessor.create(current);
        ImageAccessor previousAccessor = ImageAccessor.create(previous);
        ImageAccessor nextAccessor = ImageAccessor.create(next);

        int rows = current.getWidth();
        int cols = current.getHeight();
        int[] dRow = {-1, 1, 0, 0, -1, -1, 1, 1};
        int[] dCol = {0, 0, -1, 1, -1, 1, -1, 1};

        for (int row=1; row<rows-1; row++) {
            for (int col=1; col<cols-1; col++) {
                int currentPixel = currentAccessor.getBlue(row,col);
                boolean isMinimum = true;
                boolean isMaximum = true;

                for (int k=0; k<dRow.length; k++) {
                    int currRow = row + dRow[k];
                    int currCol = col + dCol[k];

                    // compare with current scale
                    int neighbourValue = currentAccessor.getBlue(currRow, currCol);
                    if (currentPixel >= neighbourValue) isMinimum = false;
                    if (currentPixel <= neighbourValue) isMaximum = false;

                    // compare with previous scale
                    neighbourValue = previousAccessor.getBlue(currRow, currCol);
                    if (currentPixel >= neighbourValue) isMinimum = false;
                    if (currentPixel <= neighbourValue) isMaximum = false;

                    // compare with next scale
                    neighbourValue = nextAccessor.getBlue(currRow, currCol);
                    if (currentPixel >= neighbourValue) isMinimum = false;
                    if (currentPixel <= neighbourValue) isMaximum = false;

                    // early exit
                    if (!isMinimum && !isMaximum) break;
                }

                if (isMaximum || isMinimum) {
                    keypointCandidates.add(new PixelPoint(row, col));
                }
            }
        }

        return keypointCandidates;
    }

    // Smort
    private ArrayList<PixelPoint> findKeypointCandidatesButSmarter(BufferedImage current, BufferedImage previous, BufferedImage next) {
        ArrayList<PixelPoint> keypointCandidates = new ArrayList<>();

        ImageAccessor currentAccessor = ImageAccessor.create(current);
        ImageAccessor previousAccessor = ImageAccessor.create(previous);
        ImageAccessor nextAccessor = ImageAccessor.create(next);

        int rows = current.getWidth();
        int cols = current.getHeight();

        ArrayList<PixelPoint> baseCandidates = new ArrayList<>();
        {   // Find all keypoints in the base image
            int[] dRow = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] dCol = {-1, 0, 1, -1, 1, -1, 0, 1};

            for (int row=1; row<rows-1; row++) {
                for (int col = 1; col < cols - 1; col++) {
                    int currentPixel = currentAccessor.getBlue(row, col);
                    boolean isMinimum = true;
                    boolean isMaximum = true;

                    for (int k=0; k<dRow.length; k++) {
                        int neighbourValue = currentAccessor.getBlue(
                                row + dRow[k],
                                col + dCol[k] );

                        if (currentPixel >= neighbourValue) isMinimum = false;
                        if (currentPixel <= neighbourValue) isMaximum = false;

                        if (!isMinimum && !isMaximum) break;
                    }

                    if (isMaximum || isMinimum) {
                        baseCandidates.add(new PixelPoint(row, col));
                    }
                }
            }
        }

        {   // Filter baseCandidates through previous and next images
            int[] dRow = {-1, -1, -1, 0, 0, 0, 1, 1, 1};
            int[] dCol = {-1, 0, 1, -1, 0, 1, -1, 0, 1};

            for( PixelPoint candidate : baseCandidates) {
                int row = candidate.getX();
                int col = candidate.getY();
                int currentPixel = currentAccessor.getBlue(row, col);

                boolean isMinimum = true;
                boolean isMaximum = true;

                for (int k=0; k<dRow.length; k++) {
                    int previousValue = previousAccessor.getBlue(
                            row + dRow[k],
                            col + dCol[k] );
                    if (currentPixel >= previousValue) isMinimum = false;
                    if (currentPixel <= previousValue) isMaximum = false;

                    int nextValue = nextAccessor.getBlue(
                            row + dRow[k],
                            col + dCol[k] );
                    if (currentPixel >= nextValue) isMinimum = false;
                    if (currentPixel <= nextValue) isMaximum = false;

                    if (!isMinimum && !isMaximum) break;
                }

                if (isMaximum || isMinimum) {
                    keypointCandidates.add(new PixelPoint(row, col));
                }
            }
        }

        return keypointCandidates;
    }

}
