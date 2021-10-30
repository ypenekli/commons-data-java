package com.yp.core.ref;

import com.yp.core.entity.IDataEntity;

public interface IReference<T> extends IDataEntity{	

	T getKey();

	void setKey(T pKey);

	String getValue();

	void setValue(String pValue);

	String getDescription();

	void setDescription(String pDescription);

}
