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
import java.util.Scanner;


public class Main {
	
	
	private static List<Gene> dataSet;
	private static String fileName;
	private static Scanner scanner;
	static {
		scanner = new Scanner(System.in);
	}
	
    public static void main(String[] args) {
    	try {
    		System.out.println("Enter filename (case-sensitive): ");
    		fileName = scanner.next();
    		dataSet = Parser.readDataSet("src/" + fileName);
	        
	        while(true) {
	        	System.out.println("1. K-means\n2. Hierarchical Agglomerative\n3. DB Scan\n4. Enter new file for dataset\n5. Exit");
	        	String choice = scanner.next();
	        	long startTime = 0;
	        	long endTime = 0;	        	
	        	switch(choice) {
	        	case "1":	// K Means
					System.out.println("Enter the value of K: ");
					int k = scanner.nextInt();
					startTime = System.currentTimeMillis();
			        runKMeans(k);
	        		endTime = System.currentTimeMillis();
			        break;
		        
	        	case "2":	// HAC
	        		startTime = System.currentTimeMillis();
	        		runHC();
	        		endTime = System.currentTimeMillis();
			        break;
			        
	        	case "3":	// DB Scan
	        		startTime = System.currentTimeMillis();
	        		runDBScan();
	        		endTime = System.currentTimeMillis();
	        		break;
	        		
	        	case "4":	// new file for dataset
					System.out.println("Enter filename (case-sensitive): ");
	        		fileName = scanner.next();
	        		dataSet = Parser.readDataSet("src/" + fileName);
	        		break;
	        		
	        	case "5":
	        		System.exit(0);
	        		
	        	default:
	        		System.out.println("Select valid choice...");
	        	}
			    System.out.println("\nExecuted in: " + ((double)(endTime - startTime) / 1000) + " seconds\n");
			    System.out.println("=================================================");
	        }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * Hierarchical Agglomerative Clustering
     */
    private static void runHC(){
        System.out.println("Running HAC.....");
        int k = 1;
        HAC hac = new HAC(dataSet);
        List<Cluster> clusters = hac.assignGenesToCluster(k,DistanceType.SINGLE_LINK);
        hac.createPythonFile();
        hac.printClusters();
        double jc = ClusterValidation.getJaccardCoefficient(dataSet, ModelEnum.HIERARCHICAL);
        double sc = ClusterValidation.getSilhouetteCoefficient(clusters);
        System.out.println("\nJaccard: " + jc + "\nSilhouette: " + sc);
        ClusterUtil.resetClusterData(dataSet);
    }
    
    /**
     * DB Scan
     */
    private static void runDBScan() {
    	DBScan ds = new DBScan();
        double eps = 1.5d;
        int minPts = 10;
        System.out.println("=======Running DBScan=======");
        System.out.println("Enter eps and minPts: ");
        eps = scanner.nextDouble();
        minPts = scanner.nextInt();
        List<Cluster> clusters = ds.dbScan(dataSet, eps, minPts);
        
        // write to file
        // *****MAKE CHANGES HERE
        Parser.writeDataToFile(dataSet, fileName, ModelEnum.DBSCAN);
        
        double jc = ClusterValidation.getJaccardCoefficient(dataSet, ModelEnum.DBSCAN);
        double sc = ClusterValidation.getSilhouetteCoefficient(clusters);
        
        ClusterUtil.printClusters(clusters);
        List<Gene> noisePoints = ClusterUtil.getNoisePoints(dataSet);
        System.out.println("Noise Points: " + noisePoints.size());
        System.out.println("\nJaccard: " + jc + "\nSilhouette: " + sc);
        
        ClusterUtil.resetClusterData(dataSet);
        
        
        /*
        for(eps = 0.5d; eps <= 4.1; eps += 0.1d) {
        	for(minPts = 10; minPts <= 100; minPts += 10) {
        		int count = 10;
        		//while(count-- > 5) {
			        String out = ds.dbScan(genes, eps, minPts);
			        List<Gene> noisePoints = ClusterUtil.getNoisePoints(dataSet);
			        //out += "," + ClusterUtil.jacardCoefficient(dataSet);
			        System.out.printf("%.1f", eps);
			        System.out.print("," + minPts + ",");
			        System.out.printf("%.3f", ClusterValidation.getJaccardCoefficient(dataSet,ModelEnum.DBSCAN));
			        System.out.println("," + out + "," + noisePoints.size());
			        System.out.println();
			        ClusterUtil.resetClusterData(dataSet);
        		//}
        	}
        }
        */        
        /*
        List<DoublePoint> genePoints = ClusterUtil.getGenePoints(dataSet);
        DBSCANClusterer<DoublePoint> dbscan = new DBSCANClusterer<DoublePoint>(eps, minPts);
        List<org.apache.commons.math3.ml.clustering.Cluster<DoublePoint>> apacheClusters = dbscan.cluster(genePoints);
        System.out.println("######################\nApache:");
        System.out.println("total clusters: " + apacheClusters.size());
        for(org.apache.commons.math3.ml.clustering.Cluster<DoublePoint> apacheCluster: apacheClusters) {
        	System.out.println(apacheCluster.getPoints().size());
        }
        */
    }

	private static void runKMeans(int k) {
		KMeans kMeans = new KMeans(dataSet, k);
		kMeans.assignGenesToClustersUsingKMeans();
		kMeans.postProcessing();
		List<Cluster> clusters = kMeans.getClusterList();
		ClusterUtil.printClusters(clusters);
		double jc = ClusterValidation.getJaccardCoefficient(dataSet, ModelEnum.K_MEANS);
		double sc = ClusterValidation.getSilhouetteCoefficient(clusters);
		System.out.println("JC: " + jc + "\nSC: " + sc);
		Parser.writeDataToFile(dataSet, fileName, ModelEnum.K_MEANS);
		ClusterUtil.resetClusterData(dataSet);
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
