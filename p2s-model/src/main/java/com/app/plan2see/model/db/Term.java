package com.app.plan2see.model.db;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="term")
public class Term implements Serializable {
    public Term(){
    }
    
    public Term(int id){
        this.idterm = id;
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO) 
    int idterm;
    @Column
    String term;
    @Transient
    double mean;
    
    public int getIdterm() {
        return idterm;
    }

    public void setIdterm(int idterm) {
        this.idterm = idterm;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }
}
