package com.yp.core.entity;

import java.io.Serializable;

public interface IResult<T> extends Serializable {

	boolean isSuccess();

	void setSuccess(boolean pSuccess);

	String getMessage();

	void setMessage(String pMessage);

	T getData();

	void setData(T pData);

	int getErrorcode();

	void setErrorcode(int pErrorcode);

	int getDataLength();

	void setDataLength(int pDataLength);
}
