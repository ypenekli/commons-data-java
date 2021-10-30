package com.yp.core.db;

public class DbConnInfo {

	protected String key;
	protected String value;
	protected String dbDriver;
	protected String dbPassword;
	protected String dbSeperator;
	protected String dbUrl;
	protected String dbUser;
	protected boolean defaultDb;

	public DbConnInfo(String pKey, String pValue) {
		super();
		key = pKey;
		value = pValue;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String pKey) {
		key = pKey;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String pValue) {
		value = pValue;
	}

	public String getDbDriver() {
		return dbDriver;
	}

	public void setDbDriver(String pDbDriver) {
		dbDriver = pDbDriver;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String pDbPassword) {
		dbPassword = pDbPassword;
	}

	public String getDbSeperator() {
		return dbSeperator;
	}

	public void setDbSeperator(String pDbSeperator) {
		dbSeperator = pDbSeperator;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String pDbUrl) {
		dbUrl = pDbUrl;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String pDbUser) {
		dbUser = pDbUser;
	}

	public boolean isDefaultDb() {
		return defaultDb;
	}

	public void setDefaultDb(boolean pDefaultdb) {
		defaultDb = pDefaultdb;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof DbConnInfo) {
			return getKey().equals(((DbConnInfo) obj).getKey());
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return getValue();
	}
}
