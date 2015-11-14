package edu.buffalo.dm.clustering.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.buffalo.dm.clustering.bean.Cluster;
import edu.buffalo.dm.clustering.bean.Gene;
import edu.buffalo.dm.clustering.util.ClusterUtil;

/**
 * @author hharwani
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
            Set<Gene> genes = new HashSet<Gene>();
            genes.add(gene);
            Cluster cluster = new Cluster(cId, genes);
            currentClusters.add(cluster);
            cId += 1;
        }
    }

    public List<Cluster> assignGenesToCluster(int k, DistanceType distanceType) {
        generateInitialClusters();
        int clusterPairId = genes.size();
        while (currentClusters.size() > k) {
            ClusterPair closestClusters = findClosestClusters(currentClusters, distanceType);
            ClusterPair clusterPair = searchClusterPairMap(closestClusters);
            clusterPairMap.put(clusterPairId, clusterPair);
            Cluster newCluster = mergeClusters(closestClusters, clusterPairId);
            currentClusters.add(newCluster);
            currentClusters.remove(closestClusters.first);
            currentClusters.remove(closestClusters.second);
            clusterPairId++;
        }
        return currentClusters;
    }

    private ClusterPair searchClusterPairMap(ClusterPair pair) {

        List<Gene> firstClusterGenes = new ArrayList<Gene>(pair.first.getGenes());
        List<Gene> secondClusterGenes = new ArrayList<Gene>(pair.second.getGenes());
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
        pair.first.setGenes(new HashSet<Gene>(firstClusterGenes));
        pair.second.setGenes(new HashSet<Gene>(secondClusterGenes));
        return pair;
    }

    private Cluster mergeClusters(ClusterPair mergeCluster, int clusterPairId) {
        List<Gene> genes1 = new ArrayList<Gene>(mergeCluster.first.getGenes());
        List<Gene> genes2 = new ArrayList<Gene>(mergeCluster.second.getGenes());

        List<Gene> mergedList = new ArrayList<Gene>();
        mergedList.addAll(genes1);
        mergedList.addAll(genes2);
        setClusterId(mergedList, clusterPairId);
        return new Cluster(clusterPairId, new HashSet<Gene>(mergedList));
    }

    private void setClusterId(List<Gene> gene, int clusterPairId) {
        for (Gene ele : gene) {
            ele.setClusterId(clusterPairId);
        }
    }

    private ClusterPair findClosestClusters(List<Cluster> currentClusters, DistanceType distanceType) {
        Cluster firstCandidate = null;
        Cluster secondCandidate = null;
        double distanceBetweenThem = Double.MAX_VALUE;
        double distance = 0.0;
        for (int i = 0; i < currentClusters.size(); i++) {
            Cluster clusterI = currentClusters.get(i);
            for (int j = i + 1; j < currentClusters.size(); j++) {
                Cluster clusterJ = currentClusters.get(j);
                switch (distanceType) {
                    case SINGLE_LINK:
                        distance = singleLinkageDistance(clusterI, clusterJ);
                        break;
                    case COMPLETE_LINK:
                        distance = completeLinkageDistance(clusterI, clusterJ);
                        break;
                    default:
                        break;
                }
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
        List<Gene> genes = new ArrayList<Gene>(i.getGenes());
        List<Gene> genes1 = new ArrayList<Gene>(j.getGenes());
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

    public double completeLinkageDistance(Cluster i, Cluster j) {
        List<Gene> genes = new ArrayList<Gene>(i.getGenes());
        List<Gene> genes1 = new ArrayList<Gene>(j.getGenes());
        Double longestDist = Double.MIN_VALUE;
        for (int k = 0; k < genes.size(); k++) {
            for (int l = 0; l < genes1.size(); l++) {
                double distance = calculateDistance(genes.get(k), genes1.get(l));
                if (distance > longestDist) {
                    longestDist = distance;
                }
            }
        }
        return longestDist;
    }

    private double calculateDistance(Gene i, Gene j) {
        Map<Integer, Map<Integer, Double>> distanceMap = ClusterUtil.getGeneDistanceMatrix();
        return distanceMap.get(i.getGeneId()).get(j.getGeneId());
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

    public void createPythonFile() {
        System.out.println("creating output file.");
        BufferedWriter buffWriter = null;
        try {
            String path = "src/results_HAC.txt";

            File f = new File(path);
            if (f.exists() && !f.isDirectory()) {
                f.delete();
            }

            buffWriter = new BufferedWriter(new FileWriter(new File(path).getAbsoluteFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int rowNum : clusterPairMap.keySet()) {
            ClusterPair pair = clusterPairMap.get(rowNum);
            try {
                int numOfGenes = pair.first.getGenes().size() + pair.second.getGenes().size();
                buffWriter.write(pair.first.getClusterId() + ",");
                buffWriter.write(pair.second.getClusterId() + ",");
                buffWriter.write(String.format("%.6f", pair.distance) + ",");
                buffWriter.write(Integer.toString(numOfGenes));
                buffWriter.write("\n");
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    buffWriter.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }
        try {
            buffWriter.close();
        } catch (IOException e) {

        }
        System.out.println("=======================================");
        System.out.println("File Created Successfully in home directory");
    }

    public void printClusters() {
        for (Cluster cluster : currentClusters) {
            System.out.println("Cluster id is-->" + cluster.getClusterId());
            Set<Gene> geneList = cluster.getGenes();
            System.out.println("Cluster size is-->" + geneList.size());
            System.out.println("Genes in cluster are-->");
            System.out.println("[");
            for (Gene gene : geneList) {
                System.out.print(gene.getGeneId() + ",");
            }
            System.out.print("]" + "\n");
            System.out.println("==============================================");
        }
    }

    public enum DistanceType {
        SINGLE_LINK, COMPLETE_LINK
    }

}
