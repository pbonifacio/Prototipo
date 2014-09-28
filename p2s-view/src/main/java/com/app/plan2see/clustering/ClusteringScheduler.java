package com.app.plan2see.clustering;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimerTask;
import org.apache.log4j.Logger;

public class ClusteringScheduler extends TimerTask{

    static Logger log = Logger.getLogger(ClusteringScheduler.class);
    
    @Override
    public void run(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        log.info("Started Clustering at : "+sdf.format(Calendar.getInstance().getTime()));
        new ClusteringDivide().checkDivisions();
        new ClusteringGroup().checkGroupings();
        log.info("Finished Clustering at : "+sdf.format(Calendar.getInstance().getTime()));
    }
}
