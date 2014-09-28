package com.app.plan2see.model.db;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ttest")
public class TTestTerm implements Serializable {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int idttest;
    private String relevant_term;
    private double mean;
    private int id_count_history;
    private int subset;

    public int getIdttest() {
        return idttest;
    }

    public void setIdttest(int idttest) {
        this.idttest = idttest;
    }

    public String getRelevant_term() {
        return relevant_term;
    }

    public void setRelevant_term(String relevant_term) {
        this.relevant_term = relevant_term;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public int getId_count_history() {
        return id_count_history;
    }

    public void setId_count_history(int id_count_history) {
        this.id_count_history = id_count_history;
    }

    public int getSubset() {
        return subset;
    }

    public void setSubset(int subset) {
        this.subset = subset;
    }
}
