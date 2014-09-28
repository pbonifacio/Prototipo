package com.app.plan2see.model;

import java.util.HashMap;

public class Centroid {
    
    private int clusterId;
    private HashMap<String, Double> clusterCentroid;

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public HashMap<String, Double> getClusterCentroid() {
        return clusterCentroid;
    }

    public void setClusterCentroid(HashMap<String, Double> clusterCentroid) {
        this.clusterCentroid = clusterCentroid;
    }
}
