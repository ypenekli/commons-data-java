package com.yp.core.db;

public class Pager {
	private int pageIndex;
	private int pageSize;
	private int length;

	public Pager() {
		super();
		pageIndex = 0;
		pageSize = -1;
		length = -1;

	}

	public Pager(int pPageIndex, int pLimit, int pCount) {
		this();
		pageIndex = pPageIndex;
		pageSize = pLimit;
		length = pCount;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pPageIndex) {
		pageIndex = pPageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pLimit) {
		pageSize = pLimit;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int pCount) {
		length = pCount;
	}

}
