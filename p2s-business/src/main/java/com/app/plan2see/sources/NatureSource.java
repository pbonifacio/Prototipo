package com.app.plan2see.sources;

import java.net.Proxy;
import java.util.Calendar;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.app.plan2see.db.controller.EventsController;

public class NatureSource extends GenericSource {

	public enum MonthEnumeration {
		Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec
	}

	private static final long serialVersionUID = 1L;
	private SearchPatterns patterns = new SearchPatterns("Nature");

	public NatureSource(String url, String title, Proxy proxy, int n,
			EventsController db, EscapeChars chars) {
		super(url, title, proxy, n, db, chars);

		/*
		 * PATTERN FOR LOOKING Nature.com BASIC POSTS
		 */
		patterns
				.addPattern(
						"POSTS",
						"<div class='basic-results-wrapper'>.*<li class='' id='event-(.*)'>(.*)</li>.*<div class=\"pagination\">");
		patterns.addPattern("POSTS_HTML_ID", "<li class='' id='event-(.*)'>");
		patterns.addPattern("POSTS_HTML_TITLE",
				".*<a href=\".*\">(.*)</a>.*</h3>.*");
		patterns
				.addPattern(
						"POSTS_HTML_DESC",
						"<dl class='event-info'>.*<dt>.*Date:.*</dt>.*<dd>(.*)</dd>.*<dt>.*Type:.*</dt>.*<dd>"
								+ "(.*)</dd>.*<dt>.*Country:.*</dt>.*<dd>(.*)</dd>.*</dl>.*<span class='cleardiv'><!--.*-->"
								+ "</span><p class='view event-sh'>View event description</p>.*<p class='event-details collapsed' id='event-details-.*'>"
								+ "(.*)...*<a href=\"(.*)\">Read more</a>.*</p>.*</li>");
		patterns
				.addPattern(
						"DATE",
						"([a-zA-Z]{3}),.*([a-zA-Z]{3}).*([0-9]{2}),.*([0-9]{4}).*-.*");

		logFile.debug("Patterns for " + this.getName() + "@"
				+ this.getClass().getName() + " created.");
	}
	
	java.sql.Date formatInitialDate(String date) {
		Pattern pattern = Pattern.compile(patterns.getValue("DATE"));
		Matcher matcher = pattern.matcher(date);
		java.sql.Date iniDate = null;
		Calendar c = Calendar.getInstance();
		if (matcher.find()) {
			int initialMonth = MonthEnumeration.valueOf(matcher.group(2))
					.ordinal();
			int initialDay = Integer.parseInt(matcher.group(3));
			int initialYear = Integer.parseInt(matcher.group(4));
			/*int finalMonth = MonthEnumeration.valueOf(matcher.group(2))
					.ordinal();
			int finalDay = Integer.parseInt(matcher.group(7));
			int finalYear = Integer.parseInt(matcher.group(8));*/
			c.clear();
			c.set(initialYear, initialMonth, initialDay);
			java.util.Date d = c.getTime();
			iniDate = new java.sql.Date(d.getTime());
		}
		return iniDate;
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
