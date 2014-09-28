package com.app.plan2see.sources;

import java.net.Proxy;
import java.util.Calendar;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.app.plan2see.db.controller.EventsController;

public class ITEventsSource extends GenericSource {

	public enum MonthEnumeration {
		January, February, March, April, May, June, July, August, September, October, November, December
	}

	private static final long serialVersionUID = 1L;
	private SearchPatterns patterns = new SearchPatterns("ITEvents");

	public ITEventsSource(String url, String title, Proxy proxy, int n,
			EventsController db, EscapeChars chars) {
		super(url, title, proxy, n, db, chars);
		patterns.addPattern("TITLE", "<title>(.*) &[a-zA-Z]{3}; (.*)</title>");

		/*
		 * PATTERN FOR LOOKING ITEvents.net POSTS
		 */
		patterns.addPattern("POSTS",
				"<div class=\"post\">(.*)<div class=\"tags\">");

		/*
		 * PATTERNS FOR SEARCHING ITEvents.net EVENTS INFO
		 */
		patterns.addPattern("POSTS_HTML_TITLE",
				"<h1>.* title=\"(.*)\".*</h1>");
		patterns.addPattern("POSTS_TITLE", "title=\"(.*)\"");
		patterns.addPattern("POSTS_HTML_DATE",
				"<table class=\"ec3_schedule\">(.*)</table>");
		patterns.addPattern("POSTS_DATE_DAYS_START",
				"class=\"ec3_start\">(.*)</td><td class=\"ec3_to");
		patterns.addPattern("POSTS_DATE_DAYS_END",
				"class=\"ec3_end\">(.*)</td></tr");
		patterns.addPattern("POSTS_DATE_DAY",
				"<tr><td colspan=\"3\">(.*)</td></tr>");
		patterns.addPattern("POSTS_HTML_DESC1",
				".*width.*height.*/>(.*)<a.*rel=\"nofollow\".*");
		patterns.addPattern("POSTS_HTML_DESC2",
				".*</table><p>(.*)<a.*rel=\"nofollow\".*");
		patterns.addPattern("POSTS_LOCATION", "</table><p>(.*)</p>");
		patterns.addPattern("DATE", "(.*) ([0-9]{2}), ([0-9]{4})");
		System.out.println("Patterns for " + this.getName() + "@"
				+ this.getClass().getName() + " created.");
	}
	
	void readPosts(String page){
		posts = new Vector<String>();
		System.out.println("Searching events in " + u.toString() + " @"
				+ this.getClass().getName() + ".");
		page = page.replace("\n", "");
		Pattern pattern = Pattern.compile(patterns.getValue("POSTS"));
		Matcher matcher = pattern.matcher(page);
		if (matcher.find()) {
			String text = new String(matcher.group(0));
			int i = text.indexOf("<div class=\"post\">");
			while (i <= text.length() && i >= 0) {
				text = text.replaceFirst("<div class=\"post\">", "");
				int j = text.indexOf("<div class=\"post\">");
				if (j < 0)
					posts.add(text.substring(i, text.length()));
				else
					posts.add(text.substring(i, j));
				i = j;
			}
		}
	}

	String searchName(String post_html){
		if( post_html == null )
			return null;
		System.out.println("Searching event title in " + u.toString() + " "
				+ this.getClass().getName() + ".");
		String post_title = "";
		Pattern pattern = Pattern.compile(patterns.getValue("POSTS_HTML_TITLE"));
		Matcher matcher = pattern.matcher(post_html);
		if (matcher.find()) {
			String html_title = matcher.group(0);
			pattern = Pattern.compile(patterns.getValue("POSTS_TITLE"));
			matcher = pattern.matcher(html_title);
			if (matcher.find()) {
				post_title = matcher.group(0).replace("title=\"", "");
				post_title = post_title.replace("\"", "");
				post_title = replaceHTMLTags(post_title);
				post_title = chars.replaceChars(post_title);
			}
		}
		return post_title;
	}
	
	String searchDate(String post_html){
		if( post_html == null )
			return null;
		System.out.println("Searching event date in " + u.toString() + " "
				+ this.getClass().getName() + ".");
		Pattern pattern = Pattern.compile(patterns.getValue("POSTS_HTML_DATE"));
		Matcher matcher = pattern.matcher(post_html);
		if (matcher.find()) {
			String post_date = matcher.group(1);
			if (post_date.contains("ec3_start")) {
				String start = "";
				String end = "";
				pattern = Pattern.compile(patterns
						.getValue("POSTS_DATE_DAYS_START"));
				matcher = pattern.matcher(post_date);
				if (matcher.find())
					start = matcher.group(1);
				pattern = Pattern.compile(patterns
						.getValue("POSTS_DATE_DAYS_END"));
				matcher = pattern.matcher(post_date);
				if (matcher.find())
					end = matcher.group(1);
				return start;
			} else {
				String d = "";
				pattern = Pattern.compile(patterns
						.getValue("POSTS_DATE_DAY"));
				matcher = pattern.matcher(post_date);
				if (matcher.find())
					d = matcher.group(1);
				int i = d.indexOf("<");
				if (i >= 0)
					d = d.substring(0, i);
				return d;
			}
		}
		return null;
	}
	
	String searchLocation(String post_html){
		if( post_html == null )
			return null;
		System.out.println("Searching event location in " + u.toString() + " "
				+ this.getClass().getName() + ".");
		String location = "";
		Pattern pattern = Pattern.compile(patterns.getValue("POSTS_LOCATION"));
		Matcher matcher = pattern.matcher(post_html);
		if (matcher.find()) {
			String html_location = matcher.group(1);
			int i = html_location.indexOf("<p");
			if (i < 0)
				i = html_location.indexOf("<s");
			if (i < 0)
				i = html_location.indexOf("<i");
			if (i < 0)
				i = html_location.length();
			location = html_location.substring(0, i);
			location = replaceHTMLTags(location);
			location = chars.replaceChars(location);
		}
		return location;
	}
	
	String searchDescription(String post_html){
		if( post_html == null )
			return null;
		System.out.println("Searching event description in " + u.toString() + " "
				+ this.getClass().getName() + ".");
		String html_desc = "";
		int i = post_html.indexOf("<a rel=\"nofollow\"");
		if (i > 0) {
			post_html = post_html.substring(0, i + 20);
		}
		Pattern pattern = Pattern.compile(patterns.getValue("POSTS_HTML_DESC1"));
		Matcher matcher = pattern.matcher(post_html);
		if (matcher.find()) {
			html_desc = matcher.group(1);
			html_desc = replaceHTMLTags(html_desc);
			html_desc = chars.replaceChars(html_desc);
		} else {
			pattern = Pattern
					.compile(patterns.getValue("POSTS_HTML_DESC2"));
			matcher = pattern.matcher(post_html);
			if (matcher.find()) {
				html_desc = matcher.group(1);
				html_desc = replaceHTMLTags(html_desc);
				html_desc = chars.replaceChars(html_desc);
			}
		}
		return html_desc;
	}

	java.sql.Date formatInitialDate(String date) {
		if( date == null )
			return null;
		if (date.indexOf("</td>") >= 0)
			date = date.substring(0, date.indexOf("</td") - 1);
		Pattern pattern = Pattern.compile(patterns.getValue("DATE"));
		Matcher matcher = pattern.matcher(date);
		java.sql.Date iniDate = null;
		Calendar c = Calendar.getInstance();
		if (matcher.find()) {
			int initialMonth = MonthEnumeration.valueOf(matcher.group(1))
					.ordinal();
			int initialDay = Integer.parseInt(matcher.group(2));
			int initialYear = Integer.parseInt(matcher.group(3));
			// int finalMonth =
			// MonthEnumeration.valueOf(matcher.group(2)).ordinal();
			// int finalDay = Integer.parseInt(matcher.group(7));
			// int finalYear = Integer.parseInt(matcher.group(8));
			c.clear();
			c.set(initialYear, initialMonth, initialDay);
			java.util.Date d = c.getTime();
			iniDate = new java.sql.Date(d.getTime());
		}
		return iniDate;
	}
}
