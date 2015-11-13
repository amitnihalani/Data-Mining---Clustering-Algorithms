package edu.buffalo.dm.clustering;

import edu.buffalo.dm.clustering.bean.Cluster;
import edu.buffalo.dm.clustering.bean.Gene;
import edu.buffalo.dm.clustering.model.DBScan;
import edu.buffalo.dm.clustering.model.HAC;
import edu.buffalo.dm.clustering.model.KMeans;
import edu.buffalo.dm.clustering.model.ModelEnum;
import edu.buffalo.dm.clustering.model.HAC.DistanceType;
import edu.buffalo.dm.clustering.util.ClusterUtil;
import edu.buffalo.dm.clustering.util.ClusterValidation;
import edu.buffalo.dm.clustering.util.Parser;

import java.util.List;


public class Main {

    public static void main(String[] args) {

        List<Gene> dataSet = Parser.readDataSet("src/iyer.txt");
        List<Cluster> clusters = null;
        long startTime = System.currentTimeMillis();

        /*
        // K Means
        int k = 5;
        KMeans kMeans = new KMeans(dataSet, 5);
        kMeans.assignGenesToClusters();
        //printClusters(kMeans);
        clusters = kMeans.getClusterList();
        ClusterUtil.printClusters(clusters);
        //System.out.println("DataSet size: " + dataSet.size());
        double jc = ClusterValidation.getJaccardCoefficient(dataSet, ModelEnum.K_MEANS);
        double sc = ClusterValidation.getSilhouetteCoefficient(clusters);
        System.out.println("JC: " + jc + "\nSC: " + sc);
        ClusterUtil.resetClusterData(dataSet);
        */
        
        /*
        // DB Scan
        runDBScan(dataSet);
        */

        /*
        // HAC
        System.out.println("Running.....");
        int k = 5;
        HAC hac = new HAC(dataSet);
        clusters = hac.assignGenesToCluster(k);
        hac.createPythonFile();
        //hac.printClusters();
        double jc = ClusterValidation.getJaccardCoefficient(dataSet, ModelEnum.HIERARCHICAL);
        double sc = ClusterValidation.getSilhouetteCoefficient(clusters);
        System.out.println("JC: " + jc + "\nSC: " + sc);
        ClusterUtil.resetClusterData(dataSet);
        */
        runHC(dataSet);
        long endTime = System.currentTimeMillis();
        System.out.println("\nExecuted in: " + ((double)(endTime - startTime) / 1000) + " seconds\n");
        System.out.println("=================================================");

    }
    
    
    private static void runHC(List<Gene> dataSet){
        System.out.println("Running.....");
        int k = 1;
        HAC hac = new HAC(dataSet);
        List<Cluster> clusters = hac.assignGenesToCluster(k,DistanceType.SINGLE_LINK);
        hac.createPythonFile();
        hac.printClusters();
        double jc = ClusterValidation.getJaccardCoefficient(dataSet, ModelEnum.HIERARCHICAL);
        double sc = ClusterValidation.getSilhouetteCoefficient(clusters);
        System.out.println("JC: " + jc + "\nSC: " + sc);
        ClusterUtil.resetClusterData(dataSet);
    }
    
    
    private static void runDBScan(List<Gene> genes) {
    	DBScan ds = new DBScan();
        double eps = 1.5d;
        int minPts = 10;
        
        System.out.println("Running DBScan (eps = " + eps + ", minPts = " + minPts + "):");
        List<Cluster> clusters = ds.dbScan(genes, eps, minPts);
        
        // write to file
        // *****MAKE CHANGES HERE
        Parser.writeDataToFile(genes, ModelEnum.DBSCAN);
        
        double jc = ClusterValidation.getJaccardCoefficient(genes, ModelEnum.DBSCAN);
        double sc = ClusterValidation.getSilhouetteCoefficient(clusters);
        
        ClusterUtil.printClusters(clusters);
        List<Gene> noisePoints = ClusterUtil.getNoisePoints(genes);
        System.out.println("Noise Points: " + noisePoints.size());
        System.out.println("\nJaccard: " + jc + "\nSilhouette: " + sc);
        
        ClusterUtil.resetClusterData(genes);
        
        
        /*
        for(eps = 0.5d; eps <= 4.1; eps += 0.1d) {
        	for(minPts = 10; minPts <= 100; minPts += 10) {
        		int count = 10;
        		//while(count-- > 5) {
			        String out = ds.dbScan(genes, eps, minPts);
			        List<Gene> noisePoints = ClusterUtil.getNoisePoints(genes);
			        //out += "," + ClusterUtil.jacardCoefficient(dataSet);
			        System.out.printf("%.1f", eps);
			        System.out.print("," + minPts + ",");
			        System.out.printf("%.3f", ClusterValidation.getJaccardCoefficient(genes,ModelEnum.DBSCAN));
			        System.out.println("," + out + "," + noisePoints.size());
			        System.out.println();
			        ClusterUtil.resetClusterData(genes);
        		//}
        	}
        }
        */        
        /*
        List<DoublePoint> genePoints = ClusterUtil.getGenePoints(genes);
        DBSCANClusterer<DoublePoint> dbscan = new DBSCANClusterer<DoublePoint>(eps, minPts);
        List<org.apache.commons.math3.ml.clustering.Cluster<DoublePoint>> apacheClusters = dbscan.cluster(genePoints);
        System.out.println("######################\nApache:");
        System.out.println("total clusters: " + apacheClusters.size());
        for(org.apache.commons.math3.ml.clustering.Cluster<DoublePoint> apacheCluster: apacheClusters) {
        	System.out.println(apacheCluster.getPoints().size());
        }
        */
    }
/*
    private static void printClusters(KMeans kMeans) {
        int clusterCount = 1, total =0;
        for(Cluster c : kMeans.clusters.values()) {
            System.out.println(String.format("Cluster #: %d \t Gene count: %d", clusterCount++, c.getGenes().size()));
            total += c.getGenes().size();
        }

        System.out.println("Total gene count: " + total);
    }*/
}
