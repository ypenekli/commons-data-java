package com.yp.core.ref;

import com.yp.core.entity.DataEntity;

public class Reference<T> extends DataEntity implements IReference<T> {

	public static final transient String KEY_FILED_NAME = "key";
	public static final transient String VALUE_FILED_NAME = "value";
	private static final String DESCRIPTION_FIELD_NAME = "description";

	private static final long serialVersionUID = 7652554245250573812L;
	private String keyFieldName;
	private String labelFieldName;
	private String toStringFieldName;

	public Reference(String pKeyFieldName, T pKey, String pValueFieldName,
			String pValue) {
		super();
		keyFieldName = pKeyFieldName;
		labelFieldName = pValueFieldName;
		toStringFieldName = labelFieldName;
		setKey(pKey);
		setValue(pValue);
	}

	public Reference(T pKey, String pValue) {
		this(KEY_FILED_NAME, pKey, VALUE_FILED_NAME, pValue);
	}

	public Reference(T pKey) {
		this(KEY_FILED_NAME, pKey, VALUE_FILED_NAME, pKey.toString());
	}

	public Reference(T pKey, String pValue, String pDescription) {
		this(pKey, pValue);
		set(DESCRIPTION_FIELD_NAME, pDescription);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getKey() {
		return (T) get(keyFieldName);
	}

	@Override
	public void setKey(T pKey) {
		set(keyFieldName, pKey);
	}

	@Override
	public String getValue() {
		return (String) get(labelFieldName);
	}

	@Override
	public void setValue(String pValue) {
		set(labelFieldName, pValue);
	}

	@Override
	public String getDescription() {
		return (String) get(DESCRIPTION_FIELD_NAME);
	}

	@Override
	public void setDescription(String pExtra) {
		set(DESCRIPTION_FIELD_NAME, pExtra);
	}

	public void setToStringField(String pToStringField) {
		toStringFieldName = pToStringField;
	}

	@Override
	public String toString() {
		if (!isNull(toStringFieldName))
			return (String) get(toStringFieldName);
		else
			return super.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object pObj) {
		if (pObj instanceof Reference) {
			if (!isNull(keyFieldName))
				return getKey().equals(((Reference<T>) pObj).getKey());
		} else if (pObj != null) {
			return get(keyFieldName).equals(pObj);
		}
		return false;
	}

}
