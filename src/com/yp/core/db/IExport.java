package com.yp.core.db;

import java.math.BigDecimal;

import com.yp.core.entity.IDataEntity;

public interface IExport extends IDataEntity, Comparable<IExport> {
	String getExportId();

	String getGroupCode();

	String getSourceSchema();

	String getSourceTable();

	String getTargetSchema();

	String getTargetTable();

	Integer getSourceCount();

	Integer getTargetCount();

	Integer getIdx();

	String getMessages();

	String getQuery();

	void setTargetSchema(String pTargetSchema);

	void setGroupCode(String pGroupCode);

	void setSourceCount(Integer pCount);

	void setTargetCount(Integer pCount);

	void setIdx(Integer pIdx);

	void setStartDatetimeDb(BigDecimal pTime);

	void setEndDatetimeDb(BigDecimal pTime);

	void setMessages(String pMessage);

	boolean isGroupCodeNull();

	boolean isIdxNull();

	boolean isDeleteTargetTableRows();

}
