package com.app.plan2see.web.controllers;

import com.app.plan2see.db.controller.ClientsController;
import com.app.plan2see.db.controller.ClusteringHistoryController;
import com.app.plan2see.db.controller.ClusteringTermsController;
import com.app.plan2see.db.controller.ClustersController;
import com.app.plan2see.db.controller.EventsController;
import com.app.plan2see.db.controller.EventsTermsController;
import com.app.plan2see.db.controller.HibernateUtil;
import com.app.plan2see.db.controller.TTestController;
import com.app.plan2see.db.controller.TermsController;
import com.app.plan2see.model.db.Client;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class AppDBControllers {

    ClientsController clients = null;
    EventsController events = null;
    TermsController terms = null;
    EventsTermsController eventsTerms = null;
    ClusteringHistoryController clustering = null;
    ClusteringTermsController clusteringTerms = null;
    ClustersController clustersController = null;
    TTestController ttestController = null;
    private HibernateUtil utils;
    
    public AppDBControllers(){
        utils = new HibernateUtil();
        SessionFactory sessionFactory = utils.getSessionFactory();
        Session session = sessionFactory.openSession();
        clients = new ClientsController(session);
        events = new EventsController(session);
        terms = new TermsController(session);
        eventsTerms = new EventsTermsController(session);
        clustering = new ClusteringHistoryController(session);
        clusteringTerms = new ClusteringTermsController(session);
        clustersController = new ClustersController(session);
        ttestController = new TTestController(session);
    }
    
    public TTestController getTTestController(){
        return ttestController;
    }

    public TermsController getTerms() {
        return terms;
    }

    public ClientsController getClients() {
        return clients;
    }

    public EventsTermsController getEventsTerms() {
        return eventsTerms;
    }

    public EventsController getEvents() {
        return events;
    }

    public void commitChoosedEvents(Client client){
        clients.updateEntity(client);
    }

    public ClusteringHistoryController getClustering() {
        return clustering;
    }

    public ClusteringTermsController getClusteringTerms() {
        return clusteringTerms;
    }

    public ClustersController getClustersController() {
        return clustersController;
    }

    public TTestController getTtestController() {
        return ttestController;
    }
    
    public void closeAll(){
        clients.closeSession();
        events.closeSession();
        terms.closeSession();
        eventsTerms.closeSession();
        clustering.closeSession();
        clusteringTerms.closeSession();
    }
}
