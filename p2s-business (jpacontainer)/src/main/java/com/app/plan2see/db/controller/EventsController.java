package com.app.plan2see.db.controller;

import com.app.plan2see.model.db.Client;
import com.app.plan2see.model.db.Event;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;
import java.util.ArrayList;
import java.util.List;

public class EventsController extends AbstractController{
    
    public EventsController(){
        super(Event.class);
    }
    
    public void updateEntity(Event e) {
        EntityItem<Event> item = getEntityItemById(e.getEventID());
        item.refresh();
        item.getItemProperty("cluster").setValue(e.getCluster());
        item.commit();
        container.commit();
    }
    
    public List<Event> getEntitiesById(List<Integer> ids) {
        container.removeAllContainerFilters();
        List<Event> events = new ArrayList<Event>();
        int idx = 0;
        while( idx < getEntitiesCount() ){
            Object id = container.getIdByIndex(idx);
            EntityItem<Object> evtItem = container.getItem(id);
            Event evt = (Event) evtItem.getEntity();
            if( ids.contains(evt.getEventID()) )
                events.add(evt);
            idx++;
        }
        return events;
    }
    
    public EntityItem<Event> getEntityItemById(int id) {
        container.removeAllContainerFilters();
        Container.Filter filter = new Compare.Equal("eventID", id);
        container.addContainerFilter(filter);
        container.applyFilters();
        Object evt = container.getIdByIndex(0);
        return container.getItem(evt);
    }
    
    public Event getEntityByIndex(int id) {
        container.removeAllContainerFilters();
        Object evt = container.getIdByIndex(id);
        EntityItem<Object> item = container.getItem(evt);
        Event e = (Event) item.getEntity();
        return e;
    }

    public Event getEntityById(int id) {
        container.removeAllContainerFilters();
        Filter filter = new Compare.Equal("eventID",id);
        container.addContainerFilter(filter);
        container.applyFilters();
        Object evt = container.getIdByIndex(0);
        EntityItem<Object> eventItem = container.getItem(evt);
        Event e = (Event) eventItem.getEntity();
        return e;
    }
    
    public Event getEntityByColumnFilter(String columnName, String columnValue) {
        container.removeAllContainerFilters();
        Filter filter = new Compare.Equal(columnName,columnValue);
        container.addContainerFilter(filter);
        container.applyFilters();
        Object evt = container.getIdByIndex(0);
        EntityItem<Object> eventItem = container.getItem(evt);
        Event e = (Event) eventItem.getEntity();
        return e;
    }
}
