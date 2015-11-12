package com.company.HAC;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.company.beans.Cluster;
import com.company.beans.Gene;

/**
 * 
 * @author hharwani
 *
 */
public class HAC {

    private List<Cluster> currentClusters = null;
    private List<Gene> genes = null;
    private Map<Integer, ClusterPair> clusterPairMap = new LinkedHashMap<Integer, ClusterPair>();

    public HAC(List<Gene> dataSet) {
        this.genes = dataSet;
    }

    public void generateInitialClusters() {
        int cId = 0;
        currentClusters = new ArrayList<Cluster>();
        for (Gene gene : this.genes) {
            gene.setClusterId(cId);
            List<Gene> genes = new ArrayList<Gene>();
            genes.add(gene);
            Cluster cluster = new Cluster(cId, genes);
            currentClusters.add(cluster);
            cId += 1;
        }
    }

    public Cluster assignGenesToCluster() {
        generateInitialClusters();
        int clusterPairId = genes.size();
        while (currentClusters.size() > 1) {
            ClusterPair closestClusters = findClosestClusters(currentClusters);
            ClusterPair clusterPair = searchClusterPairMap(closestClusters);
            clusterPairMap.put(clusterPairId, clusterPair);
            Cluster newCluster = mergeClusters(closestClusters, clusterPairId);
            currentClusters.add(newCluster);
            currentClusters.remove(closestClusters.first);
            currentClusters.remove(closestClusters.second);
            clusterPairId++;
        }
        return currentClusters.get(0);
    }

    private ClusterPair searchClusterPairMap(ClusterPair pair) {
        
        List<Gene> firstClusterGenes = pair.first.getGenes();
        List<Gene> secondClusterGenes = pair.second.getGenes();
        for (int i = genes.size(); i < genes.size() + this.clusterPairMap.size(); i++) {
            ClusterPair p = clusterPairMap.get(i);
            if (pair.first.getClusterId() == p.first.getClusterId() || pair.first.getClusterId() == p.second.getClusterId()) {
                pair.first.setClusterId(i);
                firstClusterGenes.addAll(p.first.getGenes());
                firstClusterGenes.addAll(p.second.getGenes());
            }
        }
        for (int i = genes.size(); i < genes.size() + this.clusterPairMap.size(); i++) {
            ClusterPair p = clusterPairMap.get(i);
            if (pair.second.getClusterId() == p.first.getClusterId() || pair.second.getClusterId() == p.first.getClusterId()) {
                pair.second.setClusterId(i);
                secondClusterGenes.addAll(p.first.getGenes());
                secondClusterGenes.addAll(p.second.getGenes());
            }
        }
        pair.first.setGenes(firstClusterGenes);
        pair.second.setGenes(secondClusterGenes);
        return pair;
    }

    private Cluster mergeClusters(ClusterPair mergeCluster, int clusterPairId) {
        List<Gene> genes1 = mergeCluster.first.getGenes();
        List<Gene> genes2 = mergeCluster.second.getGenes();
        List<Gene> mergedList = new ArrayList<Gene>();
        mergedList.addAll(genes1);
        mergedList.addAll(genes2);
        return new Cluster(clusterPairId, mergedList);
    }

    private ClusterPair findClosestClusters(List<Cluster> currentClusters) {
        Cluster firstCandidate = null;
        Cluster secondCandidate = null;
        double distanceBetweenThem = Double.MAX_VALUE;

        for (int i = 0; i < currentClusters.size(); i++) {
            Cluster clusterI = currentClusters.get(i);
            for (int j = i + 1; j < currentClusters.size(); j++) {
                Cluster clusterJ = currentClusters.get(j);
                double distance = singleLinkageDistance(clusterI, clusterJ);
                if (distance <= distanceBetweenThem) {
                    firstCandidate = clusterI;
                    secondCandidate = clusterJ;
                    distanceBetweenThem = distance;
                }
            }
        }
        return new ClusterPair(firstCandidate, secondCandidate, distanceBetweenThem);
    }

    public double singleLinkageDistance(Cluster i, Cluster j) {
        List<Gene> genes = i.getGenes();
        List<Gene> genes1 = j.getGenes();
        Double shortestDist = Double.MAX_VALUE;
        for (int k = 0; k < genes.size(); k++) {
            for (int l = 0; l < genes1.size(); l++) {
                double distance = calculateDistance(genes.get(k), genes1.get(l));
                if (distance <= shortestDist) {
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
            distance += Math.pow(exp - exp1, 2);
        }
        return Math.sqrt(distance);
    }

    private static class ClusterPair {

        public Cluster first;
        public Cluster second;
        public double distance;

        public ClusterPair(Cluster first, Cluster second, double distance) {
            this.first = first;
            this.second = second;
            this.distance = distance;
        }
    }

    public void printFile() {
        System.out.println("creating output file.");
        BufferedWriter buffWriter=null;
        try {
            String path = System.getProperty("user.home")+File.separator+"results.txt";
            buffWriter=new BufferedWriter(new FileWriter(new File(path)));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int rowNum : clusterPairMap.keySet()) {
            ClusterPair pair = clusterPairMap.get(rowNum);    
            try {
                buffWriter.write(pair.first.getClusterId() + ",");
                buffWriter.write(pair.second.getClusterId() + ",");
                buffWriter.write(String.format("%.6f", pair.distance) + ",");
                buffWriter.write(pair.first.getGenes().size() + pair.second.getGenes().size());
                buffWriter.write("\n");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                try {
                    buffWriter.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            
        }
        System.out.println("=======================================");
        System.out.println("File Created Successfully in home directory");
    }

    public void printClusters() {
        for (Cluster cluster : currentClusters) {
            System.out.println("Id is-->" + cluster.getClusterId());
            List<Gene> geneList = cluster.getGenes();
            System.out.println("Size-->" + geneList.size());
            System.out.println();
        }
    }
    
}
