/**
 * Created by Siddharth on Nov 12, 2015
 */
package edu.buffalo.dm.clustering.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.buffalo.dm.clustering.bean.Cluster;
import edu.buffalo.dm.clustering.bean.Gene;
import edu.buffalo.dm.clustering.model.ModelEnum;

public class ClusterValidation {
	
	static int[][] groundTruthMatrix;
	static int[][] clusterMatrixForDBScan;
	static int[][] clusterMatrixForKMeans;
	static int[][] clusterMatrixForHierarchical;
	
	/**
     * Populate ground truth matrix
     * @param genes
     */
    public static void populateGroundTruthMatrix(List<Gene> genes) {
    	int size = genes.size();
    	groundTruthMatrix = new int[size][size];
    	for(int row=0; row<size; row++) {
    		for(int col=row; col<size; col++) {
    			Gene geneR = genes.get(row);
    			Gene geneC = genes.get(col);
    			if(geneR.getGroundTruthClusterId() == geneC.getGroundTruthClusterId()) {
    				groundTruthMatrix[row][col] = groundTruthMatrix[col][row] = 1;
    			} else {
    				groundTruthMatrix[row][col] = groundTruthMatrix[col][row] = 0;
    			}
    		}
    	}
    }
    
    /**
     * Populate appropriate cluster matrix for given model
     * @param genes
     * @param model
     */
    public static void populateMatrix(List<Gene> genes, ModelEnum model) {
    	int size = genes.size();
    	int[][] clusterMatrix = new int[size][size];
    	for(int row=0; row<size; row++) {
    		for(int col=row; col<size; col++) {
    			Gene geneR = genes.get(row);
    			Gene geneC = genes.get(col);
    			if(geneR.getClusterId() == geneC.getClusterId()) {
    				clusterMatrix[row][col] = clusterMatrix[col][row] = 1;
    			} else {
    				clusterMatrix[row][col] = clusterMatrix[col][row] = 0;
    			}
    		}
    	}
    	
    	switch(model) {
    	case K_MEANS:		// kmeans
    		clusterMatrixForKMeans = clusterMatrix;
    		break;
    	case HIERARCHICAL:		// hierarchical
    		clusterMatrixForHierarchical = clusterMatrix;
    		break;
    	case DBSCAN:		// dbScan
    		clusterMatrixForDBScan = clusterMatrix;
    	}
    }
    
    /**
     * Method to calculate jaccard coefficient for given clustering model
     * @param genes
     * @param model
     * @return
     */
    public static double getJaccardCoefficient(List<Gene> genes, ModelEnum model) {
    	
    	populateGroundTruthMatrix(genes);
    	populateMatrix(genes, model);
    	
    	double jaccardCoefficient;
    	int[][] clusterMatrix;
    	switch(model) {
    	case K_MEANS:
    		clusterMatrix = clusterMatrixForKMeans;
    		break;
    	case HIERARCHICAL:
    		clusterMatrix = clusterMatrixForHierarchical;
    		break;
    	case DBSCAN:
    		clusterMatrix = clusterMatrixForDBScan;
    		break;
    	default:
    		return -1;	
    	}
    	
		int ss = 0, sd = 0, ds = 0;
		for (int row = 0; row < groundTruthMatrix.length; row++) {
			for (int col = 0; col < groundTruthMatrix.length; col++) {
				String matrixValue = Integer.toString(groundTruthMatrix[row][col]) + Integer.toString(clusterMatrix[row][col]);
				switch (matrixValue) {
				case "11":
					ss++;
					break;
				case "10":
					sd++;
					break;
				case "01":
					ds++;
					break;
				case "00":
					//dd++
					break;
				}
			}
		}
		jaccardCoefficient = calculateJaccardCoefficient((ss), (sd), (ds));
		return jaccardCoefficient;
	}
    
    /**
     * Calculates silhouette coefficient
     * @param clusters
     * @return
     */
    public static double getSilhouetteCoefficient(List<Cluster> clusters) {
    	int size = clusters.size();
    	if(size == 1) {
    		return -1d;
    	}
    	Map<Integer, Map<Integer, Double>> distanceMap = ClusterUtil.getGeneDistanceMatrix();
    	
    	double avgCoeff = 0d;
    	for(int i=0; i<size; i++) {
    		Cluster clusterI = clusters.get(i);
    		List<Gene> genesI = new ArrayList<Gene>(clusterI.getGenes());
    		for(int k=0; k<genesI.size(); k++) {
    			double ai = 0;
    			Gene geneK = genesI.get(k);
    			double intraDistanceK = 0d;
    			for(int l=0; l<genesI.size(); l++) {
    				if(k == l) {
    					continue;
    				}
    				Gene geneL = genesI.get(l);
    				intraDistanceK += distanceMap.get(geneK.getGeneId()).get(geneL.getGeneId());
    			}
    			ai = intraDistanceK/genesI.size();
    			
    			double bi = 1000d;
    			for(int j=0; j<size; j++) {
        			if(i == j) {
        				continue;
        			}
        			Cluster clusterJ = clusters.get(j);
        			List<Gene> genesJ = new ArrayList<Gene>(clusterJ.getGenes());
        			double sumInterGenesDistance = 0d;
        			for(Gene geneJ: genesJ) {
        				sumInterGenesDistance += distanceMap.get(geneK.getGeneId()).get(geneJ.getGeneId());
        			}
        			double avgInterGenesDistanceKJ = sumInterGenesDistance/genesJ.size();
        			if(avgInterGenesDistanceKJ < bi) {
        				bi = avgInterGenesDistanceKJ;
        			}
        		}
    			
    			double si = (bi - ai) / Math.max(ai, bi);
    			avgCoeff += si;
    		}
    	}
    	avgCoeff /= groundTruthMatrix[0].length;
    	return avgCoeff;
    }
    
    
/*    
    *//**
     * Calculates jaccard coefficient
     * @param genes
     * @return
     *//*
    public static double jacardCoefficient(List<Gene> genes) {
    	int n = genes.size();
    	int[][] groundTruthMatrix = new int[n][n];
    	int[][] clusterMatrix = new int[n][n];
    	
    	for(int i=0; i<n; i++) {
    		for(int j=i; j<n; j++) {
    			Gene geneI = genes.get(i);
    			Gene geneJ = genes.get(j);
    			if(geneI.getGroundTruthClusterId() == geneJ.getGroundTruthClusterId()) {
    				groundTruthMatrix[i][j] = groundTruthMatrix[j][i] = 1;
    			} else {
    				groundTruthMatrix[i][j] = groundTruthMatrix[j][i] = 0;
    			}
    			
    			if(geneI.getClusterId() == geneJ.getClusterId()) {
    				clusterMatrix[i][j] = clusterMatrix[j][i] = 1;
    			} else {
    				clusterMatrix[i][j] = clusterMatrix[j][i] = 0;
    			}
    		}
    	}
    	
		int ss = 0, sd = 0, ds = 0, dd = 0;
    	for (int row = 0; row < n; row++) {
			for (int col = 0; col < n; col++) {
				String matrixValue = Integer.toString(groundTruthMatrix[row][col]) + Integer.toString(clusterMatrix[row][col]);
				switch (matrixValue) {
				case "11":
					ss++;
					break;
				case "10":
					sd++;
					break;
				case "01":
					ds++;
					break;
				case "00":
					dd++;
					break;
				}
			}
		}
		double jaccardCoefficient = getJaccardCoefficient((ss), (sd), (ds));
    	return jaccardCoefficient;
    }
*/
	
	private static double calculateJaccardCoefficient(double ss, double sd, double ds) {
		return ss / (ss + sd + ds);
	}
}
