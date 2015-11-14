
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
	        	int k=5;
	        	switch(choice) {
	        	case "1":	// K Means
					System.out.println("Enter the value of K: ");
					k = scanner.nextInt();
			        runKMeans(k);
			        break;
	        	case "2":
	        	    System.out.println("Enter the value of K: ");
                    k = scanner.nextInt();
	        		runHC(k);
			        break;
	        	case "3":	// DB Scan
	        		runDBScan();
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
			    
			    System.out.println("=================================================");
	        }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * Hierarchical Agglomerative Clustering
     */
    private static void runHC(int k){
        long startTime = System.currentTimeMillis();
        System.out.println("Running HAC.....");
        HAC hac = new HAC(dataSet);
        List<Cluster> clusters = hac.assignGenesToCluster(k,DistanceType.SINGLE_LINK);
        hac.createPythonFile();
        long endTime = System.currentTimeMillis();
        for(Cluster c:clusters) {
            ClusterUtil.reCalculateCentroid(c);
        }
        ClusterUtil.calculateSSE(clusters);
        ClusterUtil.printClusters(clusters);
        double jc = ClusterValidation.getJaccardCoefficient(dataSet, ModelEnum.HIERARCHICAL);
        double sc = ClusterValidation.getSilhouetteCoefficient(clusters);
        System.out.println("\nJaccard: " + jc + "\nSilhouette: " + sc);
        Parser.writeDataToFile(dataSet, fileName, ModelEnum.HIERARCHICAL);
        ClusterUtil.resetClusterData(dataSet);
        System.out.println("\nExecuted in: " + ((double)(endTime - startTime) / 1000) + " seconds\n");
        
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
        long startTime = System.currentTimeMillis();
        List<Cluster> clusters = ds.dbScan(dataSet, eps, minPts);
        for(Cluster c:clusters) {
            ClusterUtil.reCalculateCentroid(c);
        }
        ClusterUtil.calculateSSE(clusters);
        ClusterUtil.printClusters(clusters);
        long endTime = System.currentTimeMillis();
        Parser.writeDataToFile(dataSet, fileName, ModelEnum.DBSCAN);
        double jc = ClusterValidation.getJaccardCoefficient(dataSet, ModelEnum.DBSCAN);
        double sc = ClusterValidation.getSilhouetteCoefficient(clusters);
        List<Gene> noisePoints = ClusterUtil.getNoisePoints(dataSet);
        System.out.println("Noise Points: " + noisePoints.size());
        System.out.println("\nJaccard: " + jc + "\nSilhouette: " + sc);
        ClusterUtil.resetClusterData(dataSet);
        System.out.println("\nExecuted in: " + ((double)(endTime - startTime) / 1000) + " seconds\n");
    }

	private static void runKMeans(int k) {
	    long startTime = System.currentTimeMillis();
		KMeans kMeans = new KMeans(dataSet, k);
		kMeans.assignGenesToClustersUsingKMeans();
		kMeans.postProcessing();
		ClusterUtil.calculateSSE(kMeans.getClusterList());
		List<Cluster> clusters = kMeans.getClusterList();
		ClusterUtil.printClusters(clusters);
		long endTime = System.currentTimeMillis();
		double jc = ClusterValidation.getJaccardCoefficient(dataSet, ModelEnum.K_MEANS);
		double sc = ClusterValidation.getSilhouetteCoefficient(clusters);
		System.out.println("JC: " + jc + "\nSC: " + sc);
		Parser.writeDataToFile(dataSet, fileName, ModelEnum.K_MEANS);
		ClusterUtil.resetClusterData(dataSet);
		System.out.println("\nExecuted in: " + ((double)(endTime - startTime) / 1000) + " seconds\n");
	}

}
