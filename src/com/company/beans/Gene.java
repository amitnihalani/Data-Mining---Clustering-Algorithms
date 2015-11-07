package com.company.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Amit on 10/31/2015.
 */
public class Gene {
    private int geneId;
    private int clusterId;
    private List<Double> expressionValues = new ArrayList<Double>();

    // Constructor for Gene.
    public Gene(int id, int cId, String[] expressionValueList) {
        geneId = id;
        clusterId = cId;
        for(String expressionValue:expressionValueList) {
            expressionValues.add(Double.parseDouble(expressionValue));
        }
    }

    public Gene(int id, String[] expressionValueList) {
        geneId = id;
        clusterId = -99;
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

}
