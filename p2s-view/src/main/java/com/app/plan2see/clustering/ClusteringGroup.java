package com.app.plan2see.clustering;

import static com.app.plan2see.clustering.DynamicClustering.log;
import com.app.plan2see.model.db.Client;
import com.app.plan2see.model.db.Cluster;
import com.app.plan2see.model.db.ClusteringHistory;
import com.app.plan2see.model.db.Event;
import com.app.plan2see.model.db.TTestTerm;
import com.app.plan2see.model.db.Term;
import com.app.plan2see.utils.TTestUtil;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ClusteringGroup extends DynamicClustering {

    List<Event> selectedEvents = new ArrayList<Event>();
    List<Integer> allClustersSelected = new ArrayList<Integer>();
    List<Event> allDBEvents = events.getAllEntities();
    List<Integer> allClusters = new ArrayList<Integer>();

    public void checkGroupings() {
        log.info("START EVALUATE GROUP");

        setSelectedClustersAndEvents();
        setAllClusters();

        for (int clusterOne : allClustersSelected) {

            HashMap<Integer, Double> pValues = new HashMap<Integer, Double>();

            int choosedSize = 0;
            for (Event eOne : selectedEvents) {
                if (clusterOne == eOne.getCluster()) {
                    choosedSize++;
                }
            }
            List<Event> clusterOneEvents = new ArrayList<Event>();
            for (Event e : allDBEvents) {
                log.debug("CLUSTER ONE EVENTS CLUSTER = " + e.getCluster());
                if (e.getCluster() == clusterOne) {
                    clusterOneEvents.add(e);
                }
            }
            int clusterSize = clusterOneEvents.size();
            double percentageChoosed = 0;
            if (choosedSize > 0) {
                percentageChoosed = choosedSize * 100 / clusterSize;
            }
            log.info("GROUP => CLUSTER " + clusterOne + " PERCENTAGE CHOOSED = " + percentageChoosed);
            if (percentageChoosed < 25) {
                continue;
            }
            log.debug("CLUSTER " + clusterOne + " SIZE = " + clusterOneEvents.size());
            for (int clusterTwo : allClusters) {

                if (clusterOne == clusterTwo) {
                    continue;
                }

                List<Event> clusterTwoEvents = new ArrayList<Event>();
                for (Event e : allDBEvents) {
                    log.debug("CLUSTER TWO EVENTS CLUSTER = " + e.getCluster());
                    if (e.getCluster() == clusterTwo) {
                        clusterTwoEvents.add(e);
                    }
                }
                log.debug("CLUSTER " + clusterTwo + " SIZE = " + clusterTwoEvents.size());

                tTest = new TTestUtil();
                double value = tTest.testUnequalMeansWithPValue(clusterOneEvents, clusterTwoEvents, "group");
                if (value >= TTestUtil.ALPHA) {
                    pValues.put(clusterTwo,
                            value);
                } else {
                    log.info("DON'T GROUP CLUSTERS = " + clusterOne + " AND " + clusterTwo);
                }
            }

            //check the highest pValue
            Iterator<Integer> it = pValues.keySet().iterator();
            double pValueMin = TTestUtil.ALPHA;
            int clusterTwo = clusterOne;
            while (it.hasNext()) {
                log.info("CLUSTER ONE = " + clusterOne);
                int cluster = it.next();
                double pValue = pValues.get(cluster);
                log.info("CLUSTER TWO = " + clusterTwo + " pVALUE = " + pValue);
                if (pValue >= pValueMin) {
                    clusterTwo = cluster;
                    pValueMin = pValue;
                }
            }
            if (clusterTwo != clusterOne) {
                List<Event> allClusterEvents = new ArrayList<Event>();
                for (Event e : allDBEvents) {
                    log.debug("CLUSTER TWO EVENTS CLUSTER = " + e.getCluster());
                    if (e.getCluster() == clusterTwo) {
                        allClusterEvents.add(e);
                    }
                }
                log.info("GROUP CLUSTERS = " + clusterOne + " AND " + clusterTwo);
                groupClusters(clusterOne, clusterTwo, clusterOneEvents, allClusterEvents);
            }
        }
        log.info("FINISH EVALUATE GROUP");
    }

    public void setSelectedClustersAndEvents() {
        for (int i = 0; i < clients.getEntitiesCount(); i++) {
            Client client = clients.getEntityById(i + 1);
            for (Event e : client.getChoosedEvents()) {
                e = events.getEntityById(e.getEventID());
                if (!selectedEvents.contains(e)) {
                    selectedEvents.add(e);
                }
                if (!allClustersSelected.contains(e.getCluster())) {
                    allClustersSelected.add(e.getCluster());
                }
            }
        }
    }

    public void setAllClusters() {
        for (Event e : allDBEvents) {
            int cluster = e.getCluster();
            if (!allClusters.contains(cluster)) {
                allClusters.add(cluster);
            }
        }
    }

    public void groupClusters(int c1, int c2,
            List<Event> clusterOneEvents, List<Event> clusterTwoEvents) {
        while (true) {
            if (!dbControllers.getClustering().existsEntity(iteration)) {
                break;
            }
            iteration++;
        }
        int newClusterId = c1 <= c2 ? c1 : c2;
        int removedClusterId = c1 <= c2 ? c2 : c1;
        String action = "GROUP CLUSTERS " + c1 + " AND " + c2 + " INTO CLUSTER " + newClusterId;
        log.info("STARTING GROUP CLUSTERS " + c1 + " AND " + c2 + " INTO CLUSTER " + newClusterId);
        Date now = new java.sql.Date(Calendar.getInstance().getTimeInMillis());

        log.debug("CLUSTER ONE SIZE = " + clusterOneEvents.size());
        log.debug("CLUSTER TWO SIZE = " + clusterTwoEvents.size());
        List<Event> newClusterEvents = new ArrayList<Event>();
        newClusterEvents.addAll(clusterOneEvents);
        newClusterEvents.addAll(clusterTwoEvents);

        int size = 0;
        for (Event event : newClusterEvents) {
            event.setCluster(newClusterId);
            dbControllers.getEvents().updateEntity(event);
            ClusteringHistory newCluster = new ClusteringHistory(newClusterId);
            newCluster.setIdevent(event.getEventID());
            newCluster.setTimestamp(now);
            newCluster.setAction(action);
            newCluster.setId_Count_history(iteration);
            String one = "";
            for (Term s : tTest.getRelevantTerms()) {
                one += s.getTerm() + ";";
            }
            one = one.substring(0, one.length() - 1);
            newCluster.setRelevant_terms(one);
            dbControllers.getClustering().insertEntity(newCluster);
            size++;
        }

        String one = "";
        for (Term s : tTest.getRelevantTermsOne()) {
            one += s.getTerm() + ";";
        }
        one = one.substring(0, one.length() - 1);
        Cluster newDBCluster = new Cluster();
        newDBCluster.setClusterSize(size);
        newDBCluster.setId(newClusterId);
        newDBCluster.setRelevantTerms(one);
        dbControllers.getClustersController().updateEntity(newDBCluster);
        Cluster removedCluster = new Cluster();
        removedCluster.setId(removedClusterId);
        dbControllers.getClustersController().removeEntity(removedCluster);

        for (Term term : tTest.gettTestTermsOne()) {
            TTestTerm ttest = new TTestTerm();
            ttest.setId_count_history(iteration);
            ttest.setMean(term.getMean());
            ttest.setRelevant_term(term.getTerm());
            ttest.setSubset(1);
            dbControllers.getTTestController().insertEntity(ttest);
        }
        for (Term term : tTest.gettTestTermsTwo()) {
            TTestTerm ttest = new TTestTerm();
            ttest.setId_count_history(iteration);
            ttest.setMean(term.getMean());
            ttest.setRelevant_term(term.getTerm());
            ttest.setSubset(2);
            dbControllers.getTTestController().insertEntity(ttest);
        }
        log.info("FINISHED GROUP CLUSTERS " + c1 + " AND " + c2 + " INTO CLUSTER " + newClusterId);
    }
}
