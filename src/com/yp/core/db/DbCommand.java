package com.yp.core.db;

import com.yp.core.FnParam;

public class DbCommand {
	private String name;
	private String query;
	private FnParam[] params;
	
	
	public DbCommand(final String pName, final FnParam... pParams) {
		name = pName;
		params = pParams;
	}

	public DbCommand(final String pQuery) {
		query = pQuery;
	}

	public String getName() {
		return name;
	}

	public void setName(final String pName) {
		name = pName;
	}

	public FnParam[] getParams() {
		return params;
	}

	public void setParams(final FnParam... pParams) {
		params = pParams;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(final String pQuery) {
		query = pQuery;
	}
}