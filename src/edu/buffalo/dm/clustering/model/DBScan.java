/**
 * Created by Siddharth on Nov 10, 2015
 */
package edu.buffalo.dm.clustering.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import edu.buffalo.dm.clustering.bean.Cluster;
import edu.buffalo.dm.clustering.bean.Gene;
import edu.buffalo.dm.clustering.util.ClusterUtil;

public class DBScan {

    List<Cluster> clusters;

    public List<Cluster> dbScan(List<Gene> genes, double eps, int minPts) {
        clusters = new ArrayList<Cluster>();
        int cId = 0;
        for (Gene gene : genes) {
            if (!gene.isVisited()) {
                gene.setVisited(true);

                Set<Gene> neighbors = regionQuery(gene, genes, eps);
                if (neighbors.size() < minPts) {
                    // mark gene as noise
                } else {
                    Cluster cluster = new Cluster(cId++);
                    expandCluster(gene, neighbors, cluster, eps, minPts, genes);
                    clusters.add(cluster);
                }
            }
        }

        return clusters;
    }

    /**
     * Expand cluster to find additional points that are density reachable from the given point
     *
     * @param gene
     * @param neighbors
     * @param cluster
     * @param eps
     * @param minPts
     * @param genes
     */
    private void expandCluster(Gene gene, Set<Gene> neighbors, Cluster cluster, double eps, int minPts,
                               List<Gene> genes) {

        cluster.addGene(gene);
        gene.setClusterId(cluster.getClusterId());

        Queue<Gene> neighborQueue = new ArrayDeque<Gene>();
        for (Gene neighbor : neighbors) {
            neighborQueue.add(neighbor);
        }

        while (!neighborQueue.isEmpty()) {
            Gene neighbor = neighborQueue.remove();
            if (!neighbor.isVisited()) {
                neighbor.setVisited(true);
                Set<Gene> nNeighbors = regionQuery(neighbor, genes, eps);
                if (nNeighbors.size() >= minPts) {
                    for (Gene n : nNeighbors) {
                        if (!neighborQueue.contains(n) && !n.isVisited()) {
                            neighborQueue.add(n);
                        }
                    }
                }
            }
            if (neighbor.getClusterId() < 0) {    // neighbor not in any cluster
                cluster.addGene(neighbor);
                neighbor.setClusterId(cluster.getClusterId());
            }
        }
    }

    /**
     * Get neighbors of this gene in eps distance
     *
     * @param gene
     * @param genes
     * @param eps
     * @return
     */
    private Set<Gene> regionQuery(Gene gene, List<Gene> genes, double eps) {
        Set<Gene> neighbors = new HashSet<Gene>();
        Map<Integer, Map<Integer, Double>> distanceMap = ClusterUtil.getGeneDistanceMatrix();
        for (Gene g : genes) {
            double distance = distanceMap.get(gene.getGeneId()).get(g.getGeneId());
            if (distance <= eps) {
                neighbors.add(g);
            }
        }
        return neighbors;
    }
}
