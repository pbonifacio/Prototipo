package com.app.plan2see.db.controller;

import com.app.plan2see.model.db.ClusteringTerms;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class ClusteringTermsController extends DBController {

    public ClusteringTermsController(Session session) {
        super(session);
    }
    
    public void insertEntity(ClusteringTerms entity) {
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();
    }

    public List<ClusteringTerms> getEntitiesById(int iteration) {
        List<ClusteringTerms> list = session.createCriteria(ClusteringTerms.class)
                .add(Restrictions.eq("id_count_history", iteration))
                .list();
        return list;
    }
}
