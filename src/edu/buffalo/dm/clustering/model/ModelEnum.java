/**
 * Created by Siddharth on Nov 11, 2015
 */
package edu.buffalo.dm.clustering.model;

public enum ModelEnum {
    K_MEANS(0),
    HIERARCHICAL(1),
    DBSCAN(2);
    private int value;

    private ModelEnum(int value) {
        this.setValue(value);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
