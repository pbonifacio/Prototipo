package com.app.plan2see.sources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.sql.Date;
import java.util.Calendar;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.app.plan2see.model.db.Event;


import com.app.plan2see.db.controller.EventsController;

public class ArtsSource extends GenericSource {

	public enum MonthEnumeration {
		January, February, March, April, May, June, July, August, September, October, November, December
	}

	public ArtsSource(String url, String title, Proxy proxy, int pages,
			EventsController db, EscapeChars chars) {
		super(url, title, proxy, pages, db, chars);
		// TODO Auto-generated constructor stub

		patterns.addPattern("TITLE", "<title>(.*) &[a-zA-Z]{3}; (.*)</title>");

		/*
		 * PATTERN FOR LOOKING Experiencegr.com POSTS
		 */
		patterns
				.addPattern(
						"POSTS1",
						".*<div class=\"evnt-listing\">.*<a name=\"(.*)\">.*</a>.*<dl class=\"event-info\".*");
		patterns
				.addPattern(
						"POSTS2",
						"<div class=\"evnt-listing\">.*<a name=\"(.*)\">.*</a>.*<dl class=\"event-info\"");

		/*
		 * PATTERNS FOR SEARCHING Experiencegr.com EVENTS INFO
		 */
		patterns
				.addPattern(
						"POSTS_HTML_TITLE",
						".*<h4>.*<a.*href.*title=\"View Details\">(.*)</a>.*</h4>.*<dl class=\"event-info\">.*");
		patterns.addPattern("POSTS_HTML_DATE",
				".*<h5 class=\"event-dates\">(.*)</h5>.*");
		patterns.addPattern("POSTS_HTML_LOCATION",
				".*<dt class=\"e-location\">Location:</dt>.*<dd class=\"e-location\">"
						+ ".*>(.*)</a>.*</dd>.*</dl>.*<div class=\"desc\">.*");
		patterns.addPattern("POSTS_HTML_DESC",
				".*<div class=\"desc\">(.*)<a.*href=.*" + ""
						+ "<div class=\"e-itemBottom\">.*");

		patterns.addPattern("DATE", "(.*) ([0-9]{2}),.([0-9]{4}).*-(.*)");

		logFile.debug("Patterns for " + this.getName() + "@"
				+ this.getClass().getName() + " created.");
	}

	Date formatInitialDate(String date) {
		java.sql.Date iniDate = null;
		Pattern pattern = Pattern.compile(patterns.getValue("DATE"));
		Calendar c = Calendar.getInstance();
		Matcher matcher = pattern.matcher(date);
		if (matcher.find()) {
			// System.out.println(matcher.group(1));
			int initialMonth = MonthEnumeration.valueOf(matcher.group(1))
					.ordinal();
			int initialDay = Integer.parseInt(matcher.group(2));
			int initialYear = Integer.parseInt(matcher.group(3));
			c.clear();
			c.set(initialYear, initialMonth, initialDay);
			java.util.Date d = c.getTime();
			iniDate = new java.sql.Date(d.getTime());
		}
		return iniDate;
	}

	boolean readMorePages(int nPages) {
		stop = false;
		int i = 2;
		while (i < nPages) {
			try {
				u = new URL(url.replace("e_pageNum=1", "e_pageNum=" + i));
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
			if( !stop )
				stop = readPage();
			if (stop)
				return stop;
			i++;
		}
		return false;
	}
	void readPosts(String page) {
		logFile.debug("Searching events in " + u.toString() + " "
				+ this.getClass().getName() + ".");
		posts = new Vector<String>();
		page = page.replace("\n", "");
		Pattern pattern = Pattern.compile(patterns.getValue("POSTS"));
		Matcher matcher = pattern.matcher(page);
		String text = "";
		if (matcher.find()) {
			text = new String(matcher.group(0));
			int i = text.indexOf("<li class='' id='event-");
			while (i <= text.length() && i >= 0) {
				// System.out.println(i);
				text = text.replaceFirst("<li class='' id='event-", "");
				int j = text.indexOf("<li class='' id='event-");
				if (j < 0)
					posts.add(text.substring(i, text.length()));
				else
					posts.add(text.substring(i, j));
				i = j;
			}
		}
	}

	void searchEvents(String page) {
		// TODO Auto-generated method stub
		logFile.debug("Searching events in " + u.toString() + " "
				+ this.getClass().getName() + ".");
		page = page.replace("\n", "");
		Pattern pattern = Pattern.compile(patterns.getValue("POSTS1"));
		Matcher matcher = pattern.matcher(page);
		Vector<String> posts = new Vector<String>();
		String text = "";
		// System.out.println(page);
		if (matcher.find()) {
			text = new String(matcher.group(0));
			int i = text.indexOf("<div class=\"evnt-listing\">");
			while (i <= text.length() && i >= 0) {
				text = text.replaceFirst("<div class=\"evnt-listing\">", "");
				int j = text.indexOf("<div class=\"evnt-listing\">");
				if (j < 0)
					posts.add(text.substring(i, text.length()));
				else
					posts.add(text.substring(i, j));
				i = j;
			}
		} else {
			pattern = Pattern.compile(patterns.getValue("POSTS2"));
			matcher = pattern.matcher(page);
			if (matcher.find()) {
				text = new String(matcher.group(0));
				System.out.println(text);
			}
		}
		for (String post : posts) {
			pattern = Pattern.compile(patterns.getValue("POSTS_HTML_TITLE"));
			matcher = pattern.matcher(post);
			String title = "", location = "", desc = "", unDate = "";
			java.sql.Date date = null;
			if (matcher.find())
				title = new String(matcher.group(1));
			title = title.trim();
			title = replaceHTMLTags(title);
			pattern = Pattern.compile(patterns.getValue("POSTS_HTML_DATE"));
			matcher = pattern.matcher(post);
			if (matcher.find())
				unDate = new String(matcher.group(1));
			unDate = unDate.trim();
			unDate = chars.replaceChars(unDate);
			date = formatInitialDate(unDate);
			pattern = Pattern.compile(patterns.getValue("POSTS_HTML_LOCATION"));
			matcher = pattern.matcher(post);
			if (matcher.find())
				location = new String(matcher.group(1));
			location = location.trim();
			location = replaceHTMLTags(location);
			location = chars.replaceChars(location);
			pattern = Pattern.compile(patterns.getValue("POSTS_HTML_DESC"));
			matcher = pattern.matcher(post);
			if (matcher.find())
				desc = new String(matcher.group(1));
			desc = desc.trim();
			desc = desc.replace("						 ", "");
			desc = replaceHTMLTags(desc);
			desc = chars.replaceChars(desc);
			// System.out.println(desc);
			Event event = new Event();
			addEvent(event);
		}
	}
        
        @Override
	String searchDate(String post_html) {
		if( post_html == null )
			return null;
		logFile.debug("Searching event date in " + u.toString() + " "
				+ this.getClass().getName() + ".");
		Pattern pattern = Pattern.compile(patterns.getValue("POSTS_HTML_DESC"));
		Matcher matcher = pattern.matcher(post_html);
		String date = "";
		if (matcher.find()) {
			//System.out.println(matcher.group(1));
			date = matcher.group(1);
			date = date.replace("  ", "");
		}
		return date;
	}

	@Override
	String searchDescription(String post_html) {
		if( post_html == null )
			return null;
		logFile.debug("Searching event description in " + u.toString() + " "
				+ this.getClass().getName() + ".");
		Pattern pattern = Pattern.compile(patterns.getValue("POSTS_HTML_DESC"));
		Matcher matcher = pattern.matcher(post_html);
		String description = "";
		if (matcher.find()) {
			//System.out.println(matcher.group(1));
			description = matcher.group(4);
			description = description.replace("  ", "");
		}
		return description;
	}

	@Override
	String searchLocation(String post_html) {
		if( post_html == null )
			return null;
		logFile.debug("Searching event location in " + u.toString() + " "
				+ this.getClass().getName() + ".");
		Pattern pattern = Pattern.compile(patterns.getValue("POSTS_HTML_DESC"));
		Matcher matcher = pattern.matcher(post_html);
		String location = "";
		if (matcher.find()) {
			//System.out.println(matcher.group(1));
			location = matcher.group(3);
			location = location.replace("  ", "");
		}
		return location;
	}

	@Override
	String searchName(String post_html) {
		if( post_html == null )
			return null;
		logFile.debug("Searching event title in " + u.toString() + " "
				+ this.getClass().getName() + ".");
		String post_title = "";
		Pattern pattern = Pattern.compile(patterns.getValue("POSTS_HTML_TITLE"));
		Matcher matcher = pattern.matcher(post_html);
		if (matcher.find()) {
			post_title = matcher.group(1);
			post_title = replaceHTMLTags(post_title);
			post_title = chars.replaceChars(post_title);
		}
		return post_title;
	}
}
