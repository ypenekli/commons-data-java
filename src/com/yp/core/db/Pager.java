package com.yp.core.db;

public class Pager {
	private int pageIndex;
	private int pageSize;
	private int length;

	public Pager() {
		super();
		pageSize = 50;
		pageIndex = 0;
		length = -1;

	}

	public Pager(int pPageSize) {
		this();		
		pageSize = pPageSize;
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
	
	public void reset(int pageSize) {
		this.pageSize = pageSize;
		pageIndex = 0;
		length = -1;
	}

}
