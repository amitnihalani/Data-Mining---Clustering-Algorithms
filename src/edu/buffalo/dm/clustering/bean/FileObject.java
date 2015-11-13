package edu.buffalo.dm.clustering.bean;

public class FileObject {
    private int index1;
    private int index2;
    private double distance;
    private int noOfClustersMerged;

    public FileObject(int index1, int index2, double distance,
            int noOfClustersMerged) {
        this.index1 = index1;
        this.index2 = index2;
        this.distance = distance;
        this.noOfClustersMerged = noOfClustersMerged;
    }

    public int getIndex1() {
        return index1;
    }

    public void setIndex1(int index1) {
        this.index1 = index1;
    }

    public int getIndex2() {
        return index2;
    }

    public void setIndex2(int index2) {
        this.index2 = index2;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getNoOfClustersMerged() {
        return noOfClustersMerged;
    }

    public void setNoOfClustersMerged(int noOfClustersMerged) {
        this.noOfClustersMerged = noOfClustersMerged;
    }

}
