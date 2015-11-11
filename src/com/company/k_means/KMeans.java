package com.company.k_means;

import com.company.beans.Cluster;
import com.company.beans.Gene;

import java.util.*;

/**
 * Created by Amit on 11/6/2015.
 */
public class KMeans {
    private Map<Integer, Cluster> clusters;
    private List<Gene> dataSet;
    private int k;

    int[][] datasetMatrix, groundTruthMatrix;

    public Map<Integer, Cluster> getClusters() {
        return clusters;
    }

    public void setClusters(Map<Integer, Cluster> clusters) {
        this.clusters = clusters;
    }

    public List<Gene> getDataSet() {
        return dataSet;
    }

    public void setDataSet(List<Gene> dataSet) {
        this.dataSet = dataSet;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public int[][] getDatasetMatrix() {
        return datasetMatrix;
    }

    public void setDatasetMatrix(int[][] datasetMatrix) {
        this.datasetMatrix = datasetMatrix;
    }

    public int[][] getGroundTruthMatrix() {
        return groundTruthMatrix;
    }

    public void setGroundTruthMatrix(int[][] groundTruthMatrix) {
        this.groundTruthMatrix = groundTruthMatrix;
    }


    public KMeans(List<Gene> dataset, int kCount) {
        dataSet = dataset;
        k = kCount;
        clusters = generateInitialClustersStatic(dataSet);
        assignGenesToClusters();
        datasetMatrix = new int[dataset.size()][dataset.size()];
        groundTruthMatrix = new int[dataset.size()][dataset.size()];
        generateMatrices();
        printMatrix(datasetMatrix);
    }

    /**
     * Returns distance between two genes
     *
     * @param expressionValueSet1 -
     * @param expressionValueSet2 -
     * @return the distance between two genes
     */
    public static double getDistanceBetweenPoints(List<Double> expressionValueSet1, List<Double> expressionValueSet2) {
        double distance = 0;
        for (int i = 0; i < expressionValueSet1.size(); i++) {
            distance += Math.pow(expressionValueSet1.get(i) - expressionValueSet2.get(i), (double) 2);
        }
        return Math.sqrt(distance);
    }

    private Map<Integer, Cluster> generateInitialClusters(List<Gene> dataSet) {
        List<Gene> clusterHeads = new ArrayList<>();
        Map<Integer, Cluster> clusters = new HashMap<Integer, Cluster>();
        int clusterHeadCount = 1, dataSetSize = dataSet.size();
        while (clusterHeadCount <= k) {
            Gene g = dataSet.get(new Random().nextInt(dataSetSize));

            if (!clusterHeads.contains(g)) {
                clusterHeads.add(g);
                clusterHeadCount++;
            }
        }

        clusterHeadCount--;
        for (int count = 0; count < clusterHeadCount; count++) {
            clusters.put(count + 1, new Cluster(clusterHeads.get(count).getExpressionValues(), count + 1));
        }
        return clusters;
    }

    // REMOVE THIS: JUST FOR TEST
    //
    //
    //
    private Map<Integer, Cluster> generateInitialClustersStatic(List<Gene> dataSet) {
        List<Gene> clusterHeads = new ArrayList<>();
        Map<Integer, Cluster> clusters = new HashMap<Integer, Cluster>();
        int clusterHeadCount = 0, dataSetSize = dataSet.size();
        // iyer
        //int[] sampleGenes = {1, 102, 263, 301, 344, 356, 394, 411, 474, 493};
        int[] sampleGenes = {159,232,13,157,176};

        for (int gene : sampleGenes) {
            clusterHeadCount++;
            clusterHeads.add(dataSet.get(gene - 1));
        }


        for (int count = 0; count < clusterHeadCount; count++) {
            clusters.put(count + 1, new Cluster(clusterHeads.get(count).getExpressionValues(), count + 1));
        }
        return clusters;
    }

    public void assignGenesToClusters() {

        while (true) {
            boolean clusterChange = false;
            for (Gene g : dataSet) {
                Cluster c = getClosestCluster(g, clusters);
                if (c.getClusterId() != g.getClusterId()) {
                    clusterChange = true;
                    removeGeneFromCluster(g);
                    g.setClusterId(c.getClusterId());
                    c.addGene(g);
                    reCalculateCentroid(c);
                }
            }

            if (clusterChange == false) {
                break;
            }
        }

    }

    /**
     * Removes the gene from its cluster
     *
     * @param gene - the gene to be deleted
     */
    public void removeGeneFromCluster(Gene gene) {
        Cluster c;
        if ((c = clusters.get(gene.getClusterId())) != null) {
            c.getGenes().remove(gene);
            gene.setClusterId(-99);
            reCalculateCentroid(c);
        }
    }

    /**
     * Finds the closest cluster to the point
     *
     * @param gene     - the current gene
     * @param clusters - a map of all the clusters keyed with their Ids
     * @return - the closest cluster
     */
    private Cluster getClosestCluster(Gene gene, Map<Integer, Cluster> clusters) {
        double minDistance = 99999;
        Cluster closestCluster = null;
        double distance;
        for (Object c : clusters.values()) {
            Cluster cluster = (Cluster) c;
            if ((distance = getDistanceBetweenPoints(gene.getExpressionValues(), cluster.getCentroid())) < minDistance) {
                closestCluster = cluster;
                minDistance = distance;
            }
        }
        return closestCluster;
    }

    private void reCalculateCentroid(Cluster cluster) {

        int geneCount = cluster.getGenes().size(), expressionValues = cluster.getGenes().get(0).getExpressionValues().size();
        List<Double> centroid = new ArrayList<>(expressionValues);
        for (int i = 0; i < expressionValues; i++) {
            double expValue = 0.0;
            for (Gene g : cluster.getGenes()) {
                expValue += g.getExpressionValues().get(i);
            }
            centroid.add(expValue / geneCount);
        }
        cluster.setCentroid(centroid);
    }

    private void generateMatrices() {
        for (int row = 0; row < dataSet.size(); row++) {
            int rowClusterId = dataSet.get(row).getClusterId();
            int rowGroundTruthClusterId = dataSet.get(row).getGroundTruth();
            for (int col = 0; col < dataSet.size(); col++) {
                int colClusterId = dataSet.get(col).getClusterId();
                int colGroundTruthClusterId = dataSet.get(col).getGroundTruth();

                if (rowClusterId == colClusterId) {
                    datasetMatrix[dataSet.get(row).getGeneId()-1][dataSet.get(col).getGeneId()-1] = 1;
                }

                if (rowGroundTruthClusterId == colGroundTruthClusterId) {
                    groundTruthMatrix[dataSet.get(row).getGeneId()-1][dataSet.get(col).getGeneId()-1] = 1;
                }
            }
        }
    }

    void printMatrix(int[][]a) {
        for(int i=0; i<a.length; i++) {
            System.out.println();
            for(int j=0; j<a[0].length; j++) {
                System.out.print(a[i][j] + " ");
            }
        }
    }
}
