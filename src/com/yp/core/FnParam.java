package com.yp.core;

public class FnParam {

	private String name;
	private Object value;

	public FnParam(String pName, Object pValue) {
		super();
		name = pName;
		value = pValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String pName) {
		name = pName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object pValue) {
		value = pValue;
	}
}
