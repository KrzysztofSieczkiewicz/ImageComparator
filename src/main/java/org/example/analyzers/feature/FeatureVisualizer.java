package org.example.analyzers.feature;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class FeatureVisualizer {

    public BufferedImage drawKeypoints(BufferedImage img, List<Keypoint> keypoints) {
        BufferedImage output = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = output.createGraphics();

        // Draw original image
        g.drawImage(img, 0, 0, null);

        // Set color for keypoints
        g.setColor(Color.BLUE);

        for (Keypoint kp : keypoints) {
            int x = (int) kp.getSubPixelX() * (int) Math.pow(2, kp.getOctaveIndex());
            int y = (int) kp.getSubPixelY() * (int) Math.pow(2, kp.getOctaveIndex());
            int octave = kp.getOctaveIndex(); // Get octave level
            int radius = 3 * (octave + 1); // Increase radius based on octave

            // Draw circle representing the keypoint
            g.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);
            g.fillOval(x - 2, y - 2, 4, 4); // Draw center dot
        }

        g.dispose();
        return output;
    }

    public BufferedImage drawMatches(BufferedImage img1, BufferedImage img2,
                                     List<Keypoint> keypoints1, List<Keypoint> keypoints2,
                                     List<FeatureMatch> matches) {
        int width = img1.getWidth() + img2.getWidth();
        int height = Math.max(img1.getHeight(), img2.getHeight());

        // Calculate scale ratio between the images
        double scaleX = (double) img2.getWidth() / img1.getWidth();
        double scaleY = (double) img2.getHeight() / img1.getHeight();

        // Create a combined image
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = output.createGraphics();

        // Draw images side by side
        g.drawImage(img1, 0, 0, null);
        g.drawImage(img2, img1.getWidth(), 0, null);

        // Draw matches
        for (FeatureMatch match : matches) {
            Keypoint kp1 = match.getKeypoint1();
            Keypoint kp2 = match.getKeypoint2();
            double distance = match.getDistance();

            int x1 = (int) kp1.getSubPixelX() * (int) Math.pow(2, kp1.getOctaveIndex());
            int y1 = (int) kp1.getSubPixelY() * (int) Math.pow(2, kp1.getOctaveIndex());

            // Scale keypoint positions in the second image
            int x2 = (int) kp2.getSubPixelX() * (int) Math.pow(2, kp2.getOctaveIndex()) + img1.getWidth();
            int y2 = (int) kp2.getSubPixelY() * (int) Math.pow(2, kp2.getOctaveIndex());

            // Set color based on distance
            if (distance < 150.0) {
                g.setColor(Color.GREEN);  // Good match
            } else {
                g.setColor(Color.RED);  // Bad match
            }

            if (distance > 150.0) System.out.println(distance);

            g.fillOval(x1 - 3, y1 - 3, 6, 6);  // Draw keypoint 1
            g.fillOval(x2 - 3, y2 - 3, 6, 6);  // Draw keypoint 2
            g.drawLine(x1, y1, x2, y2);        // Draw line connecting matches
        }

        g.dispose();
        return output;
    }

}
