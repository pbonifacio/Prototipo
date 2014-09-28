package com.app.plan2see.sources;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class EscapeChars {
	private BufferedWriter out = null;
	private Properties cfg = new Properties();

	public EscapeChars() {
		try {
			cfg.load(new FileInputStream(new File("htmlChars.properties")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeToFile() {
		try {
			out = new BufferedWriter(new FileWriter(new File("htmlChars.cfg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String replaceChars(String text) {
		Set<Object> en = cfg.keySet();
		Iterator i = en.iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			String value = cfg.getProperty(key);
			if (text.indexOf((String) value) >= 0)
				text = text.replaceAll(value, key);
		}
		return text;
	}

	public void addEscapeChar(String key, String value) {
		try {
			out.write(key + "=" + value + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
