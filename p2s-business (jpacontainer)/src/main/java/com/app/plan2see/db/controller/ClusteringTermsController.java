package com.app.plan2see.db.controller;

import com.app.plan2see.model.db.ClusteringTerms;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Compare;
import java.util.ArrayList;
import java.util.List;

public class ClusteringTermsController extends AbstractController {

    public ClusteringTermsController(){
        super(ClusteringTerms.class);
    }

    public ClusteringTerms getEntityById(int id) {
        container.removeAllContainerFilters();
        Container.Filter filter = new Compare.Equal("id_count_history", id);
        container.addContainerFilter(filter);
        container.applyFilters();
        Object evt = container.getIdByIndex(0);
        EntityItem<Object> item = container.getItem(evt);
        ClusteringTerms e = (ClusteringTerms) item.getEntity();
        return e;
    }
    
    public List<ClusteringTerms> getEntitiesById(int iteration) {
        List<ClusteringTerms> list = new ArrayList<ClusteringTerms>();
        container.removeAllContainerFilters();
        Container.Filter filter = new Compare.Equal("id_count_history", iteration);
        container.addContainerFilter(filter);
        container.applyFilters();
        for( Object id : container.getItemIds() ){
            EntityItem<Object> item = container.getItem(id);
            ClusteringTerms e = (ClusteringTerms) item.getEntity();
            list.add(e);
        }
        return list;
    }

    public ClusteringTerms getEntityByColumnFilter(String columnName, String columnValue) {
        container.removeAllContainerFilters();
        Container.Filter filter = new Compare.Equal(columnName, columnValue);
        container.addContainerFilter(filter);
        Object evt = container.getIdByIndex(0);
        EntityItem<Object> eventItem = container.getItem(evt);
        ClusteringTerms e = (ClusteringTerms) eventItem.getEntity();
        return e;
    }
}
