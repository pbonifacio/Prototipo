package com.app.plan2see.clustering;

import static com.app.plan2see.clustering.DynamicClustering.log;
import com.app.plan2see.model.db.Client;
import com.app.plan2see.model.db.Cluster;
import com.app.plan2see.model.db.ClusteringHistory;
import com.app.plan2see.model.db.Event;
import com.app.plan2see.model.db.TTestTerm;
import com.app.plan2see.model.db.Term;
import com.app.plan2see.utils.StatisticsUtil;
import com.app.plan2see.utils.TTestUtil;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ClusteringDivide extends DynamicClustering {

    List<Event> selectedEvents = null;
    List<Integer> selectedClusters = null;

    public void checkDivisions() {
        log.info("START EVALUATE DIVIDE");

        selectedEvents = getSelectedEvents();
        selectedClusters = getSelectedClusters();
        List<Integer> allClusters = new ArrayList<Integer>(selectedClusters);

        for (int cluster : allClusters) {
            int choosedSize = 0;
            List<Event> userEvents = new ArrayList<Event>();
            for (Event eOne : selectedEvents) {
                if (cluster == eOne.getCluster()) {
                    choosedSize++;
                    userEvents.add(eOne);
                }
            }
            List<Event> clusterEvents = new ArrayList<Event>();
            List<Event> allDBEvents = events.getAllEntities();
            for (Event e : allDBEvents) {
                if (e.getCluster() == cluster) {
                    clusterEvents.add(e);
                }
            }
            int clusterSize = clusterEvents.size();
            double percentageChoosed = 0;
            if (choosedSize > 0) {
                percentageChoosed = choosedSize * 100 / clusterSize;
            }
            log.info("DIVIDE => CLUSTER " + cluster + " PERCENTAGE CHOOSED = " + percentageChoosed);
            if (percentageChoosed < 5 || percentageChoosed > 60) {
                continue;
            }
            log.debug("CLUSTER " + cluster + " SIZE = " + clusterEvents.size());
            tTest = new TTestUtil();
            if (tTest.testUnequalMeans(userEvents, clusterEvents, "divide")) {
                log.info("DIVIDE CLUSTER = " + cluster);
                divideCluster(cluster, userEvents, clusterEvents);
            } else {
                log.info("DON'T DIVIDE CLUSTER = " + cluster);
            }
        }

        log.info("FINISH EVALUATE DIVIDE");
    }

    public List<Event> getSelectedEvents() {
        List<Event> selectedEvents = new ArrayList<Event>();
        for (int i = 0; i < clients.getEntitiesCount(); i++) {
            Client client = clients.getEntityById(i + 1);
            for (Event e : client.getChoosedEvents()) {
                if (!selectedEvents.contains(e)) {
                    selectedEvents.add(e);
                }
            }
        }
        return selectedEvents;
    }

    public List<Integer> getSelectedClusters() {
        List<Integer> selectedClusters = new ArrayList<Integer>();
        for (Event e : selectedEvents) {
            e = events.getEntityById(e.getEventID());
            int cluster = e.getCluster();
            if (!selectedClusters.contains(cluster)) {
                selectedClusters.add(cluster);
            }
        }
        return selectedClusters;
    }

    public void divideCluster(int clusterId, List<Event> usersEvents, List<Event> clusterEvents) {
        while (true) {
            if (!dbControllers.getClustering().existsEntity(iteration)) {
                break;
            }
            iteration++;
        }
        int newClusterId = 1;
        while (true) {
            newClusterId++;
            if (!dbControllers.getClustersController().existsCluster(newClusterId)) {
                break;
            }
        }
        String action = "DIVIDE CLUSTER " + clusterId + " INTO CLUSTERS " + clusterId + " AND " + newClusterId;
        log.info("STARTING DIVIDE CLUSTER " + clusterId + " INTO CLUSTERS " + clusterId + " AND " + newClusterId);
        HashMap<String, Double> userCentroid = calculateCentroid(usersEvents);
        HashMap<String, Double> clusterCentroid = calculateCentroid(clusterEvents);
        Date now = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
        log.debug("CLUSTER SIZE = " + clusterEvents.size());
        int sizeOne = 0;
        int sizeTwo = 0;
        String one = "";
        String two = "";
        StatisticsUtil utils = new StatisticsUtil();
        utils.setRelevantTerms(tTest.getRelevantTerms());
        for (Event event : clusterEvents) {
            HashMap<String, Double> eventCentroid = utils.calculateCentroid(event);
            double distanceToUser = utils.getEuclideanDistance(userCentroid, eventCentroid);
            double distanceToCentroid = utils.getEuclideanDistance(clusterCentroid, eventCentroid);
            int id = distanceToCentroid >= distanceToUser ? 0 : 1;
            event.setCluster(id == 0 ? newClusterId : clusterId);
            dbControllers.getEvents().updateEntity(event);
            ClusteringHistory[] newCluster = {new ClusteringHistory(newClusterId), new ClusteringHistory(clusterId)};
            newCluster[id].setIdevent(event.getEventID());
            newCluster[id].setTimestamp(now);
            newCluster[id].setAction(action);
            newCluster[id].setId_Count_history(iteration);
            if (one.equals("") || two.equals("")) {
                for (Term s : tTest.getRelevantTermsOne()) {
                    one += s.getTerm() + ";";
                }
                one = one.substring(0, one.length() - 1);
                for (Term s : tTest.getRelevantTermsTwo()) {
                    two += s.getTerm() + ";";
                }
                two = two.substring(0, two.length() - 1);

            }
            if (id == 0) {
                sizeOne++;
                newCluster[id].setRelevant_terms(one);
            } else {
                sizeTwo++;
                newCluster[id].setRelevant_terms(two);
            }
            dbControllers.getClustering().insertEntity(newCluster[id]);
        }

        Cluster newDBClusterOne = new Cluster();
        newDBClusterOne.setId(newClusterId);
        newDBClusterOne.setClusterSize(sizeOne);
        newDBClusterOne.setRelevantTerms(one);
        Cluster newDBClusterTwo = new Cluster();
        newDBClusterTwo.setId(clusterId);
        newDBClusterTwo.setClusterSize(sizeTwo);
        newDBClusterTwo.setRelevantTerms(two);
        dbControllers.getClustersController().updateEntity(newDBClusterTwo);
        dbControllers.getClustersController().insertEntity(newDBClusterOne);

        /*for (Term term : tTest.getRelevantTerms()) {
         log.debug("CLUSTERING RELEVANT TERM " + term.getTerm());
         ClusteringTerms clusteringTerms = new ClusteringTerms();
         clusteringTerms.setId_count_history(iteration);
         clusteringTerms.setTerm(term);
         clusteringTerms.setOccurences(occurences.get(term.getTerm()));
         dbControllers.getClusteringTerms().insertEntity(clusteringTerms);
         }*/
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
        log.info("FINISHED DIVIDE CLUSTER " + clusterId + " INTO CLUSTERS " + clusterId + " AND " + newClusterId);
    }
}
