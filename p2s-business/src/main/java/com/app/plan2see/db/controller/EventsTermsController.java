package com.app.plan2see.db.controller;

import com.app.plan2see.model.db.EventTerm;
import org.hibernate.Session;

public class EventsTermsController extends DBController {
    
    public EventsTermsController(Session session){
        super(session);
    }
    
    public void persistEntity(EventTerm et) {
        session.beginTransaction();
        session.persist(et);
        session.getTransaction().commit();
    }
    
    @Override
    public EventTerm getEntityById(int id) {
        EventTerm et = (EventTerm) session.get(EventTerm.class, id);
        return et;
    }

    public EventTerm getEventTermByCombination(int idxEvent, int idxTerm) {
        EventTerm et = (EventTerm) session.createQuery("from EventTerm where idevent="+idxEvent+
                " and idterm="+idxTerm).uniqueResult();
        return et;
    }
}
