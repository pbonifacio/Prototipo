package com.app.plan2see.model.db;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="event")
public class Event implements java.io.Serializable {

    public Event(){
        
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO) 
    private Integer eventID;
    private String eventTitle;
    private String eventDescription;
    private String eventSource;
    @ManyToMany(mappedBy="choosedEvents")
    List<Client> clients;
    int cluster;
    
    public String getEventTitle() {
        eventTitle = eventTitle.replaceAll("&.*?;", "");
        eventTitle = eventTitle.replaceAll("<!--.*?-->", "");
        eventTitle = eventTitle.replaceAll("<img.*?/>", "");
        eventTitle = eventTitle.replaceAll("<.*?/>;", "");
        eventTitle = eventTitle.replaceAll("•", "");
        eventTitle = eventTitle.replaceAll("<.*?>", "");
        eventTitle = eventTitle.replaceAll("</.*?>", "");
        eventTitle = eventTitle.replaceAll("</.*?>", "");
        return this.eventTitle;
    }

    public void setEventTitle(String eventName) {
        this.eventTitle = eventName;
    }

    public String getEventDescription() {
        eventDescription = eventDescription.replaceAll("&.*?;", "");
        eventDescription = eventDescription.replaceAll("<!--.*?-->", "");
        eventDescription = eventDescription.replaceAll("<img.*?/>", "");
        eventDescription = eventDescription.replaceAll("•", "");
        eventDescription = eventDescription.replaceAll("</.*?>", "");
        eventDescription = eventDescription.replaceAll("<.*?/>;", "");
        eventDescription = eventDescription.replaceAll("<.*?>", "");
        eventTitle = eventTitle.replaceAll("</.*?>", "");
        return this.eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventSource() {
        return this.eventSource;
    }

    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }

    public Integer getEventID() {
        return eventID;
    }

    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public int getCluster() {
        return cluster;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }
}
