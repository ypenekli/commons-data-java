package com.yp.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLDecoder;
import java.security.ProtectionDomain;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.yp.core.log.MyLogger;
import com.yp.core.ref.IReference;
import com.yp.core.ref.Reference;

public class BaseConstants {

	private BaseConstants() {
	}

	private static final String config_url = "/core/config/Config.properties";
	public static final ResourceBundle BUNDLE_MESSAGE = ResourceBundle.getBundle("core.config.Messages");

	public static final int ERRORCODE_CONNECTION = 10000;
	public static final int ERRORCODE_NO_USER = 10001;
	public static final int ERRORCODE_WRONG_PASS = 10002;
	public static final int ERRORCODE_SAVE = 10003;
	public static final int ERRORCODE_DELETE = 10004;
	public static final int ERRORCODE_ADD = 10005;
	public static final int ERRORCODE_TRANSFER = 10006;
	public static final int ERRORCODE_READ = 10007;
	public static final String MESSAGE_SAVE_SUCCEEDED;
	public static final String MESSAGE_ADD_SUCCEEDED;
	public static final String MESSAGE_ADD_ROLE_SUCCEEDED;
	public static final String MESSAGE_INFO;
	public static final String MESSAGE_WARNING;
	public static final String MESSAGE_SAVE_ERROR;
	public static final String MESSAGE_SAVE_ERROR_NODATA;
	public static final String MESSAGE_LOGIN_SUCCEEDED;
	public static final String MESSAGE_LOGIN_ERROR;
	public static final String MESSAGE_DATA_TRANSFER_SUCCEEDED;
	public static final String MESSAGE_DATA_TRANSFER_ERROR;
	public static final String MESSAGE_DATA_TRANSFER_ERROR_NODATA;
	public static final String MESSAGE_DELETE_SUCCEEDED;
	public static final String MESSAGE_DELETE_ERROR;
	public static final String MESSAGE_READ_ERROR;
	public static final String MESSAGE_CONNECTION_ERROR;
	public static final String MESSAGE_FIELD_EMPTY_WARNING;
	public static final String NEW;
	public static final String UPDATE;
	public static final Locale LOCALE_TR;
	public static final Locale LOCALE_EN;
	public static final String SLASH_TO_RIGHT = "/";
	public static final String SLASH_TO_LEFT = "\\";
	public static final String SLASH_OS;
	public static final String EOL = "\n";
	public static final String EOL_OS;
	public static final String EOL_HTML = "<br>";
	public static final String USER_DIR;
	public static final String CHAR_UTF_8 = "UTF-8";
	public static final String CHAR_UTF_16 = "UTF-16";
	public static final String CHAR_8859 = "ISO-8859-9";
	public static final String TILDE = "~";
	public static final String COMMA = ",";
	public static final String SEMI_COLON_WITH_SPACE = "; ";
	public static final String COMMA_WITH_SPACE = ", ";
	public static final String DOT_WITH_SPACE = ". ";
	public static final String SPACE = " ";
	public static final String STAR = "*";
	public static final String SHARP = "#";
	public static final String EQUAL = "=";
	public static final String PARENTHESIS_OPEN = "(";
	public static final String PARENTHESIS_CLOSE = ")";
//	public static final String TRUE = "true";
//	public static final String FALSE = "false";
	public static final IReference<String> CONFIRMATION_WAITING;
	public static final IReference<String> CONFIRMATION_OK;
	public static final IReference<String> ENABLED;
	public static final IReference<String> DISABLED;
	public static final IReference<String> ACTIVE;
	public static final IReference<String> PASSIVE;
	public static final IReference<String> TRUE;
	public static final IReference<String> FALSE;
	public static final BigDecimal BIGDECIMAL_MINUS_ONE;
	public static final BigDecimal BIGDECIMAL_TWO;
	public static final BigDecimal BIGDECIMAL_THREE;
	public static final BigDecimal BIGDECIMAL_HUNDRED;
	public static final BigDecimal BIGDECIMAL_THOUSAND;
	public static final NumberFormat FORMAT_CURRENCY;
	public static final NumberFormat FORMAT_CURRENCY_TR;
	public static final NumberFormat FORMAT_NUMBER;
//	public static final NumberFormat FORMAT_NUMBER_INTEGER;
	public static final DecimalFormat FORMAT_NUMBER_WITHOUT_DECIMAL_SEP;
	public static final NumberFormat FORMAT_NUMBER_TR;
	public static final NumberFormat FORMAT_DECIMAL_TR;
	public static final String DATE_TIME_PATTERN_SHORT_TR;
	public static final String DATE_TIME_PATTERN_LONG_TR;
	public static final String DATE_PATTERN_2xSHORT_TR;
	public static final SimpleDateFormat FORMAT_DATE_TIME_SHORT_TR;
	public static final SimpleDateFormat FORMAT_DATE_TIME_LONG_TR;
	public static final DateFormat FORMAT_DATE;
	public static final DateFormat FORMAT_DATE_SHORT_TR;
	public static final DateFormat FORMAT_DATE_2xSHORT_TR;
	public static final DateFormat FORMAT_DATE_LONG_TR;
	public static final String NONLETTER_CHARS_REGEX = "[^a-zA-Z0-9ÇçÝýÞþÖöÜüÐð]";

	public static final String SERVER;
	public static final String REMSERVER;

	private static Properties config;
	private static String rootAddress;

	static {
		// config_url = "/core/config/Config.properties";
		// bundleSql = ResourceBundle.getBundle("core.config.Queries");
		// BUNDLE_MESSAGE = ResourceBundle.getBundle("core.config.Messages");
		MESSAGE_SAVE_SUCCEEDED = getString("1015");
		MESSAGE_ADD_SUCCEEDED = getString("Add.Succeded");
		MESSAGE_ADD_ROLE_SUCCEEDED = getString("Add.Role.Succeded");
		MESSAGE_INFO = getString("10151");
		MESSAGE_WARNING = getString("10152");
		MESSAGE_SAVE_ERROR = getString("1016");
		MESSAGE_SAVE_ERROR_NODATA = getString("10161");
		MESSAGE_LOGIN_SUCCEEDED = getString("10162");
		MESSAGE_LOGIN_ERROR = getString("10163");
		MESSAGE_DATA_TRANSFER_SUCCEEDED = getString("1020");
		MESSAGE_DATA_TRANSFER_ERROR = getString("1021");
		MESSAGE_DATA_TRANSFER_ERROR_NODATA = getString("1041");
		MESSAGE_DELETE_SUCCEEDED = getString("1023");
		MESSAGE_DELETE_ERROR = getString("1024");
		MESSAGE_READ_ERROR = getString("1042");
		MESSAGE_CONNECTION_ERROR = getString("Hata.Baglanti");
		MESSAGE_FIELD_EMPTY_WARNING = getString("1022");
		NEW = getString("Addnew");
		UPDATE = getString("Update");
		LOCALE_TR = new Locale("tr", "TR");
		LOCALE_EN = new Locale("en", "US");
		SLASH_OS = System.getProperty("file.separator");
		EOL_OS = System.getProperty("line.separator");
		USER_DIR = System.getProperty("user.dir");
		CONFIRMATION_WAITING = new Reference<>("B", getString("1030"));
		CONFIRMATION_OK = new Reference<>("O", getString("1031"));
		ENABLED = new Reference<>("E", getString("1028"));
		DISABLED = new Reference<>("D", getString("1029"));
		ACTIVE = new Reference<>("A", getString("Active"));
		PASSIVE = new Reference<>("P", getString("Passive"));
		TRUE = new Reference<>("T", getString("True"));
		FALSE = new Reference<>("F", getString("False"));
		BIGDECIMAL_MINUS_ONE = new BigDecimal(-1);
		BIGDECIMAL_TWO = new BigDecimal(2);
		BIGDECIMAL_THREE = new BigDecimal(3);
		BIGDECIMAL_HUNDRED = new BigDecimal(100);
		BIGDECIMAL_THOUSAND = new BigDecimal(1000);

		FORMAT_CURRENCY = NumberFormat.getCurrencyInstance();
		FORMAT_CURRENCY_TR = NumberFormat.getCurrencyInstance(LOCALE_TR);
		FORMAT_NUMBER = NumberFormat.getNumberInstance();
//		FORMAT_NUMBER_INTEGER = NumberFormat.getIntegerInstance();
		FORMAT_NUMBER_WITHOUT_DECIMAL_SEP = new DecimalFormat("##0");
		FORMAT_NUMBER_TR = NumberFormat.getNumberInstance(LOCALE_TR);
		FORMAT_DECIMAL_TR = NumberFormat.getNumberInstance(LOCALE_TR);
		DATE_PATTERN_2xSHORT_TR = "dd.MM.yy";

		DATE_TIME_PATTERN_SHORT_TR = "dd-MM-yy HH:mm";
		FORMAT_DATE_TIME_SHORT_TR = new SimpleDateFormat(DATE_TIME_PATTERN_SHORT_TR, LOCALE_TR);

		DATE_TIME_PATTERN_LONG_TR = "dd-MM-yyyy HH:mm";
		FORMAT_DATE_TIME_LONG_TR = new SimpleDateFormat(DATE_TIME_PATTERN_LONG_TR, LOCALE_TR);
		FORMAT_DATE = DateFormat.getDateInstance();
		FORMAT_DATE_2xSHORT_TR = new SimpleDateFormat(DATE_PATTERN_2xSHORT_TR, LOCALE_TR);
		FORMAT_DATE_SHORT_TR = DateFormat.getDateInstance(DateFormat.SHORT, LOCALE_TR);
		FORMAT_DATE_LONG_TR = DateFormat.getDateInstance(DateFormat.LONG, LOCALE_TR);

		SERVER = "SERVER";
		REMSERVER = "REMSERVER";
	}

	private static void loadConfig() {
		BaseConstants.config = new Properties();
		Properties localconfig = new Properties();

		try (InputStream is = BaseConstants.class.getResourceAsStream(BaseConstants.config_url)) {
			if (is != null) {
				BaseConstants.config.load(is);
			}
		} catch (Exception e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		boolean loaded = false;
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try (InputStream is = classLoader.getResourceAsStream("../Config.xml")) {
			if (is != null) {
				localconfig = new Properties();
				localconfig.loadFromXML(is);
				BaseConstants.config.putAll(localconfig);
				loaded = true;
			}
		} catch (Exception e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		File f1;
		if (!loaded && (f1 = new File(getRootAddress() + BaseConstants.SLASH_OS + "Config.xml")).exists()) {
			try (InputStream is = new FileInputStream(f1)) {
				localconfig = new Properties();
				localconfig.loadFromXML(is);
				BaseConstants.config.putAll(localconfig);
			} catch (Exception e) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			}
		}

	}

	public static String getConfig(final String pKey) {
		if (BaseConstants.config == null) {
			loadConfig();
		}
		return BaseConstants.config.getProperty(pKey);
	}

	public static void setConfig(final String pKey, final String pVale) {
		if (BaseConstants.config != null) {
			getConfig(pKey);
		}
		BaseConstants.config.setProperty(pKey, pVale);
	}

	public static Properties getSubConfigs(final String pPartialKey) {
		if (BaseConstants.config == null) {
			loadConfig();
		}
		Properties p = new Properties();
		for (Iterator<String> iterator = config.stringPropertyNames().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			if (key.startsWith(pPartialKey)) {
				String key2 = key.substring(pPartialKey.length());
				p.put(key2, config.getProperty(key));
			}
		}
		return p;
	}

	public static void saveConfig() {
		final File f1 = new File(String.valueOf(getRootAddress()) + BaseConstants.SLASH_OS + "Config.xml");
		try (final FileOutputStream fo = new FileOutputStream(f1)) {
			BaseConstants.config.storeToXML(fo, null);
		} catch (FileNotFoundException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		} catch (IOException e2) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e2.getMessage(), e2);
		}
	}

	public static String getString(final String pKey) {
		try {
			return BaseConstants.BUNDLE_MESSAGE.getString(pKey);
		} catch (MissingResourceException e) {
			return String.valueOf('!') + pKey + '!';
		}
	}

	public static String normalizeString(final String pUrl) {
		return Normalizer.normalize(pUrl, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

	public static String normalizeStringWithoutSpace(final String pUrl) {
		return normalizeString(pUrl).replace(" ", "");
	}

	public static String decodeUrlAsUTF8(final String pUrl) {
		try {
			return URLDecoder.decode(pUrl, CHAR_UTF_8);
		} catch (UnsupportedEncodingException h) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, h.getMessage(), h);
			return pUrl;
		}
	}

	public static String getRootAddress() {
		if (BaseConstants.rootAddress == null) {
			final ProtectionDomain pt = BaseConstants.class.getProtectionDomain();
			if (pt != null) {
				final URL location = pt.getCodeSource().getLocation();
				BaseConstants.rootAddress = decodeUrlAsUTF8(new File(location.getFile()).getParent());
			} else {
				final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				final URL location2 = classLoader.getResource("core/config/Config.properties");
				if (location2 != null) {
					BaseConstants.rootAddress = decodeUrlAsUTF8(
							new File(location2.getFile()).getParentFile().getParentFile().getParent());
				}
			}
		}
		return BaseConstants.rootAddress;
	}

	public static void setRootAddress(final String pRootAddress) {
		BaseConstants.rootAddress = decodeUrlAsUTF8(pRootAddress);
	}

	public static boolean isEmpty(Collection<?> pC) {
		return pC == null || pC.isEmpty();
	}

	public static Map<String, String> readParams(final String[] args) {
		Map<String, String> params = null;
		if (args != null && args.length > 0) {
			params = new Hashtable<>(args.length);
			for (final String value : args) {
				String kv[] = value.split(EQUAL);
				if (kv != null && kv.length > 1) {
					params.put(kv[0], kv[1]);
				}
			}
		}
		return params;
	}
}