package com.company.k_means;

/**
 * Created by Amit on 11/10/2015.
 */
public class ExternalIndex {


    double jaccardCoefficient;
    double randIndex;
    KMeans kMeans;

    public double getJaccardCoefficient() {
        return jaccardCoefficient;
    }

    public void setJaccardCoefficient(double jaccardCoefficient) {
        this.jaccardCoefficient = jaccardCoefficient;
    }

    public double getRandIndex() {
        return randIndex;
    }

    public void setRandIndex(double randIndex) {
        this.randIndex = randIndex;
    }

    public KMeans getkMeans() {
        return kMeans;
    }

    public void setkMeans(KMeans kMeans) {
        this.kMeans = kMeans;
    }

    public ExternalIndex(KMeans kmeans) {
        kMeans = kmeans;
        calculateCoefficients(kmeans.getGroundTruthMatrix(), kmeans.getDatasetMatrix());
    }

    private void calculateCoefficients(int[][] gTruth, int[][] dataset) {
        int ss = 0, sd = 0, ds = 0, dd = 0;
        for (int row = 0; row < gTruth.length; row++) {
            for (int col = 0; col < gTruth.length; col++) {
                String matrixValue = Integer.toString(gTruth[row][col]) + Integer.toString(dataset[row][col]);
                switch (matrixValue) {
                    case "11":
                        ss++;
                        break;
                    case "10":
                        sd++;
                        break;
                    case "01":
                        ds++;
                        break;
                    case "00":
                        dd++;
                        break;
                }
            }
        }
        jaccardCoefficient = getJaccardCoefficient((ss), (sd), (ds));
    }



    private double getJaccardCoefficient(double ss, double sd, double ds) {
        return ss/ (ss+sd+ds);
    }

}

