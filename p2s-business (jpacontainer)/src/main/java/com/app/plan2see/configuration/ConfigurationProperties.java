package com.app.plan2see.configuration;

import java.util.Properties;

public class ConfigurationProperties {
    //APP CONF
    private String HOME;
    private String RAPIDMINER_HOME;
    //APP DEFAULTS
    private double RELEVANT_TERMS_PERCENTAGE;
    private boolean INITIALIZE;
    //BATCH
    private int MAX_EVENTS;
    
    public ConfigurationProperties(){
    }
    
    public double getRELEVANT_TERMS_PERCENTAGE(){
        //return RELEVANT_TERMS_PERCENTAGE;
        return 0.0;
    }
    
    public void setRELEVANT_TERMS_PERCENTAGE(double RELEVANT_TERMS_PERCENTAGE){
        this.RELEVANT_TERMS_PERCENTAGE = RELEVANT_TERMS_PERCENTAGE;
    }
    
    public int getMAX_EVENTS() {
        return MAX_EVENTS;
    }
    
    public String getHOME() {
        return HOME;
    }
    
    public boolean getINITIALIZE() {
        return INITIALIZE;
    }
    
    public String getRAPIDMINER_HOME(){
        return RAPIDMINER_HOME;
    }
    
    public void setProperties(Properties props){
        HOME = props.getProperty("plan2see.home");
        RAPIDMINER_HOME = props.getProperty("rapidminer.home");
        INITIALIZE = "false".equals(props.getProperty("plan2see.initialize")) ? false : true;
        //RELEVANT_TERMS_PERCENTAGE = Double.parseDouble(props.getProperty("plan2see.relevant_percentage"));
    }
}
