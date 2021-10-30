package com.yp.core.mail;

import com.yp.core.entity.DataEntity;

public class Address extends DataEntity {

	private static final long serialVersionUID = -5903807344009291822L;

	public Address() {
		super();
		className = "Address";
	}

	public Address(String pAddress, String pName) {
		this();
		setAddress(pAddress);
		setName(pName);
	}

	protected static  final  String ALN_Address = "Address";

	public String getAddress() {
		return (String) get(ALN_Address);
	}

	public void setAddress(String pAddress) {
		setField(ALN_Address, pAddress, false);
	}

	protected static  final  String ALN_Name = "Name";

	public String getName() {
		return (String) get(ALN_Name);
	}

	public void setName(String pName) {
		setField(ALN_Name, pName, false);
	}
}
