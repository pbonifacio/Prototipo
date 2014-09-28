package com.app.plan2see.db.controller;

import com.app.plan2see.db.icontroller.IDBAccess;
import java.util.List;
import org.hibernate.Session;

public class DBController implements IDBAccess {

    protected Session session;
    

    public DBController(Session session) {
        this.session = session;
    }

    public void insertEntity(Object entity) {
        session.persist(entity);
    }

    public void removeEntity(Object entity) {
        session.delete(entity);
    }

    public List getAllEntities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getEntityById(int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getEntityByColumnFilter(String columnName, String columnValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getEntitiesCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void refresh() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void closeSession() {
        session.close();
    }
}
