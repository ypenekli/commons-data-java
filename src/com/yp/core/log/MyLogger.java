package com.yp.core.log;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.yp.core.BaseConstants;

public class MyLogger {

	public static  final  String NAME = "com.yp.logger";
	private Logger logger;
	private FileHandler fh;

	public MyLogger(String pAddress) {
		super();
		logger = Logger.getLogger(NAME);
		String dDsyAdi = pAddress + BaseConstants.SLASH_TO_LEFT + "log";
		java.io.File dF = new java.io.File(dDsyAdi);
		if (!dF.exists())
			dF.mkdirs();
		try {
			fh = new FileHandler(
					pAddress + BaseConstants.SLASH_TO_LEFT + "log" + BaseConstants.SLASH_TO_LEFT + "gunluk");
		} catch (SecurityException | IOException h) {
			h.printStackTrace();
		}
		BriefLogFormatter bf = new BriefLogFormatter();
		fh.setFormatter(bf);
		logger.addHandler(fh);

	}

	public void log(Level pSeviye, String pGunluk) {
		logger.log(pSeviye, pGunluk);
	}

	public void log(Level pSeviye, String pFormat, Object... pArgs) {
		logger.log(pSeviye, pFormat, pArgs);
	}

	public void log(Level pSeviye, String pGunluk, Throwable thrown) {
		logger.log(pSeviye, pGunluk, thrown);
	}
}
