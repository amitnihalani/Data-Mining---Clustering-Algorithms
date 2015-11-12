
package com.company.beans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Amit on 11/6/2015.
 */
public class Cluster {

    private Set <Gene> genes;
    private int clusterId;
    private List<Double> centroid;

    public Cluster(List<Double> head, int cId) {
        centroid = head;
        genes = new HashSet<>();
        clusterId = cId;
    }
    public Cluster(int cId,List<Gene> genes) {
        clusterId = cId;
        this.genes=genes;
    }
    public Cluster(){
        
    }
    
    

    public Set<Gene> getGenes() {
        return genes;
    }

    public void addGenes(List<Gene> genes) {
        this.genes.addAll(genes);
    }
    public void addGenes(Set<Gene> genes) {
        this.genes.addAll(genes);
    }

    public void addGene(Gene gene) {
        this.genes.add(gene);
    }

    public void setGenes(List<Gene> genes) {
        this.genes = genes;
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
    
    public List<Gene> getItems(){
        return genes;
    }
    
    
    

}
