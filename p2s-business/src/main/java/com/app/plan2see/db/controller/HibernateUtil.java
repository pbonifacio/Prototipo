package com.app.plan2see.db.controller;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private final SessionFactory sessionFactory = buildSessionFactory();

    public SessionFactory buildSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        SessionFactory sf = configuration.buildSessionFactory();
        return sf;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
