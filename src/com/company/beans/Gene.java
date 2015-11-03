package com.company.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Amit on 10/31/2015.
 */
public class Gene {
    int geneId;
    int clusterId;
    List<Double> expressionValues = new ArrayList<Double>();

    public Gene(int id, int cId, String[] expressionValueList) {
        geneId = id;
        clusterId = cId;
        for(String expressionValue:expressionValueList) {
            expressionValues.add(Double.parseDouble(expressionValue));
        }
    }
}
