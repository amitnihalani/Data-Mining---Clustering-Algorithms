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
	/*public String dbScan(List<Gene> genes, double eps, int minPts) {*/
		clusters = new ArrayList<Cluster>();
		int cId = 0;
		Collections.shuffle(genes);
		boolean first = false;
		int firstGId = -1;
		for(Gene gene: genes) {
			if(!first) {
				first = true;
				firstGId = gene.getGeneId();
			}
			if(!gene.isVisited()) {
				gene.setVisited(true);
				
				Set<Gene> neighbors = regionQuery(gene, genes, eps);
				if(neighbors.size() < minPts) {
					// mark gene as noise
				} else {
					Cluster cluster = new Cluster(cId++);
					expandCluster(gene, neighbors, cluster, eps, minPts, genes);
					clusters.add(cluster);
				}
			}
		}
		
		System.out.println("First gene: " + firstGId);
		return clusters;
		
		/*
		//String out = eps + "," + minPts + "," + firstGId;
		String out = String.format("%.3f", ClusterValidation.getSilhouetteCoefficient(clusters));
		out += "," + firstGId;
		return out;
		*/
	}

	/*private void expandCluster(Gene gene, Set<Gene> neighbors, Cluster cluster, double eps, int minPts,
			List<Gene> genes) {

		cluster.addGene(gene);
		gene.setClusterId(cluster.getClusterId());
		
		Queue<Gene> neighborQueue = new ArrayDeque<Gene>();
		for(Gene neighbor: neighbors) {
			if(!neighbor.isVisited()) {
				neighborQueue.add(neighbor);
				neighbor.setVisited(true);
			}
		}
		
		while(!neighborQueue.isEmpty()) {
			Gene neighbor = neighborQueue.remove();
			if(!neighbor.isVisited()) {
				neighbor.setVisited(true);
				Set<Gene> nNeighbors = regionQuery(neighbor, genes, eps);
				if(nNeighbors.size() >= minPts) {
					//neighbors.addAll(nNeighbors);
					for(Gene n: nNeighbors) {
						if(!n.isVisited()) {
							neighborQueue.add(n);
							n.setVisited(true);
						}
					}
				}
			}
			if(neighbor.getClusterId() < 0) {
				cluster.addGene(neighbor);
				neighbor.setClusterId(cluster.getClusterId());
			}
		}
	}*/

	/*private void expandCluster(Gene gene, Set<Gene> neighbors, Cluster cluster, double eps, int minPts,
			List<Gene> genes) {

		cluster.addGene(gene);
		gene.setClusterId(cluster.getClusterId());
		
		Queue<Gene> neighborQueue = new ArrayDeque<Gene>();
		for(Gene neighbor: neighbors) {
			if(!neighbor.isVisited()) {
				neighborQueue.add(neighbor);
				neighbor.setVisited(true);
			}
		}
		
		while(!neighborQueue.isEmpty()) {
			Gene neighbor = neighborQueue.remove();
			if(!neighbor.isVisited()) {
				neighbor.setVisited(true);
				Set<Gene> nNeighbors = regionQuery(neighbor, genes, eps);
				if(nNeighbors.size() >= minPts) {
					for(Gene n: nNeighbors) {
						if(!n.isVisited()) {
							neighborQueue.add(n);
							n.setVisited(true);
						}
					}
				}
			}
			if(neighbor.getClusterId() < 0) {
				cluster.addGene(neighbor);
				neighbor.setClusterId(cluster.getClusterId());
			}
		}
	}*/
	
	/**
	 * Expand cluster to find additional points that are density reachable from the given point
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
		for(Gene neighbor: neighbors) {
			//if(!neighbor.isVisited()) {
				neighborQueue.add(neighbor);
				//neighbor.setVisited(true);
			//}
		}
		
		while(!neighborQueue.isEmpty()) {
			Gene neighbor = neighborQueue.remove();
			if(!neighbor.isVisited()) {
				neighbor.setVisited(true);
				Set<Gene> nNeighbors = regionQuery(neighbor, genes, eps);
				if(nNeighbors.size() >= minPts) {
					for(Gene n: nNeighbors) {
						if(!neighborQueue.contains(n) && !n.isVisited()) {
							neighborQueue.add(n);
						}
					}
				}
			}
			if(neighbor.getClusterId() < 0) {	// neighbor not in any cluster
				cluster.addGene(neighbor);
				neighbor.setClusterId(cluster.getClusterId());
			}
		}
	}
	
	/**
	 * Get neighbors of this gene in eps distance
	 * @param gene
	 * @param genes
	 * @param eps
	 * @return
	 */
	private Set<Gene> regionQuery(Gene gene, List<Gene> genes, double eps) {
		Set<Gene> neighbors = new HashSet<Gene>();
		Map<Integer, Map<Integer, Double>> distanceMap = ClusterUtil.getGeneDistanceMatrix();
		for(Gene g: genes) {
			//double distance = ClusterUtil.getDistanceBetweenPoints(g.getExpressionValues(), gene.getExpressionValues(), 2);
			double distance = distanceMap.get(gene.getGeneId()).get(g.getGeneId());
			if(distance <= eps) {
				neighbors.add(g);
			}
		}
		return neighbors;
	}
	/*
	private Set<Gene> regionQuery(Gene gene, List<Gene> genes, double eps) {
		Set<Gene> neighbors = new HashSet<Gene>();
		Map<Integer, Map<Integer, Double>> distanceMap = ClusterUtil.getGeneDistanceMatrix();
		neighbors.add(gene);
		for(Gene g: genes) {
			if(!g.isVisited()) {
				//double distance = ClusterUtil.getDistanceBetweenPoints(g.getExpressionValues(), gene.getExpressionValues(), 2);
				double distance = distanceMap.get(gene.getGeneId()).get(g.getGeneId());
				if(distance <= eps) {
					neighbors.add(g);
				}
			}
		}
		return neighbors;
	}
	*/
}
