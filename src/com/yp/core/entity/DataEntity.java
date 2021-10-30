package com.yp.core.entity;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.yp.core.log.MyLogger;
import com.yp.core.tools.DateTime;

//import javafx.beans.property.SimpleBooleanProperty;
//import javafx.beans.property.SimpleDoubleProperty;
//import javafx.beans.property.SimpleIntegerProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.beans.property.SimpleStringProperty;

public class DataEntity implements IDataEntity {

	public static final byte INSERTED = 0;
	public static final byte DELETED = 1;
	public static final byte UPDATED = 2;
	public static final byte UNCHANGED = 3;
	public static final byte EMPTY = 4;

	private static final long serialVersionUID = 5340694004093419660L;
	protected Map<String, IElement> fields;
	private Map<String, IElement> primaryKeys;
	protected byte state;
	protected String className;
	private boolean selected;

	public DataEntity() {
		super();
		className = "DataEntity";
		fields = new HashMap<>();
		primaryKeys = new HashMap<>();
		state = INSERTED;
		selected = false;
	}

	@Override
	public Map<String, IElement> getFields() {
		return fields;
	}

	@Override
	public Map<String, IElement> getPrimaryKeys() {
		return primaryKeys;
	}

	@Override
	public void setPrimaryKeys(String... pKeyNames) {
		primaryKeys.clear();
		for (String k : pKeyNames) {
			String key = k.toLowerCase(Locale.US);
			primaryKeys.put(key, new Element());
		}
	}

	protected Integer getInteger(String pFieldName) {
		Object o = get(pFieldName);
		if (o != null && !(o instanceof Integer)) {
			setField(pFieldName, Integer.parseInt(o.toString()), getFields().get(pFieldName).isChanged());
		}
		return (Integer) get(pFieldName);
	}

	@Override
	public byte getState() {
		return state;
	}

	@Override
	public void setState(byte pState) {
		state = pState;
	}

	protected transient int rowNum;

	@Override
	public Integer getRowNum() {
		return rowNum;
	}

	@Override
	public void setRowNum(Integer pRowNum) {
		rowNum = pRowNum;
	}

	@Override
	public void set(String pFieldName, Object pValue) {
		setField(pFieldName, pValue, true);
	}

	@Override
	public void set(String pFieldName, String pValue, int pLength) {
		if (pValue != null && pValue.length() > pLength) {
			pValue = pValue.substring(0, pLength - 1);
		}
		setField(pFieldName, pValue, true);
	}

	@Override
	public void setFieldReadonly(String pFieldName, boolean pReadonly) {
		String key = pFieldName.toLowerCase(Locale.US);
		if (fields.containsKey(key)) {
			fields.get(key).setReadOnly(pReadonly);
		}
		if (primaryKeys.containsKey(key)) {
			primaryKeys.get(key).setReadOnly(pReadonly);
		}
	}

	@Override
	public void setField(String pFieldName, Object pValue, boolean pChanged) {
		String key = pFieldName.toLowerCase(Locale.US);
		IElement e = fields.computeIfAbsent(key, k -> new Element(pValue));

		e.setValue(pValue, pChanged);
		if (primaryKeys.containsKey(key) && (isNew() || !pChanged)) {
			e = primaryKeys.get(key);
			if (e == null) {
				e = new Element(pValue);
			}
			e.setValue(pValue);
			primaryKeys.put(key, e);
		}
		if (pChanged && UNCHANGED == state)
			state = UPDATED;
	}

	@Override
	public Object get(String pFieldName) {
		String key = pFieldName.toLowerCase(Locale.US);
		if (fields.containsKey(key)) {
			return fields.get(key).getValue();
		}
		return null;
	}

	protected String getClob(String pAlanadi) {
		Object r = get(pAlanadi);
		if (r != null) {
			if (r instanceof String)
				return (String) r;
			if (r instanceof Clob) {
				Clob b = (Clob) r;
				try {
					return b.getSubString(1l, (int) b.length());
				} catch (SQLException e) {
					Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
		return "";
	}

	protected byte[] getBlob(String pAlanadi) {
		Object r = get(pAlanadi);
		if (r != null) {
			if (r instanceof byte[])
				return (byte[]) r;
			if (r instanceof Blob) {
				Blob b = (Blob) r;
				try {
					return b.getBytes((long) 1, (int) b.length());
				} catch (SQLException e) {
					Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
		return null;
	}

	@Override
	public void delete() {
		state = DELETED;
	}

	protected static final String ALN_DELETE_PERMITED = "Deletepermited";

	@Override
	public boolean isDeleteDisabled() {
		return "1".equals(get(ALN_DELETE_PERMITED));
	}

	public void disableDeletion() {
		setField(ALN_DELETE_PERMITED, "1", false);
	}

	public void enableDeletion() {
		setField(ALN_DELETE_PERMITED, "0", false);
	}

	@Override
	public void accept() {
		state = UNCHANGED;
		for (IElement v : fields.values()) {
			v.accept();
		}
		for (Map.Entry<String, IElement> entry : primaryKeys.entrySet()) {
			entry.setValue(fields.get(entry.getKey()));
		}

		// fields.forEach((k, v) -> {
		// v.accept();
		// });
		// primaryKeys.forEach((k, v) -> {
		// v = fields.get(k);
		// });
	}

	@Override
	public void reject() {
		if (state != INSERTED) {
			state = UNCHANGED;
			for (IElement v : fields.values()) {
				v.reject();
			}
		}
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean pSelected) {
		selected = pSelected;
	}

	public Boolean getNew() {
		return INSERTED == state;
	}

	@Override
	public boolean isNew() {
		return INSERTED == state;
	}

	@Override
	public boolean isUpdated() {
		return UPDATED == state;
	}

	@Override
	public boolean isUnchanged() {
		return UNCHANGED == state;
	}

	@Override
	public boolean isDeleted() {
		return DELETED == state;
	}

	@Override
	public boolean isUpdated(String pFieldName) {
		String key = pFieldName.toLowerCase(Locale.US);
		return fields.containsKey(key) && fields.get(key).isChanged();
	}

	@Override
	public boolean isNull(String pFieldName) {
		String key = pFieldName.toLowerCase(Locale.US);
		return !fields.containsKey(key) || fields.get(key).getValue() == null;
	}

	@Override
	public boolean isPrimaryKey(String pFieldName) {
		String key = pFieldName.toLowerCase(Locale.US);
		return primaryKeys.containsKey(key) || primaryKeys.get(key).getValue() == null;
	}

	@Override
	public void load(String[] pFieldNames, Object[] pValues) {
		int i = 0;
		for (String key : pFieldNames) {
			key = key.toLowerCase(Locale.US);
			setField(key, pValues[i++], false);
		}
	}

	@Override
	public void load(String[] pFieldNames, ResultSet pRs) {
		int i = 1;
		for (String key : pFieldNames) {
			key = key.toLowerCase(Locale.US);
			try {
				setField(key, pRs.getObject(i++), false);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public IDataEntity load(final IDataEntity pDe) {
		fields = pDe.getFields();
		primaryKeys = pDe.getPrimaryKeys();
		state = pDe.getState();
		className = pDe.getClassName();
		return this;
	}

	public String getClassName() {
		return className;
	}

	@Override
	public void checkValues() {
		checkBigDecimal(CLIENT_DATETIME);
		checkBigDecimal(LAST_CLIENT_DATETIME);
	}

	public void checkInteger(String pFieldName) {
		Object temp = get(pFieldName);
		if (temp != null && !(temp instanceof Integer))
			setField(pFieldName, (int) Double.parseDouble(temp.toString()), false);
	}

	public void checkLong(String pFieldName) {
		Object temp = get(pFieldName);
		if (temp != null && !(temp instanceof Long))
			setField(pFieldName, (long) Double.parseDouble(temp.toString()), false);
	}

	public void checkBigDecimal(String pFieldName) {
		Object temp = get(pFieldName);
		if (temp != null && !(temp instanceof BigDecimal))
			setField(pFieldName, new BigDecimal(temp.toString()), false);
	}

	public void checkDouble(String pFieldName) {
		Object temp = get(pFieldName);
		if (temp != null && !(temp instanceof Double))
			setField(pFieldName, Double.parseDouble(temp.toString()), false);
	}

	public void checkString(String pFieldName, Format pFormat) {
		Object temp = get(pFieldName);
		if (temp != null && !(temp instanceof String))
			setField(pFieldName, pFormat.format(temp), false);
	}

	public void checkString(String pFieldName) {
		Object temp = get(pFieldName);
		if (temp != null) {
			String str;
			if (!(temp instanceof String))
				str = temp.toString();
			else
				str = (String) temp;
			setField(pFieldName, str.trim(), false);
		}
	}

	public void checkBlob(String pFieldName) {
//		Object temp = get(pFieldName);
//		if (temp != null && !(temp instanceof Blob))
//			setField(pFieldName, getBlob(pFieldName), false);
	}

	public void checkClob(String pFieldName) {
//		Object temp = get(pFieldName);
//		if (temp != null && !(temp instanceof Clob))
//			setField(pFieldName, getClob(pFieldName), false);

	}

	@Override
	public String getSchemaName() {
		return "";
	}

	@Override
	public String getTableName() {
		return "";
	}

	protected static final String CLIENT_NAME = "client_name";
	protected static final String CLIENT_IP = "client_ip";
	protected static final String CLIENT_DATETIME = "client_datetime";
	protected static final String LAST_CLIENT_NAME = "last_client_name";
	protected static final String LAST_CLIENT_IP = "last_client_ip";
	protected static final String LAST_CLIENT_DATETIME = "last_client_datetime";

	@Override
	public void setClientInfo(IDataEntity pDataEntity) {
		set(CLIENT_NAME, pDataEntity.get(CLIENT_NAME));
		set(CLIENT_IP, pDataEntity.get(CLIENT_IP));
		set(CLIENT_DATETIME, pDataEntity.get(CLIENT_DATETIME));
	}

	@Override
	public void setLastClientInfo(IDataEntity pDataEntity) {
		set(LAST_CLIENT_NAME, pDataEntity.get(LAST_CLIENT_NAME));
		set(LAST_CLIENT_IP, pDataEntity.get(LAST_CLIENT_IP));
		set(LAST_CLIENT_DATETIME, pDataEntity.get(LAST_CLIENT_DATETIME));
		if (isNew()) {
			setClientInfo(pDataEntity);
		}
	}

	@Override
	public void setClientInfo(String pClientName, String pClientIP, Date pClientDatetime) {
		set(CLIENT_NAME, pClientName);
		set(CLIENT_IP, pClientIP);
		set(CLIENT_DATETIME, DateTime.asDbDateTime(pClientDatetime));
	}

	@Override
	public void setLastClientInfo(String pClientName, String pClientIP, Date pClientDatetime) {
		set(LAST_CLIENT_NAME, pClientName);
		set(LAST_CLIENT_IP, pClientIP);
		set(LAST_CLIENT_DATETIME, DateTime.asDbDateTime(pClientDatetime));
		if (isNew()) {
			setClientInfo(pClientName, pClientIP, pClientDatetime);
		}
	}

}
