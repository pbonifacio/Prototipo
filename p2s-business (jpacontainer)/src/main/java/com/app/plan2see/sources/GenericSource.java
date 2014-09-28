package com.app.plan2see.sources;

import com.app.plan2see.model.db.Event;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import com.app.plan2see.db.controller.EventsController;
import java.util.Vector;

public abstract class GenericSource extends Thread{

	public enum pattern{TITLE, DATE, DESCRIPTION, LOCATION};
	protected String url = null;
	protected URL u = null;
	protected String title = null;
	protected String type = null;
	protected BufferedReader in = null;
	protected Proxy proxy = null;
	protected BufferedWriter out = null;
	protected int nPages;
	protected EscapeChars chars;
	boolean stop = false;
	protected static Logger logFile = Logger.getLogger("Plan2SeeServer");
	private EventsController db;
	protected SearchPatterns patterns = null;
	protected String page = new String("");
	protected Vector<String> posts = null;
	
	public GenericSource(String url, String title, Proxy proxy, int nPages, EventsController db, EscapeChars chars){
		super();
		this.chars = chars;
		this.db = db;
		this.url = url;
		this.proxy = proxy;
		try {
			this.u = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			if( proxy == null )
				in = new BufferedReader(new InputStreamReader(u.openStream()));
			else
				in = new BufferedReader(new InputStreamReader(u.openConnection(proxy).getInputStream()));
		} catch (IOException e) {
			logFile.debug("Page "+u+" does not exists @"+this.getClass().getName()+".");
			stop = true;
			return;
		}
		this.title = title;
		this.nPages = nPages;
		patterns = new SearchPatterns(title);
	}
	
	public void run() {
		if (!stop)
			stop = readPage();
		if (!stop)
			stop = readMorePages(nPages);

		patterns.close();
		logFile.debug("Exiting " + this.getName() + "@"
				+ this.getClass().getName() + ".");
	}
	
	boolean readPage() {
		String line = null;
		try {
			while ((line = in.readLine()) != null) {
				page += line + "\n";
			}
		} catch (IOException e) {
			logFile.debug("Page " + u + " does not exists.");
			return true;
		}
		page = page.substring(0, page.length() - 1);
		logFile.debug("Page " + u + " read.");
		// storePage(page);
		// setTitle(page);

		if (page != null && !page.equals("")){
			readPosts(page);
			for (String post_html : posts) {
				Event e = new Event();
				e.setEventTitle(searchName(post_html));
				e.setEventDescription(searchDescription(post_html));
				e.setEventSource(title);
				logFile.debug("Adding event "+e.getEventTitle());
				addEvent(e);
			}
			page = null;
		}
		else
			return true;
		return false;
		// System.out.println();
	}

	String setTitle(String page) {
		Pattern pattern = Pattern.compile(patterns.getValue("TITLE"));
		Matcher matcher = pattern.matcher(page);
		title = "";
		if (matcher.find()) {
			title = matcher.group(0);
			title = title.replace("<title>", "");
			title = title.replace("</title>", "");
			title = title.replace("&amp;", "&");
		}
		return title;
	}
	
	boolean readMorePages(int nPages) {
		stop = false;
		int i = 2;
		while (i < nPages) {
			Runtime r = Runtime.getRuntime();
			r.gc();
			try {
				if( this instanceof ITEventsSource )
					u = new URL(url + "/page/" + i);
				else if( this instanceof ArtsSource )
					u = new URL(url.replace("e_pageNum=1", "e_pageNum=" + i));
				else if( this instanceof NatureSource )
					u = new URL(url.substring(0, url.length() - 1)
							+ "?&search[page]=1&page=" + i);
				else
					logFile.debug("Not expected Source class.");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			try {
				if (proxy == null)
					in = new BufferedReader(new InputStreamReader(u
							.openStream()));
				else
					in = new BufferedReader(new InputStreamReader(u
							.openConnection(proxy).getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
				stop = true;
			}
			logFile.debug("Reading " + u.toString() + " "
					+ this.getClass().getName() + ".");
			stop = readPage();
			if (stop)
				return stop;
			i++;
		}
		return false;
	}
	
	abstract void readPosts(String page);
	abstract String searchName(String post);
	abstract String searchDate(String post);
	abstract String searchLocation(String post);
	abstract String searchDescription(String post);
	
	abstract java.sql.Date formatInitialDate(String date);

	void addEvent(Event e) {
		// TODO Auto-generated method stub
		db.insertEntity(e);
		return;
	}
	
	void openFile(){
		try {
			out = new BufferedWriter(
					new FileWriter(new File(title + ".events")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void writeToFile(String msg){
		try {
			out.write(msg);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void closeFile(){
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void storePage(String page) {
		String filename = u.toString().replace("http://", "");
		filename = filename.replace("/", "_");
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(
					filename + ".store")));
			out.write(page);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	String replaceHTMLTags(String str) {
		str = str.replace("<strong>", "");
		str = str.replace("</strong>", "");
		str = str.replace("<br />", "");
		str = str.replace("<p>", "");
		str = str.replace("</p>", "");
		str = str.replace("</a>", "");
		str = str.replace("<!--venue-->","");
		return str;
	}
}