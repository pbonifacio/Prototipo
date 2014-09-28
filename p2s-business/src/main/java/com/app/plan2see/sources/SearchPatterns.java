package com.app.plan2see.sources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.util.Properties;

public class SearchPatterns {
	
	private String title = null; 
	private File patternsFile = null;
	private BufferedWriter out = null;
	private BufferedReader in = null;
	Properties cfg = new Properties();
	
	public SearchPatterns(String title){
		this.title = title;
		/*if( !hasFile(title) )
			patternsFile = new File(title+".properties");
		try {
			out = new BufferedWriter(new FileWriter(patternsFile));
			in = new BufferedReader(new FileReader(patternsFile));
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	public void addPattern(String key, String value){
		cfg.setProperty(key, value);
		/*try {
			if( !hasKey(key) ){
				out.write(key+"="+value+"\n");
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	public boolean hasKey(String key){
		/*try {
			cfg.load(new FileInputStream(patternsFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		if( cfg.getProperty(key) != null )
			return true;
		return false;
	}
	
	public boolean hasFile(String title){
		File f = new File(title+".properties");
		if( f.exists() ){
			patternsFile = f;
			return true;
		}
		return false;
	}
	
	public String getValue(String key){
		/*try {
			cfg.load(new FileInputStream(patternsFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		String value = cfg.getProperty(key);
		return value == null ? "" : value;
	}
	
	public void close(){
		/*try {
			out.close();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
