package com.app.plan2see.db.controller;

import com.app.plan2see.model.db.Cluster;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class ClustersController extends DBController {
    Logger log = Logger.getLogger(ClustersController.class);
    
    public ClustersController(Session session) {
        super(session);
    }
    
    public void updateEntity(Cluster c) {
        session.beginTransaction();
        Cluster cluster = (Cluster) session.createCriteria(Cluster.class)
                .add(Restrictions.eq("id", c.getId()))
                .setMaxResults(1).uniqueResult();
        cluster.setClusterSize(c.getClusterSize());
        cluster.setRelevantTerms(c.getRelevantTerms());
        session.update(cluster);
        session.getTransaction().commit();
    }
    
    public void insertEntity(Cluster entity) {
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();
    }
    
    public void removeEntity(Cluster entity){
        session.beginTransaction();
        Cluster cluster = (Cluster) session.createCriteria(Cluster.class)
                .add(Restrictions.eq("id", entity.getId()))
                .setMaxResults(1).uniqueResult();
        if( cluster == null ){
            return;
        }
        session.delete(cluster);
        session.getTransaction().commit();
    }
    
    @Override
    public Cluster getEntityById(int id) {
        Cluster c = (Cluster) session.createCriteria(Cluster.class)
             .add( Restrictions.eq("id", id) )
                .setMaxResults(1).uniqueResult();
        return c;
    }
    
    @Override
    public List<Cluster> getAllEntities() {
        List<Cluster> list = session.createCriteria(Cluster.class)
             .list();
        return list;
    }
    
    public boolean existsCluster(int id) {
        Cluster c = (Cluster) session.createCriteria(Cluster.class)
             .add( Restrictions.eq("id", id) )
                .setMaxResults(1).uniqueResult();
        if( c != null )
            return true;
        return false;           
    }
}
