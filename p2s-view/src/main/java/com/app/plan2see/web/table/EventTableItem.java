package com.app.plan2see.web.table;

import com.app.plan2see.model.db.Client;
import com.app.plan2see.web.servlet.P2SWindow;

public class EventTableItem {
    P2SCheckBox selected;
    String title;
    String description;
    int cluster;
    int id;
    Client client;
    
    public EventTableItem(int id, int cluster, String title, String description, 
            Client c, P2SWindow UI){
        this.id = id;
        this.title = title;
        this.description = description;
        this.client = c;
        this.cluster = cluster;
        selected = new P2SCheckBox(id, UI);
    }
    
    public P2SCheckBox getSelected() {
        return selected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCluster() {
        return cluster;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }
}
