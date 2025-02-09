package org.example.analyzers.feature;

import org.example.utils.ImageUtil;
import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;

public class BIGaussianHelper {

    public BufferedImage[][] buildGaussianPyramid(BufferedImage image, int octavesNum, int scalesNum, double baseSigma, double sigmaInterval, int downsamplingFactor) {
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

    public BufferedImage[][] buildDoGPyramid(BufferedImage[][] gaussianPyramid) {
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
                int red = firstAccessor.getRed(x,y) - secondAccessor.getRed(x,y);
                int blue = firstAccessor.getBlue(x,y) - secondAccessor.getBlue(x,y);
                int green = firstAccessor.getGreen(x,y) - secondAccessor.getGreen(x,y);

                resultAccessor.setPixel(x, y, 0, red, green, blue);
            }
        }

        return result;
    }

    public BufferedImage[] generateGaussianScales(BufferedImage baseImage, int scalesNum, double baseSigma, double scaleInterval) {
        int numberOfScales = scalesNum + 3;
        BufferedImage[] gaussianImages = new BufferedImage[numberOfScales];
        double baseScale = baseSigma;

        for (int i=0; i<numberOfScales; i++) {
            gaussianImages[i] = ImageUtil.gaussianBlur(baseImage, baseScale);
            baseScale *= scaleInterval;
        }

        return gaussianImages;
    }
}
