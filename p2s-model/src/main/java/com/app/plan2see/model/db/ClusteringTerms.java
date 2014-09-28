package com.app.plan2see.model.db;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "clustering_terms")
public class ClusteringTerms implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id_history;
    int id_count_history;
    @ManyToOne
    @JoinColumn(name = "id_term", referencedColumnName = "idterm")
    Term term;
    int occurences;

    public ClusteringTerms() {
    }

    public int getId_count_history() {
        return id_count_history;
    }

    public void setId_count_history(int id_count_history) {
        this.id_count_history = id_count_history;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public int getId_history() {
        return id_history;
    }

    public void setId_history(int id_history) {
        this.id_history = id_history;
    }

    public int getOccurences() {
        return occurences;
    }

    public void setOccurences(int occurences) {
        this.occurences = occurences;
    }
}
