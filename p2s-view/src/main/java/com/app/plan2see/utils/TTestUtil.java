package com.app.plan2see.utils;

import com.app.plan2see.model.db.Event;
import com.app.plan2see.model.db.EventTerm;
import com.app.plan2see.model.db.Term;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.math3.stat.descriptive.AggregateSummaryStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummaryValues;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.TTest;

public class TTestUtil extends Utils {

    List<Term> tTestTermsOne = new ArrayList<Term>();
    List<Term> tTestTermsTwo = new ArrayList<Term>();
    List<Term> relevantTermsOne = new ArrayList<Term>();
    List<Term> relevantTermsTwo = new ArrayList<Term>();
    HashMap<String, List<Integer>> allOne = new HashMap<String, List<Integer>>();
    HashMap<String, List<Integer>> allTwo = new HashMap<String, List<Integer>>();
    List<Term> terms = null;
    List<SummaryStatistics> statsOne = new ArrayList<SummaryStatistics>();
    List<SummaryStatistics> statsTwo = new ArrayList<SummaryStatistics>();
    int size = 0;
    public static final double ALPHA = 0.02;

    public TTestUtil() {
        super();
        relevantTermsOne = new ArrayList<Term>();
        relevantTermsTwo = new ArrayList<Term>();
    }

    public void setNullValues() {
        relevantTermsOne = null;
        relevantTermsTwo = null;
    }

    public List<Term> getRelevantTermsOne() {
        return relevantTermsOne;
    }

    public List<Term> getRelevantTermsTwo() {
        return relevantTermsTwo;
    }

    public List<Term> getRelevantTerms(){
        return terms;
    }

    public List<Term> gettTestTermsOne() {
        return tTestTermsOne;
    }

    public List<Term> gettTestTermsTwo() {
        return tTestTermsTwo;
    }
    
    public boolean testUnequalMeans(List<Event> listOne, List<Event> listTwo, String operation) {

        terms = termsController.getAllEntities();
        setAllTermsFrequencies(listOne, listTwo);

        removeUnunsedTerms(listOne, listTwo);

        setRelevantTerms(terms, allOne, allTwo, operation);

        for (Term term : terms) {
            log.info("FIRST QUARTIL TERM = " + term.getTerm());
        }

        setRelevantTermsMeans();

        debugRelevantTermsMeans();

        StatisticalSummaryValues stats1 = AggregateSummaryStatistics.aggregate(statsOne);
        StatisticalSummaryValues stats2 = AggregateSummaryStatistics.aggregate(statsTwo);
        try {
            TTest t = new TTest();
            return t.tTest(stats1, stats2, ALPHA);

        } catch (IllegalArgumentException ex) {
            java.util.logging.Logger.getLogger(TTestUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public double testUnequalMeansWithPValue(List<Event> listOne, List<Event> listTwo, String operation) {

        terms = termsController.getAllEntities();
        setAllTermsFrequencies(listOne, listTwo);

        removeUnunsedTerms(listOne, listTwo);

        setRelevantTerms(terms, allOne, allTwo, operation);

        for (Term term : terms) {
            log.info("FIRST QUARTIL TERM = " + term.getTerm());
        }

        setRelevantTermsMeans();

        debugRelevantTermsMeans();

        StatisticalSummaryValues stats1 = AggregateSummaryStatistics.aggregate(statsOne);
        StatisticalSummaryValues stats2 = AggregateSummaryStatistics.aggregate(statsTwo);
        try {
            TTest t = new TTest();
            return t.tTest(stats1, stats2);

        } catch (IllegalArgumentException ex) {
            java.util.logging.Logger.getLogger(TTestUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public int setRelevantTerms(List<Term> terms, HashMap<String, List<Integer>> mapOne,
            HashMap<String, List<Integer>> mapTwo, String operation) {
        List<Double> means = new ArrayList<Double>();

        //calculate mean for each terms with both subsets
        for (Term term : terms) {
            double sumOne = 0;
            List<Integer> freqsOne = mapOne.get(term.getTerm());
            for (double f : freqsOne) {
                sumOne += f;
            }
            double sumTwo = 0;
            List<Integer> freqsTwo = mapTwo.get(term.getTerm());
            for (double f : freqsTwo) {
                sumTwo += f;
            }
            means.add((sumOne + sumTwo) / (freqsOne.size() + freqsTwo.size()));
        }

        //get first quartil mean with both subsets
        Collections.sort(means, Collections.reverseOrder());
        int firstQuartil = (means.size() * 5) / 100;
        //int secondQuartil = (means.size() * 50) / 100;

        List<Double> quartilList = new ArrayList<Double>();
        int idx = 0;
        for (idx = 0; idx < firstQuartil; idx++) {
            log.info("FIRST QUARTIL VALUE = " + means.get(idx));
            quartilList.add(means.get(idx));
        }

        log.info("FIRST QUARTIL SIZE = " + idx + " && TERMS SIZE = " + terms.size());

        double minimum = Collections.min(quartilList);

        int removedTerms = 0;
        List<Term> oldTerms = new ArrayList<Term>(terms);
        for (Term term : oldTerms) {
            List<Integer> freqsOne = mapOne.get(term.getTerm());
            List<Integer> freqsTwo = mapTwo.get(term.getTerm());
            List<Integer> freqs = new ArrayList<Integer>(freqsOne);
            freqs.addAll(freqsTwo);
            if (getMeanValue(freqs) < minimum) {
                log.debug("IRRELEVANT TERM = " + term);
                terms.remove(term);
                removedTerms++;
            }
        }
        log.info("IRRELEVANT TERMS SIZE = " + removedTerms);
        return removedTerms;
    }

    public List<Term> setRelevantTerms(HashMap<String, List<Integer>> mappedTerms) {
        HashMap<String, List<Integer>> map = new HashMap<String, List<Integer>>(mappedTerms);
        
        List<Term> result = new ArrayList<Term>();

        List<Double> means = new ArrayList<Double>();
        for (Term term : termsController.getAllEntities()) {
            double sumOne = 0;
            List<Integer> freqsOne = map.get(term.getTerm());
            for (double f : freqsOne) {
                sumOne += f;
            }
            means.add(sumOne / freqsOne.size());
        }

        Collections.sort(means, Collections.reverseOrder());

        List<Double> quartilList = new ArrayList<Double>();
        int idx = 0;
        for (idx = 0; idx < 5; idx++) {
            quartilList.add(means.get(idx));
        }
        double minimum = Collections.min(quartilList);

        for (Term term : termsController.getAllEntities()) {
            List<Integer> freqsOne = map.get(term.getTerm());
            List<Integer> freqs = new ArrayList<Integer>(freqsOne);
            if (getMeanValue(freqs) < minimum) {
                log.debug("IRRELEVANT TERM = " + term);
                map.remove(term.getTerm());
            }
        }
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String s = it.next();
            for (Term t : termsController.getAllEntities()) {
                if (s.equals(t.getTerm())) {
                    t.setMean(getMeanValue(map.get(s)));
                    result.add(t);
                    break;
                }
            }
        }
        return result;
    }

    public void setAllTermsFrequencies(List<Event> listOne, List<Event> listTwo) {
        size = termsController.getEntitiesCount();
        int idxTerm = 0;
        log.info("TERMS SIZE = " + size);
        while (idxTerm < size) {
            log.debug("IDX_TERM = " + idxTerm);
            List<Integer> afs = new ArrayList<Integer>();
            int idxEvent = 1;
            Term term = terms.get(idxTerm);
            while ((idxEvent - 1) < listOne.size()) {
                log.debug("IDX_EVENT_ONE = " + idxEvent);
                EventTerm et = eventsTermsController.getEventTermByCombination(
                        listOne.get(idxEvent - 1).getEventID(), term.getIdterm());
                afs.add(new Integer(et.getFrequency()));
                idxEvent++;
            }
            allOne.put(term.getTerm(), new ArrayList<Integer>(afs));
            afs = new ArrayList<Integer>();
            idxEvent = 1;
            while ((idxEvent - 1) < listTwo.size()) {
                log.debug("IDX_EVENT_TWO = " + idxEvent);
                EventTerm et = eventsTermsController.getEventTermByCombination(
                        listTwo.get(idxEvent - 1).getEventID(), term.getIdterm());
                afs.add(new Integer(et.getFrequency()));
                idxEvent++;
            }
            allTwo.put(term.getTerm(), new ArrayList<Integer>(afs));
            idxTerm++;
        }
    }

    public List<Term> removeUnunsedTerms(List<Event> listOne, List<Event> listTwo) {
        int unusedTerms = 0;
        int idxUnusedTerm = 0;
        List<Term> newTerms = new ArrayList<Term>(terms);
        while (idxUnusedTerm < size) {
            log.debug("IDX_UNUNSED_TERM = " + idxUnusedTerm);
            String key = terms.get(idxUnusedTerm).getTerm();
            boolean hasAnyOne = false;
            boolean hasAnyTwo = false;
            int idxOne = 1;
            log.debug("LIST_ONE SIZE = " + listOne.size());
            while ((idxOne - 1) < listOne.size()) {
                log.debug("IDX_EVENT_ONE = " + idxOne);
                EventTerm et = eventsTermsController.getEventTermByCombination(
                        listOne.get(idxOne - 1).getEventID(), terms.get(idxUnusedTerm).getIdterm());
                if (et.getFrequency() > 0) {
                    hasAnyOne = true;
                }
                idxOne++;
            }
            int idxTwo = 1;
            while ((idxTwo - 1) < listTwo.size()) {
                log.debug("IDX_EVENT_TWO = " + idxTwo);
                EventTerm et = eventsTermsController.getEventTermByCombination(
                        listTwo.get(idxTwo - 1).getEventID(), terms.get(idxUnusedTerm).getIdterm());
                if (et.getFrequency() > 0) {
                    hasAnyTwo = true;
                }
                idxTwo++;
            }
            //term does not occurs in one or both subsets
            //if (!hasAnyOne || !hasAnyTwo) {

            //term does not occurs in both subsets
            if (!hasAnyOne && !hasAnyTwo) {
                log.debug("Irrelevant TERM = " + key);
                newTerms.remove(terms.get(idxUnusedTerm));
                unusedTerms++;
            }
            idxUnusedTerm++;
        }
        terms = newTerms;
        log.info("UNUNSED TERMS SIZE = " + unusedTerms);
        return terms;
    }

    public void setRelevantTermsMeans() {
        relevantTermsOne = setRelevantTerms(allOne);
        relevantTermsTwo = setRelevantTerms(allTwo);
        
        for( int i = 0; i < terms.size(); i++ ){
            statsOne.add(new SummaryStatistics());
            statsTwo.add(new SummaryStatistics());
        }
        
        for(int i = 0; i < terms.size(); i++){
            Term term = terms.get(i);
            for( int d : allOne.get(term.getTerm()) ){
                statsOne.get(i).addValue(d);
            }
            for( int d : allTwo.get(term.getTerm()) ){
                statsTwo.get(i).addValue(d);
            }
        }
        
        for(int i = 0; i < terms.size(); i++){
            Term t1 = new Term();
            t1.setTerm(terms.get(i).getTerm());
            t1.setMean(statsOne.get(i).getMean());
            tTestTermsOne.add(t1);
            Term t2 = new Term();
            t2.setTerm(terms.get(i).getTerm());
            t2.setMean(statsTwo.get(i).getMean());
            tTestTermsTwo.add(t2);
        }
    }

    public void debugRelevantTermsMeans() {
        for (int i = 0; i < terms.size(); i++) {
            Term term = terms.get(i);
            log.info("TERM(" + i + ") is " + term.getTerm());
            log.info("Subset 1 = " + statsOne.get(i).getMean()
                    + " Subset 2 = " + statsTwo.get(i).getMean());
        }
    }
}
