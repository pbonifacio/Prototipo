package com.app.plan2see.db.controller;

import com.app.plan2see.model.db.EventTerm;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Compare;

public class EventsTermsController extends AbstractController {
    
    public EventsTermsController(){
        super(EventTerm.class);
    }
    
    public EventTerm getEntityById(int id) {
        container.removeAllContainerFilters();
        Container.Filter filter = new Compare.Equal("ideventterm",id);
        container.addContainerFilter(filter);
        container.applyFilters();
        Object evt = container.getIdByIndex(0);
        EntityItem<Object> eventItem = container.getItem(evt);
        EventTerm e = (EventTerm) eventItem.getEntity();
        return e;
    }

    public EventTerm getEventTermByCombination(int termsSize, int idxEvent, int idxTerm) {
        int id = (idxEvent - 1) * termsSize + idxTerm ;
        return getEntityById(id);
    }
    
    public EventTerm getEntityByColumnFilter(String columnName, String columnValue) {
        container.removeAllContainerFilters();
        Container.Filter filter = new Compare.Equal(columnName,columnValue);
        container.addContainerFilter(filter);
        container.applyFilters();
        Object evt = container.getIdByIndex(0);
        EntityItem<Object> eventItem = container.getItem(evt);
        EventTerm e = (EventTerm) eventItem.getEntity();
        return e;
    }
}
