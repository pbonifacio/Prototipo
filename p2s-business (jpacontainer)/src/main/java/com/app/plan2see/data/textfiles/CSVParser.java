package com.app.plan2see.data.textfiles;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import org.apache.log4j.Logger;

public class CSVParser {
    
    private InputStream csvFile;
    private HashMap<Integer,ArrayList<Integer>> data;
    private HashMap<Integer,ArrayList<String>> data2;
    private ArrayList<String> attributes;
    private ArrayList<Integer> eventIds;
    private ArrayList<Integer> clusterIds;
    private ArrayList<Integer> ids;
    private int nLines;
    
    static Logger log = Logger.getLogger(CSVParser.class);
    /*public CSVParser(InputStream stream){
        this.csvFile = stream;
        data = new HashMap<Integer,ArrayList<Integer>>();
        attributes = new ArrayList<String>();
        eventIds = new ArrayList<Integer>();
        clusterIds = new ArrayList<Integer>();
        ids = new ArrayList<Integer>();
        try{
            readFile();
        }catch(Exception e){
            e.printStackTrace();
        }
    }*/
    
    public CSVParser(FileInputStream stream){
        this.csvFile = stream;
        data2 = new HashMap<Integer,ArrayList<String>>();
        attributes = new ArrayList<String>();
        try{
            readFile2();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private void readFile() throws IOException{
        Scanner scanner = new Scanner(csvFile,"UTF-8");
        String line;
        nLines = 0;
        try{
            line = scanner.nextLine();
            for( String s : line.split(";") ){
                attributes.add(s.replace("\"",""));
            }
            int indexClusterID = attributes.indexOf("label");
            int indexID = attributes.indexOf("id");
            int indexEventID = attributes.indexOf("eventID");
            attributes.remove(indexClusterID);
            attributes.remove(indexID);
            attributes.remove(indexEventID);
            while( scanner.hasNextLine() ){
                line = scanner.nextLine();
                ArrayList<Integer> frequencies = new ArrayList<Integer>();
                int i = 0;
                for( String s : line.split(";") ){
                    if( i == indexEventID ){
                        eventIds.add((int) Float.parseFloat(s.replace("\"","")));
                    }
                    else if ( i == indexClusterID ){
                        clusterIds.add(Integer.parseInt(s.replace("cluster_","").replace("\"","")));
                    }
                    else if( i == indexID ){
                        ids.add((int) Float.parseFloat(s.replace("\"","")));
                    }
                    i++;
                }
                data.put(nLines, frequencies);
                nLines++;
            }
        }finally{
            scanner.close();
        }
    }
    
    private void readFile2() throws IOException{
        log.debug("START READ = DADOS");
        Scanner scanner = new Scanner(csvFile,"UTF-8");
        String line;
        nLines = 1;
        try{
            while( scanner.hasNextLine() ){
                line = scanner.nextLine();
                ArrayList<String> buffers = new ArrayList<String>();
                int idx = 0;
                for( String s : line.split(";") ){
                    if( idx++ < 2 )
                        continue;
                    buffers.add(s.replaceAll(".0", ""));
                }
                data2.put(nLines++, buffers);
            }
            log.debug("READ ATTRIBUTES = "+data2.get(1).size());
            log.debug("READ nLINES = "+(nLines-1));
        }finally{
            scanner.close();
        }
        log.debug("FINISH READ = DADOS");
    }

    public boolean containsAttribute(String attribute){
        return attributes.indexOf(attribute) >= 0  ? true : false;
    }
    
    public int indexOfAttribute(String attribute){
        return attributes.indexOf(attribute);
    }

    public int getTermFrequency(String attribute, int event){
        int i = attributes.indexOf(attribute);
        //System.out.println("{"+attribute+"} with id {"+i+"} in eventId {"+event+"} has frequency {"+data.get(event).get(i)+"}");
        return data.get(event).get(i);
    }

    public ArrayList<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<String> attributes) {
        this.attributes = attributes;
    }

    public HashMap<Integer, ArrayList<Integer>> getData() {
        return data;
    }
    
    public HashMap<Integer, ArrayList<String>> getData2() {
        return data2;
    }

    public void setData(HashMap<Integer, ArrayList<Integer>> data) {
        this.data = data;
    }

    public int getnLines() {
        return nLines-1;
    }

    public ArrayList<Integer> getClusterIds() {
        return clusterIds;
    }    
    
    public ArrayList<Integer> getEventIds() {
        return eventIds;
    }

    public ArrayList<Integer> getIds() {
        return ids;
    }    
}
