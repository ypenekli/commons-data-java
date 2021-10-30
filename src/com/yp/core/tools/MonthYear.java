package com.yp.core.tools;

import com.yp.core.BaseConstants;

public class MonthYear {

	private Integer year;
	private Integer month;

	public MonthYear(Integer pYear, Integer pMonth) {
		super();
		month = pMonth;
		year = pYear;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer pYear) {
		year = pYear;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer pMonth) {
		month = pMonth;
	}

	public String getMonthName() {
		return DateTime.getMonthName(month - 1);
	}

	public String toString() {
		return DateTime.getMonthName(month - 1) + BaseConstants.SPACE + year;
	}

	@Override
	public boolean equals(Object pObj) {
		if (pObj != null)
			if (pObj instanceof MonthYear)
				return getYear().equals(((MonthYear) pObj).getYear())
						&& getMonth().equals(((MonthYear) pObj).getMonth());
		return false;
	}
}
