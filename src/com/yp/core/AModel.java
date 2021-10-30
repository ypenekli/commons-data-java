package com.yp.core;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.yp.core.db.DbCommand;
import com.yp.core.db.DbConnInfo;
import com.yp.core.db.DbHandler;
import com.yp.core.db.IExport;
import com.yp.core.db.OnExportListener;
import com.yp.core.entity.EntityFactory;
import com.yp.core.entity.IDataEntity;
import com.yp.core.entity.IResult;
import com.yp.core.entity.Result;
import com.yp.core.excel.AXlsAktar;
import com.yp.core.log.MyLogger;
import com.yp.core.mail.Address;
import com.yp.core.tools.StringTool;
import com.yp.core.user.IUser;
import com.yp.core.web.JsonHandler;

public abstract class AModel<T> {

	protected String query;
	protected IHandler<T> handler;
	private boolean remotingEnabled;
	protected static Locale locale;
	protected String hosturl;
	protected final String dbid;
	protected String server;
	private String confServer;

	static {
		AModel.locale = Locale.getDefault();
	}

	public enum COLUMN_SEPERATOR {

		COMMA(";"), SEMICOLON(";"), SHARP("#");

		private String key = null;

		private COLUMN_SEPERATOR(String pKey) {
			key = pKey;
		}

		public String getKey() {
			return key;
		}
	}

	@SuppressWarnings("unchecked")
	public AModel(String pServer) {
		server = pServer;
		confServer = BaseConstants.getConfig(pServer);
		hosturl = BaseConstants.getConfig(confServer + DbHandler.DB_URL);
		remotingEnabled = hosturl != null && hosturl.startsWith("http");
		if (remotingEnabled)
			handler = new JsonHandler<>(hosturl, getClass());
		else
			handler = new DbHandler<>((Class<? extends AModel<T>>) this.getClass(), server);
		dbid = confServer.substring(BaseConstants.SERVER.length());
	}

	public AModel() {
		this(BaseConstants.SERVER);
	}

	public static void cleanFolder(String pFolderAddress) {
		File dF = new File(pFolderAddress);
		for (File f : dF.listFiles()) {
			if (f.isFile() && (System.currentTimeMillis() - f.lastModified()) > 120000)
				try {
					Files.delete(f.toPath());
				} catch (IOException e) {
					Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
				}
		}
	}

	public static String convertStreamToString(java.io.InputStream is) {
		if (is != null) {
			try (InputStreamReader ir = new InputStreamReader(is, StandardCharsets.UTF_8)) {
				return IOUtils.toString(ir);
			} catch (IOException e) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return "";
	}

	public static String convertStreamToString(java.io.InputStream is, String pCharset) {
		if (is != null) {
			try {
				return IOUtils.toString(is, pCharset);
			} catch (IOException e) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return "";
	}

	public static boolean deleteFile(String pFileAddress, String pFileName) {
		try {
			return Files.deleteIfExists(Paths.get(pFileAddress + BaseConstants.SLASH_OS + pFileName));
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			return false;
		}
	}

	public static IDataEntity find(List<? extends IDataEntity> pListe, IDataEntity pVs) {
		if (pListe != null) {
			for (IDataEntity vs : pListe) {
				if (vs.equals(pVs))
					return vs;
			}
			// return pListe.stream().filter(dVs -> dVs.equals(pVs)).findAny().orElse(null);
		}
		return null;
	}

	public static IDataEntity find(List<? extends IDataEntity> pListe, Object pKytnu) {
		if (pListe != null) {
			for (IDataEntity vs : pListe) {
				if (vs.equals(pKytnu))
					return vs;
			}
			// return pListe.stream().filter(dVs ->
			// dVs.equals(pKytnu)).findAny().orElse(null);
		}
		return null;
	}

	public static String getClientIP() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException h) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, h.getMessage(), h);
		}
		return "1.1.1.1";
	}

	public static byte[] getImage(File pFile) {
		InputStream is = null;
		try {
			InputStream i = new FileInputStream(pFile);
			is = new BufferedInputStream(i);
			byte[] b = new byte[is.available()];
			is.read(b);
			return b;
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
	}

	public static byte[] getImage(String pFileAddress) {
		return getImage(new File(pFileAddress));
	}

	public static String getJarFolder() {
		String name = AModel.class.getName().replace('.', '/');
		String s = AModel.class.getResource("/" + name + ".class").toString();
		s = s.replace('/', File.separatorChar);
		s = s.substring(0, s.indexOf(".jar") + 4);
		s = s.substring(s.lastIndexOf(':') - 1);
		return s.substring(0, s.lastIndexOf(File.separatorChar) + 1);
	}

	public static String getJarFolder(Class<?> pClass) {
		String name = pClass.getName().replace('.', '/');
		String s = pClass.getResource("/" + name + ".class").toString();
		s = s.replace('/', File.separatorChar);
		s = s.substring(0, s.indexOf(".jar") + 4);
		s = s.substring(s.lastIndexOf(':') - 1);
		return s.substring(0, s.lastIndexOf(File.separatorChar) + 1);
	}

	public void acceptAll(List<? extends IDataEntity> pList) {
		if (pList != null)
			for (IDataEntity vs : pList) {
				vs.accept();
			}
	}

	public static void setLastClientInfo(IDataEntity pVs, IUser pUser) {
		String email = "Admin@yp.com";
		if (pUser != null) {
			email = pUser.getEmail();
		}
		pVs.setLastClientInfo(email, getClientIP(), Calendar.getInstance().getTime());
	}

	public static void setClientInfo(IDataEntity pVs, IUser pUser) {
		String email = "Admin@yp.com";
		if (pUser != null) {
			email = pUser.getEmail();
		}
		pVs.setClientInfo(email, getClientIP(), Calendar.getInstance().getTime());
	}

	public static String zipFile(String pFileAddress, String pFileName) {
		FileInputStream dFOku = null;

		String zipFileFullName = pFileAddress + System.getProperty("file.separator")
				+ pFileName.substring(0, pFileName.lastIndexOf('.')) + ".zip";
		String fileFullName = pFileAddress + System.getProperty("file.separator") + pFileName;

		try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFileFullName))) {
			byte[] dBuf = new byte[1024];

			dFOku = new FileInputStream(fileFullName);
			zip.putNextEntry(new ZipEntry(pFileName));
			int len;
			while ((len = dFOku.read(dBuf)) > 0) {
				zip.write(dBuf, 0, len);
			}
			zip.closeEntry();
		} catch (Exception e) {
			zipFileFullName = null;
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		} finally {
			if (dFOku != null)
				try {
					dFOku.close();
				} catch (IOException e1) {
					Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e1.getMessage(), e1);
				}

		}

		return zipFileFullName;
	}

	public static List<IDataEntity> readFromTextFile(final String pInputFilePath, final String[] pCollNames,
			final boolean pHasCollNames, final COLUMN_SEPERATOR pColSep, Class<? extends IDataEntity> pClass) {
		List<IDataEntity> inputList = new ArrayList<>();
		try (Scanner scnr = new Scanner(new FileInputStream(pInputFilePath))) {
			if (pHasCollNames)
				scnr.nextLine();
			while (scnr.hasNextLine()) {
				String line = scnr.nextLine();
				String[] p = line.split(pColSep.key);// a CSV has comma separated lines
				IDataEntity vs = EntityFactory.newInstance(pClass);
				vs.load(pCollNames, p);
				vs.checkValues();
				inputList.add(vs);
			}
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return inputList;
	}

	public static List<IDataEntity> readFromTextFile8(final String pInputFilePath, final String[] pCollNames,
			final boolean pHasCollNames, final COLUMN_SEPERATOR pColSep, Class<? extends IDataEntity> pClass) {
		List<IDataEntity> inputList = new ArrayList<>();
		File inputF = new File(pInputFilePath);
		try (InputStream inputFS = new FileInputStream(inputF)) {
			BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
			// skip the header of the csv
			inputList = br.lines().skip(pHasCollNames ? 1 : 0).map(line -> {
				String[] p = line.split(pColSep.key);// a CSV has comma separated lines
				IDataEntity vs = EntityFactory.newInstance(pClass);
				vs.load(pCollNames, p);
				vs.checkValues();
				return vs;
			}).collect(Collectors.toList());
			br.close();
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return inputList;
	}

	public T find(final T pDataEntity) {
		return handler.find(pDataEntity);
	}

	public IDataEntity findOne(final DbCommand pQuery, Type pOutType) {
		return handler.findOne(pQuery, pOutType);
	}

	public T findOne(final DbCommand pQuery) {
		return handler.findOne(pQuery);
	}

	public List<IDataEntity> findAny(final DbCommand pQuery, final Type pOutType) {
		return handler.findAny(pQuery, pOutType);
	}

	public List<T> findAny(final DbCommand pQuery) {
		return handler.findAny(pQuery);
	}

	public IResult<T> save(final T pData) {
		return handler.save(pData);
	}

	public IResult<IDataEntity> save(final IDataEntity pData) {
		return handler.save(pData);
	}

	public IResult<List<T>> saveAll(final List<T> pData) {
		return handler.saveAll(pData);
	}

	public IResult<String> execute(final DbCommand pQuery) {
		return handler.execute(pQuery);
	}

	public IResult<String> executeAll(final DbCommand... pQueries) {
		return handler.executeAll(pQueries);
	}

	public IResult<String> saveAtomic(final Object... pParams) {
		return handler.saveAtomic(pParams);
	}

	public List<IDataEntity> findDbTables(String pLibrary, String pSchema) {
		return handler.findDbTables(pLibrary, pSchema);
	}

	public IResult<IExport> exportDb(DbConnInfo pTarget, IExport pExport, OnExportListener proceedListener) {
		return handler.exportDb(pTarget, pExport, proceedListener);
	}

	public IResult<AXlsAktar> exportToXls(final DbCommand pQuery, Type pOutType, AXlsAktar pXls) {
		return handler.exportToXls(pQuery, pOutType, pXls);
	}

	public IResult<AXlsAktar> exportToXls(List<? extends IDataEntity> pList, AXlsAktar pXls) {
		IResult<AXlsAktar> res = new Result<>();
		if (!BaseConstants.isEmpty(pList)) {
			res.setSuccess(true);
			pXls.yaz(pList);
			// list.forEach(vs -> pXls.yukle(vs));
			res.setData(pXls);
		}
		return res;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void exportXlsToDb(final String pFileName, IDataEntity[] pTables) {
		List<IDataEntity> addList = new ArrayList<>();
		List<IDataEntity> list = new ArrayList<>();

		String clientName = "11111";
		String ckientIp = getClientIP();
		Date datetime = new Date();
		try (InputStream file = BaseConstants.class.getResourceAsStream(pFileName)) {
			try (org.apache.poi.ss.usermodel.Workbook dWb = WorkbookFactory.create(file)) {
				for (int dI = 0; dI < pTables.length; dI++) {
					Class<? extends IDataEntity> classs = pTables[dI].getClass();
					list = readFromExcelWorkbook(dWb, pTables[dI].getTableName(), null, true, classs);
					if (!BaseConstants.isEmpty(list)) {
						for (IDataEntity de : list) {
							IDataEntity add = classs.getConstructor(IDataEntity.class).newInstance(de);
							add.setLastClientInfo(clientName, ckientIp, datetime);
							addList.add(add);
						}
						saveAll((List) addList);
						addList.clear();
						list.clear();
					}
				}
			}

		} catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}

	}

	public IResult<String> executeSQLfromResourceFile(final String pSqlFileFullName) {
		return executeSQLfromString(convertStreamToString(BaseConstants.class.getResourceAsStream(pSqlFileFullName)));
	}

	public IResult<String> executeSQLfromFile(final String pSqlFileFullName) {
		try {
			return executeSQLfromString(convertStreamToString(Files.newInputStream(Paths.get(pSqlFileFullName))));
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	public IResult<String> executeSQLfromString(final String pSql) {
		IResult<String> res;
		if (!StringTool.isNull(pSql)) {
			String[] dKomutDizi = pSql.split(";");
			ArrayList<DbCommand> dSrgListe = new ArrayList<>();
			int i = 1;
			for (String q : dKomutDizi) {
				if (!StringTool.isNull(q)) {
					q = q.trim();
					if (!q.startsWith("--") && q.length() > 5) {
						DbCommand cmd = new DbCommand("sql." + i++);
						cmd.setQuery(q);
						dSrgListe.add(cmd);
					}
				}
			}
			DbCommand[] queries = new DbCommand[dSrgListe.size()];
			res = handler.executeAll(dSrgListe.toArray(queries));
		} else {
			res = new Result<>();
			res.setMessage(BaseConstants.getString("1018"));
		}
		return res;
	}

	public boolean isRemotingEnabled() {
		return remotingEnabled;
	}

	private static IDataEntity loadCell(Row row, final String[] pCollNames, Class<? extends IDataEntity> pClass) {
		Object[] values = new Object[pCollNames.length];
		for (int dI = 0; dI < pCollNames.length; dI++) {
			Cell cell = row.getCell(dI);
			if (cell == null)
				values[dI] = null;
			else if (cell.getCellType() == CellType.NUMERIC)
				values[dI] = cell.getNumericCellValue();
			else
				values[dI] = cell.getStringCellValue();
		}
		IDataEntity newDe = EntityFactory.newInstance(pClass);
		newDe.load(pCollNames, values);
		newDe.checkValues();
		return newDe;
	}

	private static String[] readColumunNames(Row row) {
		java.util.Iterator<Cell> cellIterator = row.cellIterator();
		List<String> collNameList = new ArrayList<>();
		while (cellIterator.hasNext()) {
			collNameList.add(cellIterator.next().getStringCellValue());
		}
		String[] collNames = new String[collNameList.size()];
		collNameList.toArray(collNames);
		return collNames;
	}

	public static List<IDataEntity> readFromExcelWorkbook(org.apache.poi.ss.usermodel.Workbook pWorkbook,
			String pSheetName, final String[] pCollNames, final boolean pHasCollNames,
			Class<? extends IDataEntity> pClass) {
		List<IDataEntity> res = new ArrayList<>();
		org.apache.poi.ss.usermodel.Sheet sheet = null;
		String[] collNames = pCollNames;
		if (StringTool.isNull(pSheetName))
			sheet = pWorkbook.getSheetAt(0);
		else
			sheet = pWorkbook.getSheet(pSheetName.toLowerCase());
		if (sheet != null) {
			Iterator<Row> rowIterator = sheet.iterator();
			if (rowIterator.hasNext()) {
				if (pHasCollNames && collNames == null) {
					collNames = readColumunNames(rowIterator.next());
				}
				while (rowIterator.hasNext()) {
					res.add(loadCell(rowIterator.next(), collNames, pClass));
				}
			}
		}
		return res;

	}

	public static List<IDataEntity> readFromExcelFile(String pInputFilePath, String pSheetName,
			final String[] pCollNames, final boolean pHasCollNames, Class<? extends IDataEntity> pClass) {
		List<IDataEntity> dListe = null;
		try (FileInputStream file = new FileInputStream(new File(pInputFilePath))) {
			org.apache.poi.ss.usermodel.Workbook dWb = WorkbookFactory.create(file);
			dListe = readFromExcelWorkbook(dWb, pSheetName, pCollNames, pHasCollNames, pClass);
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return dListe;
	}

	public static List<IDataEntity> load(final List<? extends IDataEntity> pList, Class<? extends IDataEntity> pClass) {
		List<IDataEntity> result = null;
		if (pList != null) {
			result = new ArrayList<>(pList.size());
			for (IDataEntity de : pList) {
				result.add(EntityFactory.newInstance(pClass).load(de));
			}
		}
		return result;
	}

	private IResult<String> validateSenMail(final Address pFrom, Address pReplyTo, String pSubject, String pBody,
			List<Address> pTo) {
		return new Result<>(true, "");
	}

	public IResult<String> sendMail(Address pFrom, Address pReplyTo, String pSubject, String pBody, List<Address> pTo,
			List<Address> pBcc, List<Address> pCc, List<Address> pAttachement) {
		IResult<String> res = validateSenMail(pFrom, pReplyTo, pSubject, pBody, pTo);
		if (res.isSuccess()) {
			FnParam from = new FnParam("from", pFrom);
			FnParam replyto = new FnParam("replyto", pReplyTo == null ? pFrom : pReplyTo);
			FnParam subject = new FnParam("subject", pSubject);
			FnParam body = new FnParam("body", pBody);
			FnParam to = new FnParam("to", pTo);

			FnParam bcc = new FnParam("bcc", pBcc);
			FnParam cc = new FnParam("cc", pCc);
			FnParam attachemts = new FnParam("attachemts", pAttachement);
			return handler.sendMail(from, replyto, subject, body, to, bcc, cc, attachemts);
		}
		return res;

	}
}