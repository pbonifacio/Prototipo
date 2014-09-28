package com.app.plan2see.model.db;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
@Table(name = "client")
public class Client implements java.io.Serializable {

    public Client() {
    }

    public Client(int id, String username, String password, String clientName,
            boolean isAdmin, Date lastLogin) {
        this.clientID = id;
        this.username = username;
        this.password = password;
        this.clientName = clientName;
        this.isAdmin = isAdmin;
        this.lastLogin = lastLogin;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int clientID;
    String username;
    String password;
    String clientName;
    boolean isAdmin;
    Date lastLogin;
    @ManyToMany
    @JoinTable(
      name = "clientevent",
    joinColumns = {
        @JoinColumn(name = "idclient", referencedColumnName = "clientID")},
    inverseJoinColumns = {
        @JoinColumn(name = "idevent", referencedColumnName = "eventID")})
    private List<Event> choosedEvents = new ArrayList<Event>();

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public boolean hasChoosedEvent(int id){
        for(Event e : choosedEvents){
            if( e.getEventID() == id )
                return true;
        }
        return false;
    }
    
    public void removeEvent(int event) {
        List<Event> oldEvents = new ArrayList<Event>(choosedEvents);
        int idx = 0;
        for(Event e : oldEvents){
            if( e.getEventID() == event )
                choosedEvents.remove(idx);
            idx++;
        }
    }

    public List<Event> getChoosedEvents() {
        return choosedEvents;
    }

    public void setChoosedEvents(List<Event> choosedEvents) {
        this.choosedEvents = choosedEvents;
    }

    public int getChoosedEventsSize() {
        return getChoosedEvents().size();
    }
}
