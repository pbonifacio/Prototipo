package com.app.plan2see.db.controller;

import com.app.plan2see.model.db.Term;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Compare;

public class TermsController extends AbstractController {
    
    public TermsController(){
        super(Term.class);
    }
    
    public Term getEntityById(int id) {
        container.removeAllContainerFilters();
        Container.Filter filter = new Compare.Equal("eventID",id);
        container.addContainerFilter(filter);
        container.applyFilters();
        Object evt = container.getIdByIndex(0);
        EntityItem<Object> eventItem = container.getItem(evt);
        Term e = (Term) eventItem.getEntity();
        return e;
    }
    
    public Term getEntityByColumnFilter(String columnName, String columnValue) {
        container.removeAllContainerFilters();
        Container.Filter filter = new Compare.Equal(columnName,columnValue);
        container.addContainerFilter(filter);
        container.applyFilters();
        Object evt = container.getIdByIndex(0);
        EntityItem<Object> eventItem = container.getItem(evt);
        return (Term) eventItem.getEntity();
    }
}
