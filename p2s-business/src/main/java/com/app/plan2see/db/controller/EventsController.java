package com.app.plan2see.db.controller;

import com.app.plan2see.model.db.Event;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class EventsController extends DBController{
    
    public EventsController(Session session){
        super(session);
    }
    
    public void updateEntity(Event e) {
        session.beginTransaction();
        Event event = (Event) session.get(Event.class, e.getEventID());
        if( event.getEventID() == e.getEventID() ){
            event.setCluster(e.getCluster());
        }
        session.merge(event);
        session.getTransaction().commit();
    }
    
    @Override
    public Event getEntityById(int id) {
        Event event = (Event) session.get(Event.class, id);        
        return event;
    }
    
    @Override
    public int getEntitiesCount(){
        int size = session.createQuery("from Event").list().size();
        return size;
    }
    
    @Override
    public List<Event> getAllEntities(){
        return session.createQuery("from Event").list();
    }
    
    public List<Event> getAllEntities(int cluster){
        return session.createCriteria(Event.class)
                .add(Restrictions.eq("cluster", cluster))
                .list();
    }
}
