/**
 * Created by Siddharth on Nov 10, 2015
 */
package edu.buffalo.dm.clustering.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.ml.clustering.DoublePoint;

import edu.buffalo.dm.clustering.bean.Cluster;
import edu.buffalo.dm.clustering.bean.Gene;


public class ClusterUtil {
	
	static Map<Integer,Map<Integer,Double>> geneDistanceMap;
	
	/**
     * Returns distance between two genes
     *
     * @param expressionValueSet1 -
     * @param expressionValueSet2 -
     * @return the distance between two genes
     */
    public static double getDistanceBetweenPoints(List<Double> expressionValueSet1, List<Double> expressionValueSet2, int power) {
        double distance = 0;
        for (int i = 0; i < expressionValueSet1.size(); i++) {
            distance += Math.pow(expressionValueSet1.get(i) - expressionValueSet2.get(i), (double)power);
        }
        //return Math.sqrt(distance);
        return Math.pow(distance, 1/(double)power);
    }
    
    /**
     * Print clusters along with gene results
     * @param clusters - list of clusters to print
     */
    public static void printClusters(List<Cluster> clusters) {
    	System.out.println("Clusters:");
    	for(Cluster cluster: clusters) {
    		System.out.println("ClusterId: " + cluster.getClusterId() + "\tGene Count: " + cluster.getGenes().size());
    	}
    }
    
    /**
     * Resets cluster assignment for all the genes
     * @param genes - list of genes to reset
     */
    public static void resetClusterData(List<Gene> genes) {
    	for(Gene gene: genes) {
    		gene.setClusterId(-1);
    		gene.setVisited(false);
    	}
    }

    /**
     * Generate distance map for the given list of genes
     * @param genes
     */
    public static void generateGeneDistanceMap(List<Gene> genes) {
    	geneDistanceMap = new HashMap<Integer, Map<Integer, Double>>();
    	Map<Integer, Double> distances;
    	int size = genes.size();
    	for(int row=0; row<size; row++) {
    		distances = new HashMap<Integer, Double>();
    		Gene geneR = genes.get(row);
    		for(int col=0; col<size; col++) {
    			Gene geneC = genes.get(col);
    			double distance = getDistanceBetweenPoints(geneR.getExpressionValues(), geneC.getExpressionValues(), 2);
    			distances.put(geneC.getGeneId(), distance);
    		}
    		geneDistanceMap.put(geneR.getGeneId(), distances);
    	}
    }
    
    /**
     * Get distance map for the genes
     * @return
     */
    public static Map<Integer, Map<Integer, Double>> getGeneDistanceMatrix() {
    	return geneDistanceMap;
    }
    
    /**
     * Get genes that do not belong to any cluster
     * @param genes
     * @return
     */
    public static List<Gene> getNoisePoints(List<Gene> genes) {
    	List<Gene> noisePoints = new ArrayList<Gene>();
    	for(Gene gene: genes) {
    		if(gene.getClusterId() < 0) {
    			noisePoints.add(gene);
    		}
    	}
    	return noisePoints;
    }
    
    /**
     * Generate gene points list to be used for apache library function
     * @param genes
     * @return
     */
    public static List<DoublePoint> getGenePoints(List<Gene> genes) {
    	List<DoublePoint> genePoints = new ArrayList<DoublePoint>();
    	for(Gene gene: genes) {
    		double[] exprValues = new double[gene.getExpressionValues().size()];
    		for(int i=0; i<exprValues.length; i++) {
    			exprValues[i] = gene.getExpressionValues().get(i);
    		}
    		DoublePoint genePoint = new DoublePoint(exprValues);
    		genePoints.add(genePoint);
    	}
    	return genePoints;
    }

}
