package com.yp.core.db;

public interface OnExportListener {

	public enum PHASE {
		STARTS, PROCEED, SAVE_BEFORE, SAVE_AFTER, ENDS, FAILS, ENDS_ALL, FAILS_ALL
	}

	void onProceed(PHASE phase, Double progress, int count, String message);

}
