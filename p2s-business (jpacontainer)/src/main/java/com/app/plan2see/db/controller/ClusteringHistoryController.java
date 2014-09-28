package com.app.plan2see.db.controller;

import com.app.plan2see.model.db.ClusteringHistory;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Compare;
import java.util.ArrayList;
import java.util.List;

public class ClusteringHistoryController extends AbstractController {

    public ClusteringHistoryController(){
        super(ClusteringHistory.class);
    }
    
    public boolean existsEntity(int id) {
        container.removeAllContainerFilters();
        Container.Filter filter = new Compare.Equal("id_count_history", id);
        container.addContainerFilter(filter);
        container.applyFilters();
        if( !container.getItemIds().isEmpty() )
            return true;
        return false;            
    }
    
    public boolean existsCluster(int id) {
        container.removeAllContainerFilters();
        Container.Filter filter = new Compare.Equal("cluster", id);
        container.addContainerFilter(filter);
        container.applyFilters();
        if( !container.getItemIds().isEmpty() )
            return true;
        return false;            
    }

    public ClusteringHistory getEntityByIndex(int id) {
        container.removeAllContainerFilters();
        Object evt = container.getIdByIndex(id);
        EntityItem<Object> item = container.getItem(evt);
        ClusteringHistory e = (ClusteringHistory) item.getEntity();
        return e;
    }
    
    public ClusteringHistory getEntityById(int id) {
        container.removeAllContainerFilters();
        Container.Filter filter = new Compare.Equal("id_count_history", id);
        container.addContainerFilter(filter);
        container.applyFilters();
        Object evt = container.getIdByIndex(0);
        EntityItem<Object> item = container.getItem(evt);
        ClusteringHistory e = (ClusteringHistory) item.getEntity();
        return e;
    }
    
    public List<ClusteringHistory> getEntitiesById(int iteration) {
        List<ClusteringHistory> list = new ArrayList<ClusteringHistory>();
        container.removeAllContainerFilters();
        Container.Filter filter = new Compare.Equal("id_count_history", iteration);
        container.addContainerFilter(filter);
        container.applyFilters();
        for( Object id : container.getItemIds() ){
            EntityItem<Object> item = container.getItem(id);
            ClusteringHistory e = (ClusteringHistory) item.getEntity();
            list.add(e);
        }
        return list;
    }

    public ClusteringHistory getEntityByColumnFilter(String columnName, String columnValue) {
        container.removeAllContainerFilters();
        Container.Filter filter = new Compare.Equal(columnName, columnValue);
        container.addContainerFilter(filter);
        Object evt = container.getIdByIndex(0);
        EntityItem<Object> eventItem = container.getItem(evt);
        ClusteringHistory e = (ClusteringHistory) eventItem.getEntity();
        return e;
    }
}
