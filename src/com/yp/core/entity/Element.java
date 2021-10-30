package com.yp.core.entity;

public class Element implements IElement {

	private static final long serialVersionUID = 4398406209912604390L;
	private Object value;
	private Object _value;
	private boolean changed;
	private String typeName;
	private boolean readOnly;

	public Element() {
		super();
		value = null;
		changed = false;
		typeName = "";
		readOnly = false;
	}

	public Element(Object pValue) {
		this();
		value = pValue;
		changed = false;
		typeName = "";
		readOnly = false;
	}

	public Element(String pValue, Boolean pChanged) {
		this(pValue);
		value = pValue;
		changed = pChanged;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object pValue) {
		setValue(pValue, true);
	}

	@Override
	public void setValue(Object pValue, boolean pChanged) {
		if(!changed && pChanged)
			_value=value;
		value = pValue;
		changed = pChanged;
	}

	@Override
	public boolean isChanged() {
		return changed;
	}

	@Override
	public void setChanged(boolean pChanged) {
		changed = pChanged;
	}

	@Override
	public String getTypeName() {
		return typeName;
	}

	@Override
	public void setTypeName(String pTypeName) {
		typeName = pTypeName;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public void setReadOnly(boolean pReadonly) {
		readOnly = pReadonly;
	}

	@Override
	public void accept() {
		changed = false;
	}

	@Override
	public void reject() {
		if(changed) {
			changed = false;
			value = _value;
			_value = null;
		}
	}

}
