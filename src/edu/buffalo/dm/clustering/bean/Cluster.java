package edu.buffalo.dm.clustering.bean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Amit on 11/6/2015.
 */
public class Cluster {

    private Set<Gene> genes;
    private int clusterId;
    private List<Double> centroid;

    public Cluster(int cId) {
    	clusterId = cId;
    	genes = new HashSet<Gene>();
    }
    
    public Cluster(List<Double> head, int cId) {
        centroid = head;
        genes = new HashSet<Gene>();
        clusterId = cId;
    }
    
    public Cluster(int cId, Set<Gene> genes) {
        clusterId = cId;
        this.genes = genes;
    }

    public Set<Gene> getGenes() {
        return genes;
    }

    public void addGenes(Set<Gene> genes) {
        this.genes.addAll(genes);
    }

    public void addGene(Gene gene) {
        this.genes.add(gene);
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public List<Double> getCentroid() {
        return centroid;
    }

    public void setCentroid(List<Double> centroid) {
        this.centroid = centroid;
    }
    
    public void setGenes(Set<Gene> genes) {
        this.genes = genes;
    }

}
