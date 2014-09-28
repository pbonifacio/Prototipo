package com.app.plan2see.db.icontroller;

import java.util.List;

public interface IDBAccess<T extends Object> { 
    public abstract void insertEntity(T entity);
    public abstract void removeEntity(T entity);
    public abstract List<T> getAllEntities();
    public abstract T getEntityById(int id);
    public abstract T getEntityByColumnFilter(String columnName, String columnValue);
    public abstract int getEntitiesCount();
    public abstract void refresh();
}
