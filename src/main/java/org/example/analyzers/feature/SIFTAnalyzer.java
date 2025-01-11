package org.example.analyzers.feature;

import org.example.utils.ImageUtil;
import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;

public class SIFTAnalyzer {

    /**
     * When to stop creating octaves
     */
    int minImageSizeThreshold = 16;



    private void constructScaleSpace(BufferedImage image) {
        // 1. Multiple Gaussian blur
        double sigma1 = 1.0;
        double sigma2 = 1.41;

        BufferedImage blurred1 = ImageUtil.gaussianBlur(image, sigma1);
        BufferedImage blurred2 = ImageUtil.gaussianBlur(image, sigma2);

        // 2. Difference of Gaussian
        BufferedImage dog = generateDoG(blurred1, blurred2);

        // 3. Octaves
        int octavesNo = calculateOctavesNum(image);

        // 4. DoG Pyramid

    }
        private int calculateOctavesNum(BufferedImage image) {
        int currWidth = image.getWidth();
        int currHeight = image.getHeight();

        int octaves = 0;
        while((currWidth/2 >= minImageSizeThreshold) && (currHeight/2 >= minImageSizeThreshold)) {
            octaves++;
            currWidth /= 2;
            currHeight /= 2;
        }

        return octaves;
    }

    private BufferedImage[][] generateDoGPyramid(BufferedImage baseImage, int octavesNum, int scalesNum, float sigma) {
        BufferedImage image = ImageUtil.greyscale(baseImage);
        BufferedImage[][] pyramid = new BufferedImage[octavesNum][];

        for (int octave=0; octave<octavesNum; octave++) {
            BufferedImage[] gaussianImages = generateGaussianImages(image, scalesNum+3, sigma);

            BufferedImage[] differenceImages = new BufferedImage[scalesNum+2];
            for (int scale=0; scale<scalesNum+2; scale++) {
                differenceImages[scale] = generateDoG(
                        gaussianImages[scale+1],
                        gaussianImages[scale] );
            }

            pyramid[octave] = differenceImages;

            image = ImageUtil.resize(
                    image,
                    image.getWidth()/2,
                    image.getHeight()/2 );
        }


        return pyramid;
    }

    public BufferedImage generateDoG(BufferedImage first, BufferedImage second) {
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

    private BufferedImage[] generateGaussianImages(BufferedImage baseImage, int scalesNum, float baseSigma) {
        BufferedImage[] gaussianImages = new BufferedImage[scalesNum];
        float currentSigma = baseSigma;

        for (int i=0; i<scalesNum; i++) {
            gaussianImages[i] = ImageUtil.gaussianBlur(baseImage, currentSigma);
            currentSigma *= 1.4142f;
        }

        return gaussianImages;
    }
}
