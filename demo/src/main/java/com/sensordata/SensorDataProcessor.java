package com.sensordata;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class SensorDataProcessor {

    // Senson data and limits.
    public double[][][] data;
    public double[][] limit;

    // constructor
    public SensorDataProcessor(double[][][] data, double[][] limit) {
        this.data = data;
        this.limit = limit;
    }

    // calculates average of sensor data
    private double average(double[] array) {
        int i = 0;
        double val = 0;
        for (i = 0; i < array.length; i++) {
            val += array[i];
        }

        return val / array.length;
    }

    // calculate data
    public void calculate(double d) {

        long startTime = System.nanoTime();

        int i, j, k = 0;
        double[][][] data2 = new double[data.length][data[0].length][data[0][0].length];

        BufferedWriter out;

        // Write racing stats data into a file
        try {
            out = new BufferedWriter(new FileWriter("RacingStatsData.txt"));

            int iLength = data.length;
            int jLength = iLength > 0 ? data[0].length : 0;
            int kLength = jLength > 0 ? data[0][0].length : 0;

            for (i = 0; i < iLength; i++) {
                for (j = 0; j < jLength; j++) {
                    double limitPow = limit[i][j] * limit[i][j];
                    double[] data_ij = data[i][j];
                    double[] data2_ij = data2[i][j];
                    double avg_data_ij = average(data_ij);

                    double sum_data2_ij = 0.0;
                    boolean condition = (i + 1) * (j + 1) > 0;

                    for (k = 0; k < kLength; k++) {
                        double dij = data_ij[k];
                        double val = dij / d - limitPow;
                        data2_ij[k] = val;

                        sum_data2_ij += val;
                        double avg_data2_ij = sum_data2_ij / kLength;

                        if (avg_data2_ij > 10 && avg_data2_ij < 50) {
                            break;
                        } else if (val > dij) {
                            break;
                        } else if (Math.abs(dij) < Math.abs(val)
                                && avg_data_ij < val && condition) {
                            data2_ij[k] = val * 2;
                            sum_data2_ij += val;
                        }
                    }
                }
            }

            for (i = 0; i < data2.length; i++) {
                for (j = 0; j < data2[0].length; j++) {
                    out.write(data2[i][j] + "\t");
                }
            }

            out.close();

            long endTime = System.nanoTime();
            long elapsedMs = (endTime - startTime) / 1_000_000;
            System.out.println("calculate() completed in " + elapsedMs + " ms");

        } catch (Exception e) {
            System.out.println("Error= " + e);
            long endTime = System.nanoTime();
            long elapsedMs = (endTime - startTime) / 1_000_000;
            System.out.println("calculate() failed after " + elapsedMs + " ms");
        }
    }

}