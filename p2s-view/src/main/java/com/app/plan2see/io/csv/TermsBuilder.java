package com.app.plan2see.io.csv;

import com.app.plan2see.model.db.Event;
import com.app.plan2see.model.db.EventTerm;
import com.app.plan2see.web.controllers.AppDBControllers;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TermsBuilder {
    
    private FileInputStream groups;
    private FileInputStream termsFrequencies;
    Scanner scanner = null;
    ArrayList<Integer> events = new ArrayList<Integer>();
    HashMap<Integer,List<Integer>> frequencies = new HashMap<Integer, List<Integer>>();
    
    public TermsBuilder(){
        try {
            groups = new FileInputStream(new File("/home/pbonifacio/Desktop/groups.csv"));
            termsFrequencies = new FileInputStream(new File("/home/pbonifacio/Desktop/termsFrequencies.csv"));
            
            scanner = new Scanner(groups);
            while( scanner.hasNext() ){
                Integer i = Integer.parseInt(scanner.next());
                events.add(i);
            }
            
            scanner = new Scanner(termsFrequencies);
            int i = 1;
            while( scanner.hasNext() ){
                String line = scanner.next();
                String[] data = line.split(";");
                ArrayList<Integer> occurences = new ArrayList<Integer>();
                for(String s : data ){
                    occurences.add(Double.valueOf(s).intValue());
                }
                if( events.contains(i) ){
                    frequencies.put(i, occurences);
                }
                i++;
            }
            System.out.println(frequencies.keySet().size());
            
            i = 1;
            Iterator<Integer> it = frequencies.keySet().iterator();
            AppDBControllers controllers = new AppDBControllers();
            while( it.hasNext() ){
                int event = it.next();
                Event e = controllers.getEvents().getEntityById(event);
                int idxTerm = 1;
                System.out.println(e.getEventID());
                for( int freq : frequencies.get(event) ){
                    EventTerm et = new EventTerm();
                    et.setEvent(e);
                    et.setIdeventterm(i);
                    et.setTerm(controllers.getTerms().getEntityById(idxTerm));
                    et.setFrequency(freq);
                    controllers.getEventsTerms().persistEntity(et);
                    idxTerm++;
                    i++;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(TermsBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            scanner.close();
        }        
    }
}
