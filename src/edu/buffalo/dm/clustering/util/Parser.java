package edu.buffalo.dm.clustering.util;

import edu.buffalo.dm.clustering.bean.Gene;
import edu.buffalo.dm.clustering.model.ModelEnum;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Amit on 10/31/2015.
 */
public class Parser {

    /**
     * Returns a list of gene objects created from information of genes provided in input file
     * @param filePath - the path of input (dataset) file
     * @return - a list of genes
     */
    public static List<Gene> readDataSet(String filePath) {
        BufferedReader bufferedReader = getReader(filePath);
        List<Gene> dataSet = new ArrayList<>();
        String geneData;
        try {
            while ((geneData = bufferedReader.readLine()) != null) {
                dataSet.add(getGeneObject(geneData));
            }
            ClusterValidation.populateGroundTruthMatrix(dataSet);
            ClusterUtil.generateGeneDistanceMap(dataSet);
        } catch (IOException e) {
            System.out.println("Exception while reading line");
            e.printStackTrace();
        }
        return dataSet;
    }
    
    public static void writeDataToFile(List<Gene> genes, ModelEnum model) {
    	BufferedWriter bw = null;
    	FileWriter fw = null;
    	String fileName = model.toString();
    	File file = new File("src/" + fileName + "_out");
    	try {
	    	if(!file.exists()) {
	    		file.createNewFile();
	    	}
	    	fw = new FileWriter(file.getAbsoluteFile());
	    	bw = new BufferedWriter(fw);
	    	
	    	StringBuilder sb1 = new StringBuilder();
	    	StringBuilder sb2 = new StringBuilder();
	    	StringBuilder sb3 = new StringBuilder();
	    	
	    	sb1.append("[");
	    	sb2.append("[");
	    	String buf = "";
	    	String bu = "";
	    	for(Gene gene: genes) {
	    		sb1.append(bu + "[");
	    		buf = "";
	    		sb2.append(bu + gene.getClusterId());
	    		List<Double> expr = gene.getExpressionValues();
	    		for(int i=0; i<expr.size(); i++) {
	    			sb1.append(buf + expr.get(i));
	    			buf = ", ";
	    		}
	    		sb1.append("]");
	    		bu = ", ";
	    	}
	    	sb1.append("]");
	    	sb2.append("]");
	    	
	    	bw.write(sb1.toString() + "\n" + sb2.toString());
	    	bw.close();
    	} catch(IOException e) {
    		System.err.println("Exception while writing to file");
    	}
    	
    }

    /**
     * Returns a buffered reader for a file
     * @param filePath - the path of the file
     * @return - the buffered reader object
     */
    private static BufferedReader getReader(String filePath) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            System.out.println("Incorrect file path! Please enter the correct path for input file");
            System.exit(1);
        }
        return bufferedReader;
    }

    /**
     * Returns a gene object created from the information in input file.
     * @param geneData - the gene information from input file
     * @return - the created gene object.
     */
    private static Gene getGeneObject(String geneData) {
        String[] geneInformation = geneData.split("\t");
        return new Gene(Integer.parseInt(geneInformation[0]), geneInformation[1], Arrays.copyOfRange(geneInformation, 2, geneInformation.length));
    }


}

