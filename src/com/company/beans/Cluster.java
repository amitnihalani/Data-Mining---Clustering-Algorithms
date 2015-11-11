
package com.company.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amit on 11/6/2015.
 */
public class Cluster {

    private List<Gene> genes;
    private int clusterId;
    private List<Double> centroid;

    public Cluster(List<Double> head, int cId) {
        centroid = head;
        genes = new ArrayList<>();
        clusterId = cId;
    }
    public Cluster(int cId,List<Gene> genes) {
        clusterId = cId;
        this.genes=genes;
    }
    public Cluster(){
        
    }
    
    

    public List<Gene> getGenes() {
        return genes;
    }

    public void addGenes(List<Gene> genes) {
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
    
    public List<Gene> getItems(){
        return genes;
    }
    
    
    

}
