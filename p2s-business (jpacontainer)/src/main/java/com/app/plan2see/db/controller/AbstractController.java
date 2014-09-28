package com.app.plan2see.db.controller;

import com.app.plan2see.db.icontroller.IDBAccess;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractController implements IDBAccess {
    protected JPAContainer container;
    public static final String PERSISTENCE_UNIT = "Plan2See";
    
    public AbstractController(Class obj){
        this.container = JPAContainerFactory.make(obj, PERSISTENCE_UNIT);
        this.container.setAutoCommit(true);
    }
    
    public void refresh(){
        container.refresh();
    }
    
    public void insertEntity(Object entity) {
        container.addEntity(entity);
    }

    public void removeEntity(int idx) {
        Object itemId = container.getIdByIndex(idx);
        container.removeItem(itemId);
    }
    
    public List<EntityItem> getAllEntities() {
        container.removeAllContainerFilters();
        List<EntityItem> objects = new ArrayList<EntityItem>();
        int idx = 0;
        while( idx < getEntitiesCount() ){
            Object id = container.getIdByIndex(idx);
            EntityItem<Object> item = container.getItem(id);
            objects.add(item);
            idx++;
        }
        return objects;
    }
    
    public int getEntitiesCount() {
        container.removeAllContainerFilters();
        return container.size();
    }
    
    public abstract Object getEntityById(int id);
    
    @Override
    public abstract Object getEntityByColumnFilter(String columnName, String columnValue);
    
    public void commit(){
        container.commit();
    }
}
