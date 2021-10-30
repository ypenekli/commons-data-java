package com.yp.core.tools;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.yp.core.BaseConstants;
import com.yp.core.log.MyLogger;
import com.yp.core.ref.IReference;
import com.yp.core.ref.Reference;

public class DateTime extends java.util.GregorianCalendar {

	private static final long serialVersionUID = -7973536949424984954L;

	private BigDecimal dbDate;

	private BigDecimal dbTime;

	private BigDecimal dateTime;

	private static DateFormat dFormat;

	public DateTime() {
		super();
		dbDate = createDate(this);
		dbTime = createTime(this);
		dateTime = createDateTime(this);
	}

	public DateTime(Date pTime) {
		super();
		setTime(pTime);
		dbDate = createDate(this);
		dbTime = createTime(this);
		dateTime = createDateTime(this);
	}

	public DateTime(BigDecimal pDate, BigDecimal pTime) {
		super();
		dbDate = pDate;
		dbTime = pTime;
		setTime(asDateTime(pDate, pTime));
		dateTime = createDateTime(this);
	}

	public DateTime(int year, int month, int day) {
		super(year, month, day);
		dbDate = createDate(this);
		dbTime = createTime(this);
		dateTime = createDateTime(this);
	}

	public DateTime(int year, int month, int day, int hour, int minute) {
		super(year, month, day, hour, minute);
		dbDate = createDate(this);
		dbTime = createTime(this);
		dateTime = createDateTime(this);
	}

	public DateTime(int year, int month, int day, int hour, int minute, int second) {
		super(year, month, day, hour, minute, second);
		dbDate = createDate(this);
		dbTime = createTime(this);
		dateTime = createDateTime(this);
	}

	public DateTime(java.util.Locale aLocale) {
		super(aLocale);
		dbDate = createDate(this);
		dbTime = createTime(this);
		dateTime = createDateTime(this);
	}

	public DateTime(java.util.TimeZone zone) {
		super(zone);
		dbDate = createDate(this);
		dbTime = createTime(this);
		dateTime = createDateTime(this);
	}

	public DateTime(java.util.TimeZone zone, java.util.Locale aLocale) {
		super(zone, aLocale);
		dbDate = createDate(this);
		dbTime = createTime(this);
		dateTime = createDateTime(this);
	}

	public BigDecimal getDbDate() {
		return dbDate;
	}

	public BigDecimal getDbTime() {
		return dbTime;
	}

	public BigDecimal getDbTime8() {
		return dbTime.divide(BigDecimal.TEN, BigDecimal.ROUND_HALF_DOWN);
	}

	public BigDecimal getDbDateTime() {
		return dateTime;
	}

	public static BigDecimal dbToday() {
		return createDate(Calendar.getInstance());
	}

	public static BigDecimal dbNow() {
		return createDateTime(Calendar.getInstance());
	}

	public static BigDecimal normalize(BigDecimal pDateTime) {
		return new BigDecimal(pDateTime.toString().substring(0, 14)).multiply(BaseConstants.BIGDECIMAL_THOUSAND);
	}

	private static BigDecimal createDateTime(Calendar pC) {
		return new BigDecimal((pC.get(Calendar.YEAR) * 10000000000000L) + ((pC.get(Calendar.MONTH) + 1) * 100000000000L)
				+ (pC.get(Calendar.DAY_OF_MONTH) * 1000000000L) + (pC.get(Calendar.HOUR_OF_DAY) * 10000000)
				+ (pC.get(Calendar.MINUTE) * 100000) + (pC.get(Calendar.SECOND) * 1000)
				+ (pC.get(Calendar.MILLISECOND)));
	}

	public static BigDecimal asDbDate(Date pDate) {
		BigDecimal dSnc = BigDecimal.ZERO;
		if (pDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(pDate);
			dSnc = createDate(c);

		}
		return dSnc;
	}

	public static BigDecimal asDbDate(LocalDate pDate) {
		BigDecimal dSnc = BigDecimal.ZERO;
		if (pDate != null) {
			dSnc = createDate(pDate);
		}
		return dSnc;

	}

	public static java.sql.Date asSqlDate(Date pDate) {
		java.sql.Date dSnc = null;
		if (pDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(pDate);
			dSnc = java.sql.Date.valueOf(
					c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH));
		}
		return dSnc;
	}

	private static final String TIRE = "-";

	public static java.sql.Date asSqlDate(String pDate) {
		java.sql.Date dSnc = null;
		if (pDate != null && pDate.length() > 6) {
			pDate = pDate.replace("/", TIRE).replace("\\", TIRE).replace(".", TIRE).replace(",", TIRE);
			String dDizi[] = pDate.split(TIRE);
			if (dDizi.length == 3)
				dSnc = java.sql.Date.valueOf(dDizi[2] + TIRE + dDizi[1] + TIRE + dDizi[0]);
		}
		return dSnc;
	}

	public static BigDecimal asDbTime(Date pDate) {
		BigDecimal dSnc = BigDecimal.ZERO;
		if (pDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(pDate);
			dSnc = createTime(c);
		}
		return dSnc;
	}

	public static BigDecimal asDbDateTime(Date pDate) {
		BigDecimal dSnc = BigDecimal.ZERO;
		if (pDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(pDate);
			dSnc = createDateTime(c);
		}
		return dSnc;
	}

	public static BigDecimal asDbDate(Integer pYil, Integer pAy, Integer pGun) {
		return new BigDecimal((pYil * 10000) + (pAy * 100) + pGun);
	}

	private static BigDecimal createDate(Calendar pC) {
		int dYil = pC.get(Calendar.YEAR);
		if (dYil > 999)
			return new BigDecimal(
					(dYil * 10000) + ((pC.get(Calendar.MONTH) + 1) * 100) + (pC.get(Calendar.DAY_OF_MONTH)));
		return null;
	}

	private static BigDecimal createDate(LocalDate pC) {
		int dYil = pC.getYear();
		if (dYil > 999)
			return new BigDecimal((dYil * 10000) + ((pC.getMonthValue()) * 100) + (pC.getDayOfMonth()));
		return null;
	}

	private static BigDecimal createTime(Calendar pC) {
		int dYil = pC.get(Calendar.YEAR);
		if (dYil > 999)
			return new BigDecimal((pC.get(Calendar.HOUR_OF_DAY) * 10000000) + (pC.get(Calendar.MINUTE) * 100000)
					+ (pC.get(Calendar.SECOND) * 1000) + (Math.floor(pC.get(Calendar.MILLISECOND))));
		return null;
	}

	static DateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
	static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd HHmmssSSS", BaseConstants.LOCALE_TR);

	public static Date asDateTime(BigDecimal pDate, BigDecimal pTime) {
		Date dSnc = null;
		DateFormat dF;
		if (pDate != null && pDate.intValue() > 9999999) {
			String dTarih = StringTool.getString(BaseConstants.FORMAT_NUMBER_WITHOUT_DECIMAL_SEP, pDate);
			dF = sdf1;
			if (pTime != null && pTime.intValue() > 9999999) {
				String time = StringTool.getString(BaseConstants.FORMAT_NUMBER_WITHOUT_DECIMAL_SEP, pTime);
				time = StringTool.padLeft(time, '0', 9);
				dTarih += " " + time;
				dF = sdf2;
			}
			try {
				dSnc = dF.parse(dTarih);
			} catch (ParseException h) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, h.getMessage(), h);
			}
		}
		return dSnc;
	}

	public static LocalDate asLocalDate(BigDecimal pDate) {
		if (pDate != null && pDate.intValue() > 9999999) {
			String dT = pDate.toString();
			return LocalDate.of(Integer.parseInt(dT.substring(0, 4)), Integer.parseInt(dT.substring(4, 6)),
					Integer.parseInt(dT.substring(6)));
		}
		return null;
	}

	public static String asDateTR(BigDecimal pDate) {
		if (pDate != null) {
			String dGec = pDate.toString();
			if (dGec.length() == 8)
				return dGec.substring(6) + "/" + dGec.substring(4, 6) + "/" + dGec.substring(0, 4);
		}
		return null;
	}

	public static String asDateTimeTR(BigDecimal pDate) {
		if (pDate != null) {
			String dGec = pDate.toString();
			if (dGec.length() > 14)
				return dGec.substring(6, 8) + "/" + dGec.substring(4, 6) + "/" + dGec.substring(0, 4) + " "
						+ dGec.substring(8, 10) + ":" + dGec.substring(10, 12) + ":" + dGec.substring(12, 14) + ":"
						+ dGec.substring(14);
		}
		return null;
	}

	public static String asDateTR(Date pDate) {
		if (pDate != null) {
			String dGec = asDbDate(pDate).toString();
			if (dGec.length() == 8)
				return dGec.substring(6) + "/" + dGec.substring(4, 6) + "/" + dGec.substring(0, 4);
		}
		return null;
	}

	public static String asDateTR(LocalDate pDate) {
		if (pDate != null) {
			String dGec = asDbDate(pDate).toString();
			if (dGec.length() == 8)
				return dGec.substring(6) + "/" + dGec.substring(4, 6) + "/" + dGec.substring(0, 4);
		}
		return null;
	}

	// public static DateTimeFormatter FORMAT_DATE_TR =
	// DateTimeFormatter.ofPattern("dd/MM/yyyy", BaseConstants.LOCALE_TR);

	public static Date asDate(String pDate) {
		if (pDate != null) {
			try {
				return BaseConstants.FORMAT_DATE.parse(pDate);
			} catch (ParseException e) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return null;
	}

	public static Date asDate(BigDecimal pDateTime) {
		if (pDateTime != null) {
			long dTrhZmn = pDateTime.longValue();
			if (dTrhZmn > 999999999999999l) {
				int bolen = dTrhZmn > 9999999999999999l ? 1000000000 : 100000000;
				double dTarih = Math.floor(dTrhZmn / bolen);
				double dZaman = dTrhZmn - (dTarih * bolen);
				return asDateTime(BigDecimal.valueOf(dTarih), BigDecimal.valueOf(dZaman));
			} else if (dTrhZmn > 9999999) {
				return asDateTime(pDateTime, BigDecimal.ZERO);
			}
		}
		return null;
	}

	public static DateFormat getFormat(Locale pYrlayr) {
		if (dFormat == null) {
			dFormat = DateFormat.getDateInstance(ALL_STYLES, pYrlayr);
		}
		return dFormat;
	}

	public static BigDecimal get(Date pDate, int pBolum) {
		Calendar c = Calendar.getInstance();
		c.setTime(pDate);
		return new BigDecimal(c.get(pBolum));
	}

	public static long diffDays(Date pDateSmaller, Date pDateBigger) {
		long dSnc = 0;
		if (pDateSmaller != null && pDateBigger != null) {
			long diff = (pDateBigger.getTime() - pDateSmaller.getTime());
			dSnc = (int) (diff / 86400000);
		}
		return dSnc;
	}

	public static long diffDays(LocalDate pDateSmaller, LocalDate pDateBigger) {
		return (pDateBigger.toEpochDay() - pDateSmaller.toEpochDay());
	}

	public static Integer diffMonths(LocalDate pDateSmaller, LocalDate pDateBigger) {
		return ((Long) ChronoUnit.MONTHS.between(pDateSmaller, pDateBigger)).intValue();
	}

	public static Integer diffMonths(Integer pYearSmaller, Integer pMonthSmaller, Integer pYearBigger,
			Integer pMonthBigger) {
		int diff = 0;
		if (pYearSmaller.equals(pYearBigger))
			diff = pMonthBigger - pMonthSmaller;
		else {
			int diffyear = pYearBigger - pYearSmaller;
			diff = (12 - pMonthSmaller) + pMonthBigger;
			if (diffyear > 1) {
				diff += 12 * (diffyear - 1);
			}
		}
		return diff;
	}

	public static int diffMonths(Date pDateSmaller, Date pDateBigger) {
		int dSnc = 0;
		if (pDateSmaller != null && pDateBigger != null) {
			Calendar c1 = Calendar.getInstance();
			c1.setTime(pDateSmaller);

			Calendar c2 = Calendar.getInstance();
			c2.setTime(pDateBigger);

			int y1 = c1.get(Calendar.YEAR);
			int y2 = c2.get(Calendar.YEAR);
			int m1 = c1.get(Calendar.MONTH);
			int m2 = c2.get(Calendar.MONTH);
			dSnc = diffMonths(y1, m1, y2, m2);
		}
		return dSnc;
	}

	@Override
	public void add(int pField, int pAmount) {
		super.add(pField, pAmount);
		dbDate = createDate(this);
		dbTime = createTime(this);
		dateTime = createDateTime(this);
	}

	public static String getMonthName(int pAy) {
		return BaseConstants.getString("AY" + pAy);
	}

	public static String getMonthShortName(int pAy) {
		return BaseConstants.getString("AY.KISA" + pAy);
	}

	public static String getDayName(int pGun) {
		return BaseConstants.getString("GUN" + pGun);
	}

	public static Date asDate(LocalDate date) {
		if (date != null)
			return Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		return null;
	}

	public static Date asDate(LocalDateTime date) {
		if (date != null)
			return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
		return null;
	}

	public static LocalDate asLocalDate(Date date) {
		if (date != null)
			return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		return null;
	}

	public static LocalDateTime asLocalDateTime(Date date) {
		if (date != null)
			return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		return null;
	}

	public static List<IReference<Integer>> getYears(Integer pStart) {
		List<IReference<Integer>> dScmListe = new ArrayList<>(4);
		Calendar today = Calendar.getInstance();
		Integer buYil = today.get(Calendar.YEAR) + 1;

		for (int i = buYil; i > pStart; i--) {
			dScmListe.add(new Reference<Integer>(i, i + ""));

		}
		return dScmListe;
	}

	public static List<IReference<Integer>> getMonths(boolean pShortNames) {
		List<IReference<Integer>> months = new ArrayList<>(12);
		for (int dI = 0; dI < 12; dI++) {
			String dAyadi = pShortNames ? getMonthShortName(dI) : getMonthName(dI);
			Integer dKod = dI + 1;
			months.add(new Reference<Integer>(dKod, dAyadi));
		}
		return months;
	}

	public static String[] getMontNames(boolean pShortNames) {
		String[] monthNames = new String[12];
		for (int dI = 0; dI < 12; dI++) {
			monthNames[dI] = pShortNames ? getMonthShortName(dI) : getMonthName(dI);
		}
		return monthNames;
	}

	public static List<IReference<Integer>> getDays(int pStart, int pEnd) {
		if (pStart > 0 && pEnd < 32 && pStart < pEnd) {
			int count = pEnd - pStart;
			List<IReference<Integer>> dScmListe = new ArrayList<>(count);

			for (int dI = 0; dI < count; dI++) {
				Integer dKod = dI + 1;
				dScmListe.add(new Reference<Integer>(dKod));
			}
			return dScmListe;
		}
		return null;
	}

	@Override
	public String toString() {
		return "DateTime [YYYY:AA:GG:SS:DD:SSS=" + dateTime + "]";
	}
}