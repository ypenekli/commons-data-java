package com.yp.core.db;

import com.yp.core.FnParam;

public class DbCommand {
	private String name;
	private String query;
	private FnParam[] params;
	private Pager pager;

	public DbCommand(final String pName, final FnParam... pParams) {
		this(pName, new Pager(), pParams);
	}
	
	public DbCommand(final String pName, final Pager pPager, final FnParam... pParams) {
		name = pName;
		params = pParams;
		pager = pPager;
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

	public Pager getPager() {
		return pager;
	}

	public void setPager(Pager pPager) {
		pager = pPager;
	}
}