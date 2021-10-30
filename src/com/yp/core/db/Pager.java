package com.yp.core.db;

public class Pager {
	private int offset;
	private int limit;
	private int count;

	public Pager() {
		super();
		offset = 0;
		limit = -1;
		count = -1;

	}

	public Pager(int pOffset, int pLimit, int pCount) {
		this();
		offset = pOffset;
		limit = pLimit;
		count = pCount;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int pOffset) {
		offset = pOffset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int pLimit) {
		limit = pLimit;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int pCount) {
		count = pCount;
	}

}
