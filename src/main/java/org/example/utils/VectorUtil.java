package org.example.utils;

public class VectorUtil {


    public static double[] multiplyVector(double[] vector, double value) {
        int length = vector.length;

        double[] result = new double[length];

        for (int i = 0; i < length; i++) {
                result[i] += vector[i] * value;
        }

        return result;
    }

    public static double getVectorNorm(double[] vector) {
        double squaresSum = 0;
        for (double value: vector) {
            squaresSum += value * value;
        }

        return Math.sqrt(squaresSum);
    }

    public static double getVectorDotProduct(double[] vector) {
        return getVectorDotProduct(vector, vector);
    }

    public static double getVectorDotProduct(double[] vectorA, double[] vectorB) {
        double sum = 0;
        for (int i=0; i<vectorA.length; i++) {
            sum += vectorA[i] * vectorB[i];
        }

        return sum;
    }
}
