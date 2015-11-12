package com.company;

import com.company.HAC.HAC;
import com.company.beans.Cluster;
import com.company.beans.Gene;
import com.company.k_means.ExternalIndex;
import com.company.k_means.KMeans;
import com.company.parser.Parser;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<Gene> dataSet = Parser.readDataSet("src/iyer.txt");
 /*       KMeans kMeans = new KMeans(dataSet, 5);
//        kMeans.assignGenesToClusters();
       // printClusters(kMeans);
        System.out.println("DataSet size: " + dataSet.size());
        ExternalIndex index = new ExternalIndex(kMeans);
        System.out.println("Jaccard coef: " + index.getJaccardCoefficient());*/
        System.out.println("Running.....");
        System.out.println("===============================================");
        long startTime=System.currentTimeMillis();
        HAC hac=new HAC(dataSet);
        hac.assignGenesToCluster();
        hac.printFile();
        long endTime=System.currentTimeMillis();
        System.out.println("===============================================");
        System.out.println("Exeution Finished.");
        System.out.println("Total time taken is-> "+(endTime-startTime)/1000+" seconds");
        //hac.printMap();
        //hac.printClusters();
    }

   /* private static void printClusters(KMeans kMeans) {
        int clusterCount = 1, total =0;
        for(Cluster c : kMeans.getClusters().values()) {
            System.out.println(String.format("Cluster #: %d \t Gene count: %d", clusterCount++, c.getGenes().size()));
            total += c.getGenes().size();
        }

        System.out.println("Total gene count: " + total);
    }*/
}