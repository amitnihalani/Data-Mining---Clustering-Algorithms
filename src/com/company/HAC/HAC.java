package com.company.HAC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.company.beans.Cluster;
import com.company.beans.Gene;

/**
 * This class is responsible for ru
 * 
 * @author hharwani
 *
 */
public class HAC {

    List<Cluster> currentClusters = null;
    List<Gene> genes = null;
    public Map<Integer, Cluster> clusterMap = null;
    List<FileObject> fileObjectList = new ArrayList<FileObject>();

    public HAC(List<Gene> dataSet) {
        this.genes = dataSet;
    }

    public void generateInitialClusters() {
        int cId = 0;
        currentClusters = new ArrayList<Cluster>();
        clusterMap = new HashMap<Integer, Cluster>();
        for (Gene gene : this.genes) {
            gene.setClusterId(cId);
            List<Gene> genes = new ArrayList<Gene>();
            genes.add(gene);
            Cluster cluster = new Cluster(cId, genes);
            currentClusters.add(cluster);
            clusterMap.put(cId, cluster);
            cId += 1;
        }
    }

    public Cluster assignGenesToCluster() {
        generateInitialClusters();
        while (currentClusters.size() > 1) {
            ClusterPair closestClusters = findClosestClusters(
                    currentClusters);
            fileObjectList.add(new FileObject(
                    closestClusters.first.getClusterId(),
                    closestClusters.second.getClusterId(),
                    closestClusters.distance, 2));
            Cluster newCluster = mergeClusters(closestClusters.first,
                    closestClusters.second);
            clusterMap.put(newCluster.getClusterId(), newCluster);
            currentClusters.add(newCluster);
            currentClusters.remove(closestClusters.first);
            currentClusters.remove(closestClusters.second);
            clusterMap.remove(closestClusters.second.getClusterId());
        }
        return currentClusters.get(0);
    }

    private Cluster mergeClusters(Cluster first, Cluster second) {
        List<Gene> genes = first.getGenes();
        List<Gene> genes1 = second.getGenes();
        List<Gene> mergedList = new ArrayList<Gene>();
        mergedList.addAll(genes);
        mergedList.addAll(genes1);
        return new Cluster(first.getClusterId(), mergedList);
    }

    private ClusterPair findClosestClusters(
            List<Cluster> currentClusters) {
        Cluster firstCandidate = null;
        Cluster secondCandidate = null;
        double distanceBetweenThem = Double.MAX_VALUE;

        for (int i = 0; i < currentClusters.size(); i++) {
            Cluster clusterI = currentClusters.get(i);
            for (int j = i + 1; j < currentClusters.size(); j++) {
                Cluster clusterJ = currentClusters.get(j);
                double distance = singleLinkageDistance(clusterI,
                        clusterJ);
                if (distance <= distanceBetweenThem) {
                    firstCandidate = clusterI;
                    secondCandidate = clusterJ;
                    distanceBetweenThem = distance;
                }
            }
        }
        return new ClusterPair(firstCandidate, secondCandidate,
                distanceBetweenThem);
    }

    public double singleLinkageDistance(Cluster i, Cluster j) {
        List<Gene> genes = i.getGenes();
        List<Gene> genes1 = j.getGenes();
        Double shortestDist = Double.MIN_VALUE;
        for (int k = 0; k < genes.size(); k++) {
            for (int l = 0; l < genes1.size(); l++) {
                double distance = calculateDistance(genes.get(k),
                        genes1.get(l));
                if (shortestDist < distance) {
                    shortestDist = distance;
                }
            }
        }
        return shortestDist;
    }

    private double calculateDistance(Gene i, Gene j) {
        List<Double> list = i.getExpressionValues();
        List<Double> list1 = j.getExpressionValues();
        Double distance = 0.0;
        for (int k = 0; k < list.size(); k++) {
            double exp = list.get(k);
            double exp1 = list1.get(k);
            distance += Math.pow(Math.abs(exp - exp1), 2);
        }
        return Math.sqrt(distance);
    }

    private static class ClusterPair {

        public Cluster first;
        public Cluster second;
        public double distance;

        public ClusterPair(Cluster first, Cluster second,
                double distance) {
            this.first = first;
            this.second = second;
            this.distance = distance;
        }
    }

    public void printFile() {
        for (FileObject file : fileObjectList) {
            System.out.println(file.getIndex1() + ","
                    + file.getIndex2() + "," + file.getDistance()
                    + "," + file.getNoOfClustersMerged());
        }
    }

}
