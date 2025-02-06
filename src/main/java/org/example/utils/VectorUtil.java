package org.example.utils;

public class VectorUtil {


    public static float[] multiplyVector(float[] vector, float value) {
        int length = vector.length;

        float[] result = new float[length];

        for (int i = 0; i < length; i++) {
                result[i] += vector[i] * value;
        }

        return result;
    }

    public static float getVectorNorm(float[] vector) {
        float squaresSum = 0;
        for (float value: vector) {
            squaresSum += value * value;
        }

        return (float) Math.sqrt(squaresSum);
    }

    public static float getVectorDegreesOrientation2D(float[] vector) {
        return (float) Math.toDegrees( Math.atan2(vector[0], vector[1]) );
    }

    public static float getVectorDotProduct(float[] vector) {
        return getVectorDotProduct(vector, vector);
    }

    public static float getVectorDotProduct(float[] vectorA, float[] vectorB) {
        float sum = 0;
        for (int i=0; i<vectorA.length; i++) {
            sum += vectorA[i] * vectorB[i];
        }

        return sum;
    }

    /**
     * Performs L2 normalization on provided vector. Returns new instance
     */
    public static float[] normalizeL2(float[] vector) {
        float[] normalizedVector = new float[vector.length];

        float sum = 0;
        for (float value: vector) {
            sum += value*value;
        }

        if (sum == 0) return vector;

        for (int i = 0; i < vector.length; i++) {
            normalizedVector[i] = vector[i] / sum;
        }

        return normalizedVector;
    }

    /**
     * Limits vector contents to provided min/max values
     * @return new clipped vector
     */
    public static float[] clip(float[] vector, float min, float max) {
        float[] clippedVector = new float[vector.length];

        for (int i=0; i<vector.length; i++) {
            float value = vector[i];
            if (value > max) {
                clippedVector[i] = max;
            } else clippedVector[i] = Math.max(value, min);
        }

        return clippedVector;
    }
}
