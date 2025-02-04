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
}
