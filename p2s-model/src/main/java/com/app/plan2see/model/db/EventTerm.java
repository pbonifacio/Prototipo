package com.app.plan2see.model.db;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="eventterm")
public class EventTerm implements Serializable{
    
    public EventTerm(){
        
    }
    
    public EventTerm(int id, int freq, Event e, Term t){
        this.ideventterm = id;
        this.frequency = freq;
        this.event = e;
        this.term = t;
    }
    
    @Id
    int ideventterm;
    @Column(name="frequency")
    int frequency;
    @ManyToOne
    @JoinColumn(name="idevent", referencedColumnName="eventID")
    Event event;
    @ManyToOne
    @JoinColumn(name="idterm", referencedColumnName="idterm")
    Term term;

    public int getIdeventterm() {
        return ideventterm;
    }

    public void setIdeventterm(int ideventterm) {
        this.ideventterm = ideventterm;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }
}
