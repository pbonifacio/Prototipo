package com.app.plan2see.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="clustering_history")
public class ClusteringHistory implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id_history;
    String action;
    int idevent;
    @Temporal(TemporalType.TIMESTAMP)
    Date timestamp;
    int id_count_history;
    int cluster;
    String relevant_terms;

    public int getId_count_history() {
        return id_count_history;
    }

    public void setId_count_history(int id_count_history) {
        this.id_count_history = id_count_history;
    }

    public String getRelevant_terms() {
        return relevant_terms;
    }

    public void setRelevant_terms(String relevant_terms) {
        this.relevant_terms = relevant_terms;
    }

    public ClusteringHistory() {
    }
    
    public ClusteringHistory(int id){
        this.cluster = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getIdevent() {
        return idevent;
    }

    public void setIdevent(int idevent) {
        this.idevent = idevent;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getId_Count_history() {
        return id_count_history;
    }

    public void setId_Count_history(int id_count_history) {
        this.id_count_history = id_count_history;
    }

    public int getCluster() {
        return cluster;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }

    public int getId_history() {
        return id_history;
    }

    public void setId_history(int id_history) {
        this.id_history = id_history;
    }
}
