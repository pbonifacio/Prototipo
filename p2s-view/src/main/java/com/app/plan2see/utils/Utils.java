package com.app.plan2see.utils;

import com.app.plan2see.db.controller.EventsController;
import com.app.plan2see.db.controller.EventsTermsController;
import com.app.plan2see.db.controller.TermsController;
import com.app.plan2see.web.controllers.AppDBControllers;
import java.util.List;
import org.apache.log4j.Logger;

public class Utils {
    
    protected Logger log = Logger.getLogger(StatisticsUtil.class);
    protected TermsController termsController;
    protected EventsController eventsController;
    protected EventsTermsController eventsTermsController;
    
    public Utils(){
        AppDBControllers controllers = new AppDBControllers();
        termsController = controllers.getTerms();
        eventsController = controllers.getEvents();
        eventsTermsController = controllers.getEventsTerms();        
    }
    
    protected double getMeanValue(List<Integer> list) {
        double mean = 0;
        String prettyList = "";
        for (int freq : list) {
            mean += freq;
            prettyList += freq+",";
        }
        log.debug(prettyList.substring(0, prettyList.length()-1));
        return (mean / list.size());
    }
}
