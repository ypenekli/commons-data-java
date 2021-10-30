package com.yp.core.entity;

import java.io.Serializable;

public interface IElement extends Serializable {

	Object getValue();

	void setValue(Object pValue);

	boolean isChanged();

	void setChanged(boolean pChanged);

	void setValue(Object pValue, boolean pChanged);

	String getTypeName();

	void setTypeName(String pTypeName);

	boolean isReadOnly();

	void setReadOnly(boolean pReadonly);

	void accept();
	
	void reject();
}
