package com.app.plan2see.db.controller;

import com.app.plan2see.model.db.Client;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Compare;

public class ClientsController extends AbstractController {

    public ClientsController() {
        super(Client.class);
    }

    public void updateEntity(Client c) {
        EntityItem<Client> item = getEntityItemById(c.getClientID());
        Client client = (Client) item.getEntity();
        if( client.getClientID() == c.getClientID() ){
            item.refresh();
            item.getItemProperty("choosedEvents").setValue(c.getChoosedEvents());
            item.commit();
        }
        container.commit();
    }
    
    public Client getEntityByIndex(int id) {
        container.removeAllContainerFilters();
        Object evt = container.getIdByIndex(id);
        EntityItem<Object> item = container.getItem(evt);
        Client e = (Client) item.getEntity();
        return e;
    }

    public Client getEntityById(int id) {
        container.removeAllContainerFilters();
        Container.Filter filter = new Compare.Equal("clientID", id);
        container.addContainerFilter(filter);
        container.applyFilters();
        Object evt = container.getIdByIndex(0);
        EntityItem<Object> item = container.getItem(evt);
        Client e = (Client) item.getEntity();
        return e;
    }

    public EntityItem<Client> getEntityItemById(int id) {
        container.removeAllContainerFilters();
        Container.Filter filter = new Compare.Equal("clientID", id);
        container.addContainerFilter(filter);
        container.applyFilters();
        Object evt = container.getIdByIndex(0);
        return container.getItem(evt);
    }

    public Client getClientLogin(String username, String password) {
        Client c = (Client) getEntityByColumnFilter("username", username);
        if( c == null)
            return null;
        if (c.getUsername().equals(username)
                && c.getPassword().equals(password)) {
            return c;
        }
        return null;
    }

    public Client getEntityByColumnFilter(String columnName, String columnValue) {
        container.removeAllContainerFilters();
        Container.Filter filter = new Compare.Equal(columnName, columnValue);
        container.addContainerFilter(filter);
        Object evt = container.getIdByIndex(0);
        if( evt == null )
            return null;
        EntityItem<Object> eventItem = container.getItem(evt);
        Client e = (Client) eventItem.getEntity();
        return e;
    }
}
