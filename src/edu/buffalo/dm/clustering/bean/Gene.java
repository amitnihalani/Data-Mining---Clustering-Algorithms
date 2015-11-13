package edu.buffalo.dm.clustering.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amit on 10/31/2015.
 */
public class Gene {
    private int geneId;
    private int clusterId;
    private List<Double> expressionValues = new ArrayList<Double>();
    private int groundTruthClusterId;
    
    // for dbscan:
    private boolean visited;

    // Constructor for Gene.
    public Gene(int id, int cId, String[] expressionValueList) {
        geneId = id;
        clusterId = cId;
        for(String expressionValue:expressionValueList) {
            expressionValues.add(Double.parseDouble(expressionValue));
        }
        visited = false;
    }

    public Gene(int id, String groundTruthCluster, String[] expressionValueList) {
        geneId = id;
        clusterId = -1;
        groundTruthClusterId = Integer.parseInt(groundTruthCluster);
        for(String expressionValue:expressionValueList) {
            expressionValues.add(Double.parseDouble(expressionValue));
        }
    }

    // Getters and setters
    public int getGeneId() {
        return geneId;
    }

    public void setGeneId(int geneId) {
        this.geneId = geneId;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public List<Double> getExpressionValues() {
        return expressionValues;
    }

    public void setExpressionValues(List<Double> expressionValues) {
        this.expressionValues = expressionValues;
    }

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public int getGroundTruthClusterId() {
		return groundTruthClusterId;
	}

	public void setGroundTruthClusterId(int groundTruthClusterId) {
		this.groundTruthClusterId = groundTruthClusterId;
	}

}
