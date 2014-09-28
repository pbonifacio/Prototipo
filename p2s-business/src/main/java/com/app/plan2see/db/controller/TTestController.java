package com.app.plan2see.db.controller;

import com.app.plan2see.model.db.TTestTerm;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class TTestController extends DBController {

    public TTestController(Session session) {
        super(session);
    }
    
    public void insertEntity(TTestTerm entity) {
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();
    }
    
    @Override
    public TTestTerm getEntityById(int id) {
        TTestTerm c = (TTestTerm) session.get(TTestTerm.class, id);
        return c;
    }
    
    @Override
    public List<TTestTerm> getAllEntities() {
        List<TTestTerm> list = session.createCriteria(TTestTerm.class)
             .list();
        return list;
    }
    
    public List<TTestTerm> getEntitiesById(int iteration, int subset) {
        List<TTestTerm> list = session.createCriteria(TTestTerm.class)
                .add(Restrictions.eq("id_count_history", iteration))
                .add(Restrictions.eq("subset", subset))
                .list();
        return list;
    }
}
