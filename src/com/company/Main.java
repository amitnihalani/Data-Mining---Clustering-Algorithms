package com.company;

import com.company.beans.Gene;
import com.company.parser.Parser;

import java.util.List;

public class Main {

    public static void main(String[] args) {
	// write your code here
        List<Gene> dataSet = Parser.readDataSet("src/cho.txt");
        System.out.println(dataSet);
    }
}
