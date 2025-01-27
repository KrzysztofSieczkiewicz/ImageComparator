package org.example.analyzers.feature;

import org.example.analyzers.common.PixelPoint;
import org.example.utils.ImageUtil;
import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class BISIFTAnalyzer {
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

    /**
     * Contrast threshold below which keypoint will be discarded as noise
     */
    double keypointContrastThreshold = 0.03;

    /**
     * Hessian eigenvalues ratio below which keypoint will be discarded as edge keypoint
     */
    double keypointEdgeResponseRatio = 10;


    public void constructScaleSpace(BufferedImage image) {
        BIGaussianHelper helper = new BIGaussianHelper();

        // 0. Greyscale the image
        BufferedImage greyscaleImage = ImageUtil.greyscale(image);

        // 1. Octaves
        int octavesAmount = calculateOctavesNum(greyscaleImage, minImageSizeThreshold, downsamplingFactor);

        // 2. Scale intervals
        double sigmaInterval = calculateScaleIntervals(scalesAmount);

        // 3. Build Gaussian Pyramid
        BufferedImage[][] gaussianPyramid = helper.buildGaussianPyramid(greyscaleImage, octavesAmount, scalesAmount, baseSigma, sigmaInterval, downsamplingFactor);

        // 4. Build DoG pyramid
        BufferedImage[][] dogPyramid = helper.buildDoGPyramid(gaussianPyramid);

        // 5. Find keypoints in the DoG pyramid


    }

    public int calculateOctavesNum(BufferedImage image, int minSizeThreshold, int downsamplingFactor) {
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


    private void detectKeypoints(BufferedImage[][] dogPyramid) {
        int octavesNum = dogPyramid.length;
        int scalesNum = dogPyramid[0].length;

        for (int octave=0; octave<octavesNum; octave++) {

            for (int scale=1; scale<scalesNum-1; scale++) {
                BufferedImage currentScaleImage = dogPyramid[octave][scale];

                // 0. find potential keypoints
                ArrayList<PixelPoint> potentialCandidates = findPotentialKeypoints( dogPyramid[octave], scale );

                // filter potential keypoints by checking contrast and edge response
                ArrayList<KeypointCandidate> keypointCandidates = potentialCandidates.stream()
                        .map(potentialCandidate -> new KeypointCandidate(currentScaleImage, potentialCandidate))
                        .filter(candidate ->
                                !candidate.isLowContrast(keypointContrastThreshold) &&
                                !candidate.isEdgeResponse(keypointEdgeResponseRatio))
                        .collect(Collectors.toCollection(ArrayList::new));

                // refine candidates into full keypoints
//                ArrayList<Keypoint> keypoints = keypointCandidates.stream()
//                        .map(candidate -> candidate.refineCandidate(octave, scale))
//                        .collect(Collectors.toCollection(ArrayList::new));

                // TODO [CURRENT]: extend Keypoint class

                // TODO: wymaga drobnej zmiany -> po wyszukaniu "ciekawych" pixeli zapisujemy je jako listę potencjalnych pixeli
                //  po filtrowaniu -> zapisujmy je jako listę hessianPoint - kontrast i krawędzie mogą być sprawdzane macierzą Hessego 2x2 (w ramach jednej skali)
                //  do refinowania kandydatów potrzebna jest macierz Hessego 3x3 (masz już część w HessianPoint)
                //  Podsumowując:
                //  1. ArrayList<PixelPoint> potentialCandidates = ...
                //  2. ArrayList<KeypointCandidate> keypointCandidates = potantialCandidates...
                //  3. ArrayList<Keypoint> keypoints = keypointCandidates...
                // TODO: zmień nazwę HessianPoint na KeypointCandidate, rozważ dodanie indeksu skali i indeksu oktawy do zmiennych wewn.

                // 3. calculate exact position of keypoint (subpixel coordinates)

                // 4. at this point candidates should be ready to make into full Keypoints, but only after:
                //  a. assigning orientation to each Keypoint (compute the Gradient Magnitude and Orientation)
                //  b. create orientation histogram
                //  c. decide on dominant orientation

                // 5. Generate normalized descriptors for each keypoint
                // 6. Use descriptor distances and RANSAC to match keypoints across different images
            }

        }
    }

    private ArrayList<PixelPoint> findPotentialKeypoints(BufferedImage[] octave, int scaleIndex) {
        ArrayList<PixelPoint> keypointCandidates = new ArrayList<>();

        ImageAccessor currentAccessor = ImageAccessor.create(octave[scaleIndex-1]);
        ImageAccessor previousAccessor = ImageAccessor.create(octave[scaleIndex]);
        ImageAccessor nextAccessor = ImageAccessor.create(octave[scaleIndex+1]);

        int rows = currentAccessor.getWidth();
        int cols = currentAccessor.getHeight();
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
