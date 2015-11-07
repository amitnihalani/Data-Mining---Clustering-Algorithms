package com.company.k_means;

import com.company.beans.Cluster;
import com.company.beans.Gene;

import java.util.*;

/**
 * Created by Amit on 11/6/2015.
 */
public class KMeans {

    public Map<Integer, Cluster> clusters;
    public List<Gene> dataSet;

    public KMeans(List<Gene> dataset) {
        dataSet = dataset;
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
            distance += Math.pow(expressionValueSet1.get(i) - expressionValueSet2.get(i), (double)2);
        }
        return Math.sqrt(distance);
    }

    private Map<Integer, Cluster> generateInitialClusters(List<Gene> dataSet) {
        List<Gene> clusterHeads = new ArrayList<>();
        Map<Integer, Cluster> clusters = new HashMap<Integer, Cluster>();
        int clusterHeadCount = 1, dataSetSize = dataSet.size();
        while (clusterHeadCount <= 10) {
            Gene g = dataSet.get(new Random().nextInt(dataSetSize));

            if (!clusterHeads.contains(g)) {
                clusterHeads.add(g);
                clusterHeadCount++;
            }
        }

        clusterHeadCount--;
        for (Gene g : clusterHeads ) {
            clusters.put(clusterHeadCount, new Cluster(g.getExpressionValues(), clusterHeadCount--));

            if(clusterHeadCount<=0){
                break;
            }
        }
        return clusters;
    }

    public void assignGenesToClusters() {

        while(true) {
            boolean clusterChange = false;
            for (Gene g : dataSet) {
                Cluster c = getClosestCluster(g, clusters);
                if(c.getClusterId() != g.getClusterId()) {
                    clusterChange = true;
                    removeGeneFromCluster(g);
                    c.addGene(g);
                }
            }

            reCalculateCentroid(clusters);

            if(clusterChange==true) {
                break;
            }
        }

    }

    /**
     * Removes the gene from its cluster
     * @param gene - the gene to be deleted
     */
    public void removeGeneFromCluster(Gene gene) {
        Cluster c;
        if((c = clusters.get(gene.getClusterId())) != null) {
            c.getGenes().remove(gene);
        }
    }

    /**
     * Finds the closest cluster to the point
     * @param gene - the current gene
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
        return  closestCluster;
    }

    private void reCalculateCentroid(Map<Integer, Cluster> clusters) {
        for(Cluster c: clusters.values()) {
            int geneCount = c.getGenes().size(), expressionValues = c.getGenes().get(0).getExpressionValues().size();

            List<Double> centroid = new ArrayList<>(expressionValues);
            for(int i=0; i<expressionValues; i++) {
                double expValue = 0.0;
                for(Gene g : c.getGenes()) {
                    expValue += g.getExpressionValues().get(i);
                }
                centroid.add(expValue/geneCount);
            }
            c.setCentroid(centroid);
        }
    }
}
