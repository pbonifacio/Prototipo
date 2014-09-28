package com.app.plan2see.utils;

import com.app.plan2see.model.db.Cluster;
import com.app.plan2see.model.db.Event;
import com.app.plan2see.model.db.EventTerm;
import com.app.plan2see.model.db.Term;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class StatisticsUtil extends Utils{
    
    List<Term> relevantTerms = new ArrayList<Term>();
    
    public StatisticsUtil(){
        super();
    }

    public List<Term> getRelevantTerms() {
        return relevantTerms;
    }

    public void setRelevantTerms(List<Term> relevantTerms) {
        this.relevantTerms = relevantTerms;
    }
    
    public double getEuclideanDistance(HashMap<String, Double> centroid1, HashMap<String, Double> centroid2) {
        double distance = 0;
        Iterator<String> it;
        if (centroid1.keySet().size() >= centroid2.keySet().size()) {
            it = centroid1.keySet().iterator();
        } else {
            it = centroid2.keySet().iterator();
        }
        while (it.hasNext()) {
            String term = it.next();
            if (centroid1.get(term) == null || centroid2.get(term) == null) {
                continue;
            }
            double frequency1 = centroid1.get(term);
            double frequency2 = centroid2.get(term);
            double diff = frequency1 - frequency2;
            distance += Math.pow(diff, 2);
        }
        distance = Math.sqrt(distance);
        return distance;
    }

    public double getAfVarianceValue(List<Integer> af) {
        double mean = getMeanValue(af);
        double variance = 0;
        String prettyList = "";
        for (int i = 0; i < af.size(); i++) {
            variance += Math.pow(af.get(i) - mean, 2);
            prettyList += variance+",";
        }
        log.debug(prettyList.substring(0, prettyList.length()-1));
        return variance;
    }
    
    public HashMap<String, Double> calculateCentroidForTable(Event event) {
        HashMap<String, Double> allTerms = new HashMap<String, Double>();
        List<Term> terms = termsController.getAllEntities();
        for (int i = 0; i < terms.size(); i++) {
            Term key = terms.get(i);
            EventTerm et = eventsTermsController.getEventTermByCombination(
                    event.getEventID(), key.getIdterm());
            double frequency = et.getFrequency();
            allTerms.put(key.getTerm(), frequency);
        }
        return allTerms;
    }
    
    public HashMap<String, Double> calculateCentroidForTable(Cluster cluster) {
        HashMap<String, Double> allTerms = new HashMap<String, Double>();
        List<Term> terms = termsController.getAllEntities();
        for (int i = 0; i < terms.size(); i++) {
            Term key = terms.get(i);
            double sumTerm = 0;
            int size = 0;
            for (Event event : eventsController.getAllEntities(cluster.getId())) {
                EventTerm et = eventsTermsController.getEventTermByCombination(
                        event.getEventID(), key.getIdterm());
                sumTerm += et.getFrequency();
                size++;
            }
            double mean = sumTerm / size;
            allTerms.put(key.getTerm(), mean);
        }
        return allTerms;
    }
    
    public HashMap<String, Double> calculateCentroid(Event event) {
        HashMap<String, Double> allTerms = new HashMap<String, Double>();
        List<Term> terms = getRelevantTerms();
        for (int i = 0; i < terms.size(); i++) {
            Term key = terms.get(i);
            EventTerm et = eventsTermsController.getEventTermByCombination(
                    event.getEventID(), key.getIdterm());
            double frequency = et.getFrequency();
            allTerms.put(key.getTerm(), frequency);
        }
        return allTerms;
    }
    
    public HashMap<String, Double> calculateCentroid(Cluster cluster) {
        HashMap<String, Double> allTerms = new HashMap<String, Double>();
        List<Term> terms = getRelevantTerms();
        for (int i = 0; i < terms.size(); i++) {
            Term key = terms.get(i);
            double sumTerm = 0;
            int size = 0;
            for (Event event : eventsController.getAllEntities(cluster.getClusterid())) {
                EventTerm et = eventsTermsController.getEventTermByCombination(
                        event.getEventID(), key.getIdterm());
                sumTerm += et.getFrequency();
                size++;
            }
            double mean = sumTerm / size;
            allTerms.put(key.getTerm(), mean);
        }
        return allTerms;
    }
}
