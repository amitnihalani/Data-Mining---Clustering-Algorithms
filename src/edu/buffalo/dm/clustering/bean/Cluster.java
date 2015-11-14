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
    private Gene medoidGene;

    public Cluster(int cId) {
        clusterId = cId;
        genes = new HashSet<Gene>();
    }

    public Cluster(List<Double> head, int cId) {
        centroid = head;
        genes = new HashSet<Gene>();
        clusterId = cId;
        //medoid = new Gene()
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "genes=" + genes +
                '}';
    }

    public Cluster(Gene gene, int cId) {
        centroid = gene.getExpressionValues();
        genes = new HashSet<Gene>();
        clusterId = cId;
        medoidGene = gene;
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

    public Gene getMedoidGene() {
        return medoidGene;
    }

    public void setMedoidGene(Gene medoidGene) {
        this.medoidGene = medoidGene;
    }

}
