package com.app.plan2see.clustering;

import com.app.plan2see.db.controller.ClientsController;
import com.app.plan2see.db.controller.EventsController;
import com.app.plan2see.model.db.Event;
import com.app.plan2see.model.db.EventTerm;
import com.app.plan2see.model.db.Term;
import com.app.plan2see.utils.TTestUtil;
import com.app.plan2see.web.controllers.AppDBControllers;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

public class DynamicClustering{

    protected static Logger log = Logger.getLogger(ClusteringDivide.class);
    protected ClientsController clients = null;
    protected EventsController events = null;
    protected TTestUtil tTest = new TTestUtil();
    protected AppDBControllers dbControllers = null;
    int iteration = 1;
    
    public DynamicClustering(){
        if( dbControllers == null ){ 
            dbControllers = new AppDBControllers();
        }
        clients = dbControllers.getClients();
        events = dbControllers.getEvents();
    }

    HashMap<String, Double> calculateCentroid(List<Event> events) {
        HashMap<String, Double> allTerms = new HashMap<String, Double>();
        List<Term> terms = tTest.getRelevantTerms();
        for (int i = 0; i < terms.size(); i++) {
            Term key = terms.get(i);
            double sumTerm = 0;
            for (Event event : events) {
                EventTerm et = dbControllers.getEventsTerms().getEventTermByCombination(
                        event.getEventID(), key.getIdterm());
                sumTerm += et.getFrequency();
            }
            double mean = sumTerm / events.size();
            allTerms.put(key.getTerm(), mean);
        }
        return allTerms;
    }
}
