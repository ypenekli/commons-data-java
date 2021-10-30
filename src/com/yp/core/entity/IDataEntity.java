package com.yp.core.entity;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Map;

public interface IDataEntity extends Serializable {

	String getSchemaName();

	String getTableName();

	Map<String, IElement> getFields();

	Map<String, IElement> getPrimaryKeys();

	void setPrimaryKeys(String... pKeyNames);

	void set(String pFieldName, Object pValue);

	void set(String pFieldName, String pValue, int pLength);

	void setField(String pFieldName, Object pValue, boolean pChanged);

	void setFieldReadonly(String pFieldName, boolean pChanged);

	void setSelected(boolean pSelect);

	Object get(String pFieldName);

	byte getState();

	void setState(byte pState);

	Integer getRowNum();

	void setRowNum(Integer pRowNum);

	void delete();

	void accept();
	
	void reject();

	boolean isSelected();

	boolean isNew();

	boolean isUpdated();

	boolean isUnchanged();

	boolean isUpdated(String pFieldName);

	boolean isDeleteDisabled();

	boolean isDeleted();

	boolean isPrimaryKey(String pFieldName);

	boolean isNull(String pFieldName);

	String getClassName();

	void load(String[] pFieldNames, Object[] pValues);

	void load(String[] pFieldNames, ResultSet pRs);

	IDataEntity load(IDataEntity pDe);

	void checkValues();

	void setClientInfo(IDataEntity pDataEntity);

	void setLastClientInfo(IDataEntity pDataEntity);

	void setClientInfo(String pUser, String pClientIP, Date pDate);

	void setLastClientInfo(String pUser, String pClientIP, Date pDate);
}
