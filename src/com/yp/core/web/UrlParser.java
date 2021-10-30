package com.yp.core.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.yp.core.log.MyLogger;

public class UrlParser {
	private UrlParser() {
	}

	public static Map<String, String> getQueryies(String pUrl) {
		Map<String, String> queries = new HashMap<>();
		try {
			URL uri = new URL(pUrl);
			String query = uri.getQuery();
			if (query != null) {
				String[] params = query.split("=");
				if (params != null && params.length > 1) {
					for (int i = 0; i < params.length; i += 2) {
						queries.put(params[i], params[i + 1]);
					}
				}
			}
		} catch (MalformedURLException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return queries;

	}
}
