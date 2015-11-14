package edu.buffalo.dm.clustering.model;

import java.util.*;

import edu.buffalo.dm.clustering.bean.Cluster;
import edu.buffalo.dm.clustering.bean.Gene;
import edu.buffalo.dm.clustering.util.ClusterUtil;

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

    public List<Cluster> getClusterList() {
        return new ArrayList<Cluster>(clusters.values());
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

    /**
     * Constructor for creating KMeans object
     *
     * @param dataset - the original dataset - list of genes
     * @param kCount  - the k value for teh algorithm
     */
    public KMeans(List<Gene> dataset, int kCount) {
        dataSet = dataset;
        k = kCount;
        clusters = generateInitialClusters(dataSet);
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

    /**
     * Generates a map of cluster ids to the clusters containing randomly chosen K clusters
     *
     * @param dataSet - the original dataset with all genes
     * @return - the map of cluster ids to the generated clusters
     */
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
            clusters.put(count + 1, new Cluster(clusterHeads.get(count), count + 1));
            clusters.get(count + 1).addGene(clusterHeads.get(count));
        }
        return clusters;
    }

    /**
     * This method is just for testing the values for a static list of cluster ids
     *
     * @param dataSet - ooriginal dataset
     * @return - map of clusters
     */
    private Map<Integer, Cluster> generateInitialClustersStatic(List<Gene> dataSet) {
        List<Gene> clusterHeads = new ArrayList<>();
        Map<Integer, Cluster> clusters = new HashMap<Integer, Cluster>();
        int clusterHeadCount = 0, dataSetSize = dataSet.size();
        // iyer
        //int[] sampleGenes = {1, 102, 263, 301, 344, 356, 394, 411, 474, 493};
        int[] sampleGenes = {159, 232, 13, 157};
        //int[] sampleGenes = {3, 1, 6};
        for (int gene : sampleGenes) {
            clusterHeadCount++;
            clusterHeads.add(dataSet.get(gene - 1));
        }


        for (int count = 0; count < clusterHeadCount; count++) {
            clusters.put(count + 1, new Cluster(clusterHeads.get(count).getExpressionValues(), count + 1));
            clusters.get(count + 1).addGene(clusterHeads.get(count));
        }
        return clusters;
    }

    /**
     * Assigns genes to the clusters until the clusters are stable
     */
    public void assignGenesToClustersUsingKMeans() {
        while (true) {
            // set the flag initially to false
            boolean clusterChange = false;
            for (Gene g : dataSet) {
                Cluster c = getClosestCluster(g, clusters);
                if (c.getClusterId() != g.getClusterId()) {
                    // set the flag to true if there are changes happening
                    clusterChange = true;
                    removeGeneFromCluster(g);
                    g.setClusterId(c.getClusterId());
                    c.addGene(g);
                    reCalculateCentroid(c);
                }
            }

            // stop processing once the clusters are stable
            if (clusterChange == false) {
                break;
            }
        }

    }

    /**
     * Assign genes to clusters using medoids instead of centroids
     */
    public void assignGenesToClustersUsingKMediod() {
        while (true) {
            boolean clusterChange = false;
            for (Gene g : dataSet) {
                Cluster c = getClosestClusterUsingMedoid(g, clusters);
                if (c.getClusterId() != g.getClusterId()) {
                    clusterChange = true;
                    removeGeneFromCluster(g);
                    g.setClusterId(c.getClusterId());
                    c.addGene(g);
                    reCalculateMedoid(c);
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
            reCalculateMedoid(c);
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

    /**
     * Finds the closest cluster to the point using mediods
     *
     * @param gene     - the current gene
     * @param clusters - a map of all the clusters keyed with their Ids
     * @return - the closest cluster
     */
    private Cluster getClosestClusterUsingMedoid(Gene gene, Map<Integer, Cluster> clusters) {
        double minDistance = 99999;
        Cluster closestCluster = null;
        double distance;
        for (Object c : clusters.values()) {
            Cluster cluster = (Cluster) c;
            if ((distance = ClusterUtil.getGeneDistanceMatrix().get(((Cluster) c).getMedoidGene().getGeneId()).get(gene.getGeneId())) < minDistance) {
                closestCluster = cluster;
                minDistance = distance;
            }
        }
        return closestCluster;
    }

    /**
     * Re calculates the centroid of the cluster
     * @param cluster - the cluster
     */
    private void reCalculateCentroid(Cluster cluster) {

        int geneCount = cluster.getGenes().size(), expressionValues = cluster.getCentroid().size();
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

    /**
     * Re calculates medoid of the cluster
     * @param cluster - the cluster of genes
     */
    private void reCalculateMedoid(Cluster cluster) {
        int minDistance = Integer.MAX_VALUE, avgDistanceToAllPoints, geneCount = cluster.getGenes().size();
        for (Gene currentGene : cluster.getGenes()) {
            avgDistanceToAllPoints = 0;
            for (Gene gene : cluster.getGenes()) {
                if (gene != currentGene) {
                    avgDistanceToAllPoints += ClusterUtil.getGeneDistanceMatrix().get(currentGene.getGeneId()).get(gene.getGeneId());
                }
            }
            if (geneCount > 1) {
                avgDistanceToAllPoints = avgDistanceToAllPoints / (geneCount - 1);
            }

            if (avgDistanceToAllPoints < minDistance) {
                minDistance = avgDistanceToAllPoints;
                cluster.setMedoidGene(currentGene);
            }
        }
    }

    void printMatrix(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.println();
            for (int j = 0; j < a[0].length; j++) {
                System.out.print(a[i][j] + " ");
            }
        }
    }

    /**
     * Removes the clusters with minimum amount of points i.e. Noise Points
     */
    public void postProcessing() {
        int tenPercentDatasetSize = (int) (dataSet.size() * 0.02);
        for (Cluster c : getClusterList()) {
            if (c.getGenes().size() <= tenPercentDatasetSize) {
                for (Gene g : c.getGenes()) {
                    g.setClusterId(-1);
                }
            }
        }
    }
}