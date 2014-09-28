package com.app.plan2see.model.db;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "clusters")
public class Cluster implements Serializable {
    
    public Cluster(){
        
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    int clusterid;
    int id;
    int clusterSize;
    String relevantTerms;

    public int getClusterid() {
        return clusterid;
    }

    public void setClusterid(int clusterid) {
        this.clusterid = clusterid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClusterSize() {
        return clusterSize;
    }

    public void setClusterSize(int clusterSize) {
        this.clusterSize = clusterSize;
    }

    public String getRelevantTerms() {
        return relevantTerms;
    }

    public void setRelevantTerms(String relevantTerms) {
        this.relevantTerms = relevantTerms;
    }
}
