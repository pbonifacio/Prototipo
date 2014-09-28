package com.app.plan2see.web.servlet;

import com.app.plan2see.clustering.ClusteringScheduler;
import com.app.plan2see.configuration.ConfigurationProperties;
import com.app.plan2see.web.controllers.AppDBControllers;
import java.util.Timer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.apache.log4j.Logger;

public class Initializer extends HttpServlet {

    static Logger log = Logger.getLogger(Initializer.class);
    private static final long serialVersionUID = -8409798536222402345L;
    ConfigurationProperties confProperties = new ConfigurationProperties();
    static AppDBControllers controllers = new AppDBControllers();
    boolean isClusteringRunning = false;

    @Override
    public void init() throws ServletException {
        /*int delay = 2000;   // delay for 0 seconds
        int period = 1 * 60 * 60 * 1000;  // repeat every 5 minutes
        Timer timer = new Timer();
        timer.schedule(new ClusteringScheduler(), delay, period);
        log.info("Created Scheduler");*/
    }

    public boolean isClusteringRunning() {
        return isClusteringRunning;
    }

    public void setIsClusteringRunning(boolean isClusteringRunning) {
        this.isClusteringRunning = isClusteringRunning;
    }

    /*public void buildDBClusters() {
     Properties props = System.getProperties();
     try {
     props.load(this.getServletContext().getResourceAsStream("/resources/configuration/conf.properties"));
     confProperties.setProperties(props);
     //ApplicationImpl.writeXML();
     //ApplicationUtil.initializeData("it");
     //ApplicationUtil.readData();
     RapidMinerImpl miner = new RapidMinerImpl(confProperties.getRAPIDMINER_HOME(),
     confProperties.getHOME());
     miner.buildDBClusters();
     } catch (IOException e) {
     e.printStackTrace();
     }
     }

     public void startEventsCrawler() {

     int MAX_PAGES = 70;

     EscapeChars chars = new EscapeChars();

     //GenericSource sourceOne = new ArtsSource(cfg.getProperty("EVENTS_7"),
     //		"Cultural", null, MAX_PAGES, db, chars);
     //GenericSource sourceTwo = new NatureSource(cfg.getProperty("EVENTS_5"),
     //        "Nature.com", null, MAX_PAGES, db, chars);
     GenericSource sourceIT = new ITEventsSource("http://itevent.net/tag/free", "ITEvents.net", null, MAX_PAGES, new EventsController(), chars);
     //sourceOne.start();
     sourceIT.start();
     //sourceThree.start();
     try {
     //sourceOne.join();
     sourceIT.join();
     //sourceThree.join();
     } catch (InterruptedException e) {
     e.printStackTrace();
     }
     }*/
    public static AppDBControllers getControllers() {
        return controllers;
    }
}
