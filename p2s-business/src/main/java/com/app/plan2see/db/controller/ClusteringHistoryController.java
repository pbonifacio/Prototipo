package com.app.plan2see.db.controller;

import com.app.plan2see.model.db.ClusteringHistory;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class ClusteringHistoryController extends DBController {

    public ClusteringHistoryController(Session session){
        super(session);
    }
    
    public void insertEntity(ClusteringHistory entity) {
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();
    }
    
    public boolean existsEntity(int id) {
        ClusteringHistory c = (ClusteringHistory) session.createCriteria(ClusteringHistory.class)
             .add( Restrictions.eq("id_count_history", id) )
                .setMaxResults(1).uniqueResult();
        if( c != null )
            return true;
        return false;            
    }
    
    @Override
    public ClusteringHistory getEntityById(int id) {
        ClusteringHistory c = (ClusteringHistory) session.get(ClusteringHistory.class, id);
        return c;
    }
    
    public List<ClusteringHistory> getEntitiesById(int iteration) {
        List<ClusteringHistory> list = session.createCriteria(ClusteringHistory.class)
             .add( Restrictions.eq("id_count_history", iteration) )
                .list();
        return list;
    }
    
    @Override
    public int getEntitiesCount(){
        int size = session.createQuery("from ClusteringHistory").list().size();
        return size;
    }
}
