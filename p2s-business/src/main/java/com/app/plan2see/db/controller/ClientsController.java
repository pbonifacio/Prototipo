package com.app.plan2see.db.controller;

import com.app.plan2see.model.db.Client;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class ClientsController extends DBController {

    public ClientsController(Session session) {
        super(session);
    }
    
    public void updateEntity(Client c) {
        session.beginTransaction();
        Client client = (Client) session.get(Client.class, c.getClientID());
        if( client.getClientID() == c.getClientID() ){
            client.setChoosedEvents(c.getChoosedEvents());
        }
        session.persist(c);
        session.getTransaction().commit();
    }
    
    @Override
    public Client getEntityById(int id) {
        Client client = (Client) session.get(Client.class, id);
        return client;
    }
    
    public Client getClientLogin(String username, String password) {
        Client c = (Client) session.createCriteria(Client.class)
             .add( Restrictions.like("username", username) )
                .setMaxResults(1).uniqueResult();
        if( c == null)
            return null;
        if (c.getUsername().equals(username)
                && c.getPassword().equals(password)) {
            return c;
        }
        return null;
    }

    @Override
    public Client getEntityByColumnFilter(String columnName, String columnValue) {
        Client client = (Client) session.createCriteria(Client.class)
             .add( Restrictions.eq(columnName, columnValue) )
                .setMaxResults(1).uniqueResult();
        return client;
    }

    @Override
    public int getEntitiesCount(){
        int size = session.createQuery("from Client").list().size();
        return size;
    }
}
