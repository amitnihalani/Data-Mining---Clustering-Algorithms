package com.company;

import com.company.HAC.HAC;
import com.company.beans.Cluster;
import com.company.beans.Gene;
import com.company.k_means.KMeans;
import com.company.parser.Parser;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<Gene> dataSet = Parser.readDataSet("src/cho.txt");
        KMeans kMeans = new KMeans(dataSet);
        kMeans.assignGenesToClusters();
        printClusters(kMeans);
        System.out.println("DataSet size: " + dataSet.size());
        long startTime=System.currentTimeMillis();
        HAC hac=new HAC(dataSet);
        Cluster cluster=hac.assignGenesToCluster();
        System.out.println(cluster.getClusterId());
        hac.printFile();
        long endTime=System.currentTimeMillis();
        System.out.println("Total time taken is-> "+(endTime-startTime)/1000+" seconds");
    }

    private static void printClusters(KMeans kMeans) {
        int clusterCount = 1, total =0;
        for(Cluster c : kMeans.clusters.values()) {
            System.out.println(String.format("Cluster #: %d \t Gene count: %d", clusterCount++, c.getGenes().size()));
            total += c.getGenes().size();
        }
        System.out.println("Total gene count: " + total);
    }
}
