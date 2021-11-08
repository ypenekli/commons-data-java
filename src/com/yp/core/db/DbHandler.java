package com.yp.core.db;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.sql.DataSource;

import com.yp.core.AModel;
import com.yp.core.BaseConstants;
import com.yp.core.FnParam;
import com.yp.core.IHandler;
import com.yp.core.db.OnExportListener.PHASE;
import com.yp.core.entity.DataEntity;
import com.yp.core.entity.EntityFactory;
import com.yp.core.entity.IDataEntity;
import com.yp.core.entity.IElement;
import com.yp.core.entity.IResult;
import com.yp.core.entity.Result;
import com.yp.core.excel.AXlsAktar;
import com.yp.core.log.MyLogger;
import com.yp.core.sec.StringEncrypter;
import com.yp.core.tools.StringTool;

public class DbHandler<T> implements IHandler<T> {

	public static final String DB_DATASOURCE = ".DS";
	public static final String DB_URL = ".DBURL";
	public static final String DB_USER = ".DBUSER";
	public static final String DB_PASSWORD = ".DBPASSWORD";
	public static final String DB_DRIVER = ".DBDRIVER";
	public static final String DB_SCHEMASEPERATOR = ".DBSCHEMASEPERATOR";
	public static final String DB_SCHEMAS = ".DBSCHEMAS.";
	public static final String INSERT_INTO = " INSERT INTO ";
	public static final String WHERE = " WHERE ";
	public static final String SELECT = " SELECT ";
	public static final String FROM = " FROM ";
	public static final String SELECT_FROM = " SELECT * FROM ";
	public static final String SELECT_COUNT = " SELECT COUNT(*) AS COUNT ";
	public static final String SELECT_COUNT_FROM = " SELECT COUNT(*) AS COUNT FROM ";
	public static final String UPDATE = " UPDATE ";
	public static final String DELETE = " DELETE ";
	public static final String VALUES = " VALUES ";
	public static final String SET = " SET ";
	public static final String AND = " AND ";
	public static final String REGEX_SCHEMA_SEPERATOR = "~KA~";

	private Connection connection;

	private ArrayList<String> paramList;

	private ArrayList<Object> valueList;

	private int fieldCount = 0;
	private String[] fieldNames;

	private String query;
	private String countQuery;

	private Class<? extends AModel<T>> callerClass;

	private String schemaSeperator = ".";
	private Properties schemas;
	private Properties config;
	private DataSource dataSource;
	private String url;

	private static final String TEST = "lombar@Set#Sonra";
	private static final String USER = "user";
	private static final String PWD = "password";

	private static final String COUNT = "count";

	public DbHandler(Class<? extends AModel<T>> pCallerClass, String pServer) {
		builtConnection(pServer, null);
		loadSchemas(pServer);
		callerClass = pCallerClass;
		paramList = new ArrayList<>();
		valueList = new ArrayList<>();
	}

	public DbHandler(Class<? extends AModel<T>> pCallerClass) {
		this(pCallerClass, null);
	}

	private String getSchemaName(IDataEntity pE) {
		return (String) schemas.getOrDefault(pE.getSchemaName(), pE.getSchemaName());
	}

	private void loadSchemas(String pServer) {
		schemas = new Properties();
		String server = BaseConstants.getConfig(!StringTool.isNull(pServer) ? pServer : BaseConstants.SERVER);
		schemas = BaseConstants.getSubConfigs(server + DB_SCHEMAS);
	}

	private void builtConnection(String pServer, String pDbFileName) {
		config = new Properties();
		String server = BaseConstants.getConfig(!StringTool.isNull(pServer) ? pServer : BaseConstants.SERVER);

		String dataSourceName = BaseConstants.getConfig(server + DB_DATASOURCE);
		if (!StringTool.isNull(dataSourceName)) {
			try {
				javax.naming.Context ctx = new javax.naming.InitialContext(config);
				dataSource = (DataSource) ctx.lookup(dataSourceName);
			} catch (NamingException h) {
				dataSourceName = null;
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, h.getMessage(), h);
			}
		}

		if (StringTool.isNull(dataSourceName)) {
			String temp = BaseConstants.getConfig(server + DB_SCHEMASEPERATOR);
			if (temp != null)
				schemaSeperator = temp;
			url = BaseConstants.getConfig(server + DB_URL);
			if (!StringTool.isNull(pDbFileName))
				url += pDbFileName;
			String driver = BaseConstants.getConfig(server + DB_DRIVER);
			setUser(BaseConstants.getConfig(server + DB_USER));
			setPassword(BaseConstants.getConfig(server + DB_PASSWORD));

			try {
				Class.forName(driver);// .newInstance();
			} catch (ClassNotFoundException e) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			}

		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public T find(T pDataEntity) {
		IDataEntity de = null;
		generateSellectCommand((IDataEntity) pDataEntity);
		if (query != null) {
			boolean closed = connection == null;
			PreparedStatement ps = null;
			try {
				if (closed)
					connection = getConnection();
				if (connection != null) {
					ps = connection.prepareStatement(query);
					for (int i = 0; i < paramList.size(); i++) {
						ps.setObject(i + 1, valueList.get(i));
					}
					de = fetchData(ps, (IDataEntity) pDataEntity);
					ps.clearParameters();
				}
			} catch (SQLException e) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			} finally {
				if (ps != null) {
					try {
						ps.close();
					} catch (SQLException e) {
						Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
					}
				}
				close(closed);
			}
		}
		return (T) de;
	}

	private IDataEntity readOne(Type pOutType) {
		IDataEntity de = null;
		if (connection != null) {
			try (PreparedStatement ps = connection.prepareStatement(query);) {
				for (int i = 0; i < paramList.size(); i++) {
					ps.setObject(i + 1, valueList.get(i));
				}
				de = fetchData(ps, pOutType);
				ps.clearParameters();

			} catch (SQLException e) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return de;
	}

	private IDataEntity findCount() {
		IDataEntity de = null;
		if (connection != null) {
			try (PreparedStatement ps = connection.prepareStatement(countQuery);) {
				for (int i = 0; i < paramList.size(); i++) {
					ps.setObject(i + 1, valueList.get(i));
				}
				de = fetchData(ps, DataEntity.class);
				ps.clearParameters();

			} catch (SQLException e) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return de;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T findOne(DbCommand pQuery) {
		IDataEntity de = null;
		refreshValues(pQuery, false);
		if (query != null) {
			boolean closed = connection == null;
			try {
				if (closed)
					connection = getConnection();
				de = readOne(getTypeParameterClass());
			} catch (SQLException e) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			} finally {
				close(closed);
			}
		}
		return (T) de;
	}

	@Override
	public IDataEntity findOne(DbCommand pQuery, Type pOutType) {
		IDataEntity de = null;
		refreshValues(pQuery, false);
		if (query != null) {
			boolean closed = connection == null;
			try {
				if (closed)
					connection = getConnection();
				de = readOne(pOutType);
			} catch (SQLException e) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			} finally {
				close(closed);
			}
		}
		return de;
	}

	private List<IDataEntity> readAny(Type pOutType) {
		ArrayList<IDataEntity> list = null;
		if (connection != null) {
			try (PreparedStatement ps = connection.prepareStatement(query);) {
				for (int i = 0; i < paramList.size(); i++) {
					ps.setObject(i + 1, valueList.get(i));
				}
				list = fetchAllData(ps, pOutType);
				ps.clearParameters();

			} catch (SQLException e) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return list;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> findAny(DbCommand pQuery) {
		List<T> list = null;
		refreshValues(pQuery, false);
		if (query != null) {
			boolean closed = connection == null;
			try {
				if (closed)
					connection = getConnection();
				list = (List) readAny(getTypeParameterClass());

			} catch (SQLException e) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			} finally {
				close(closed);
			}
		}
		return list;
	}

	@Override
	public List<IDataEntity> findAny(DbCommand pQuery, Type pOutType) {
		List<IDataEntity> list = null;
		refreshValues(pQuery, false);
		if (query != null) {
			boolean closed = connection == null;
			try {
				if (closed)
					connection = getConnection();
				list = readAny(pOutType);
			} catch (SQLException e) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			} finally {
				close(closed);
			}
		}
		return list;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public IResult<List<T>> findPageAny(DbCommand pQuery, Pager pPager) {
		IResult<List<T>> result = new Result<>();
		int count = pPager.getLength();
		refreshValues(pQuery, count < 0);

		if (query != null) {
			boolean closed = connection == null;
			try {
				if (closed)
					connection = getConnection();

				if (count < 0) {
					IDataEntity c = findCount();
					if (c != null && !c.isNull(COUNT)) {
						count = (int) c.get(COUNT);
						result.setDataLength(count);
						pPager.setLength(count);
					}
				}
				result.setData((List) readAny(getTypeParameterClass()));
				result.setSuccess(true);
			} catch (SQLException e) {
				result.setSuccess(false);
				result.setMessage(BaseConstants.MESSAGE_CONNECTION_ERROR);
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			} finally {
				close(closed);
			}
		}
		return result;
	}

	@Override
	public IResult<List<IDataEntity>> findPageAny(DbCommand pQuery, Type pOutType, Pager pPager) {
		IResult<List<IDataEntity>> result = new Result<>();
		int count = pPager.getLength();
		refreshValues(pQuery, count < 0);
		if (query != null) {
			boolean closed = connection == null;
			try {
				if (closed)
					connection = getConnection();

				if (count < 0) {
					IDataEntity c = findCount();
					if (c != null && !c.isNull("count")) {
						count = (int) c.get("count");
						result.setDataLength(count);
						pPager.setLength(count);
					}
				}
				result.setData(readAny(pOutType));
				result.setSuccess(true);
			} catch (SQLException e) {
				result.setSuccess(false);
				result.setMessage(BaseConstants.MESSAGE_CONNECTION_ERROR);
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			} finally {
				close(closed);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IResult<T> save(T pDataEntity) {
		return (IResult<T>) save((IDataEntity) pDataEntity);
	}

	@Override
	public IResult<IDataEntity> save(IDataEntity pDataEntity) {
		IResult<IDataEntity> result = new Result<>();
		result.setData(pDataEntity);
		if (pDataEntity.isNew()) {
			checkAndGeneratePk(pDataEntity);
			generateInsertCommand(pDataEntity);
		} else if (pDataEntity.isUpdated()) {
			generateUpdateCommand(pDataEntity);
		} else if (pDataEntity.isDeleted()) {
			generateDeleteCommand(pDataEntity);
		}
		if (query != null) {
			boolean closed = connection == null;
			try {
				if (closed)
					connection = getConnection();
				update(result);
			} catch (SQLException e) {
				result.setSuccess(false);
				result.setMessage(BaseConstants.MESSAGE_SAVE_ERROR);
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			} finally {
				clear();
				close(closed);
			}
		}
		return result;
	}

	private void update(IResult<IDataEntity> result) {
		if (connection != null) {
			try (PreparedStatement ps = connection.prepareStatement(query)) {
				ParameterMetaData pm = ps.getParameterMetaData();
				setParameters(ps, pm);
				if (ps.executeUpdate() > 0) {
					result.setSuccess(true);
					result.setMessage(BaseConstants.MESSAGE_SAVE_SUCCEEDED);
				} else {
					result.setSuccess(false);
					result.setMessage(BaseConstants.MESSAGE_SAVE_ERROR);
				}
				ps.clearParameters();
			} catch (SQLException e) {
				result.setSuccess(false);
				result.setMessage(BaseConstants.MESSAGE_SAVE_ERROR);
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	private void updateAll(IResult<List<T>> result, List<T> pDataList) {
		int success = 0;
		if (connection != null) {
			try (PreparedStatement ps = connection.prepareStatement(query)) {
				connection.setAutoCommit(false);
				ParameterMetaData pm = ps.getParameterMetaData();
				setParameters(ps, pm);
				success = ps.executeUpdate();
				ps.clearParameters();
				for (int j = 1; success > 0 && j < pDataList.size(); j++) {
					refreshValues((IDataEntity) pDataList.get(j));
					setParameters(ps, pm);
					success = ps.executeUpdate();
					ps.clearParameters();
				}
				if (success > 0) {
					result.setSuccess(true);
					result.setMessage(BaseConstants.MESSAGE_SAVE_SUCCEEDED);
				} else {
					result.setSuccess(false);
					result.setMessage(BaseConstants.MESSAGE_SAVE_ERROR);
				}
			} catch (SQLException e) {
				result.setSuccess(false);
				result.setMessage(BaseConstants.MESSAGE_SAVE_ERROR);
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	private void rollback(boolean closed) {
		if (closed && connection != null) {
			try {
				connection.rollback();
			} catch (Exception e2) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e2.getMessage(), e2);
			}

			try {
				connection.close();
			} catch (Exception e2) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e2.getMessage(), e2);
			}
			connection = null;
		}
	}

	private void commit(boolean closed) {
		if (closed && connection != null) {
			try {
				connection.commit();
			} catch (Exception e2) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e2.getMessage(), e2);
			}
			try {
				connection.close();
			} catch (Exception e2) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e2.getMessage(), e2);
			}
			connection = null;
		}
	}

	private void close(boolean closed) {
		if (closed && connection != null) {
			try {
				connection.close();
			} catch (Exception e2) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e2.getMessage(), e2);
			}
			connection = null;
		}
	}

	private void close(Connection pConnection, boolean closed) {
		if (closed && pConnection != null) {
			try {
				pConnection.close();
			} catch (Exception e2) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e2.getMessage(), e2);
			}
			pConnection = null;
		}
	}

	@Override
	public IResult<List<T>> saveAll(List<T> pDataList) {
		IResult<List<T>> result = new Result<>();
		result.setData(pDataList);
		if (!BaseConstants.isEmpty(pDataList)) {
			IDataEntity de = (IDataEntity) pDataList.get(0);
			if (de.isNew())
				generateInsertCommand(de);
			else if (de.isUpdated())
				generateUpdateCommand(de);
			else if (de.isDeleted())
				generateDeleteCommand(de);

			boolean closed = connection == null;
			try {
				if (query != null) {
					if (closed)
						connection = getConnection();
					updateAll(result, pDataList);
					if (!result.isSuccess()) {
						rollback(closed);
					}
				}
			} catch (SQLException e) {
				result.setSuccess(false);
				result.setMessage(BaseConstants.MESSAGE_SAVE_ERROR);
				rollback(closed);
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			} finally {
				commit(closed);
			}

		}

		return result;

	}

	private void execute(IResult<String> result) {
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ParameterMetaData pm = ps.getParameterMetaData();
			setParameters(ps, pm);
			ps.execute();
			int count = ps.getUpdateCount();
			if (count == -1 || count > 0) {
				result.setSuccess(true);
				result.setMessage(BaseConstants.MESSAGE_SAVE_SUCCEEDED);
			} else {
				result.setSuccess(false);
				result.setMessage(BaseConstants.MESSAGE_SAVE_ERROR);
			}
			ps.clearParameters();
		} catch (SQLException e) {
			result.setSuccess(false);
			result.setMessage(BaseConstants.MESSAGE_SAVE_ERROR);
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Override
	public IResult<String> execute(DbCommand pQuery) {
		IResult<String> result = new Result<>();
		refreshValues(pQuery, false);
		if (query != null) {
			boolean closed = connection == null;
			try {
				if (closed)
					connection = getConnection();
				if (connection != null) {
					execute(result);
				}
			} catch (SQLException e) {
				result.setSuccess(false);
				result.setMessage(BaseConstants.MESSAGE_SAVE_ERROR);
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			} finally {
				close(closed);
			}
		}

		return result;
	}

	@Override
	public IResult<String> executeAll(DbCommand... pQueries) {
		IResult<String> result = new Result<>();
		boolean closed = connection == null;
		try {
			if (pQueries != null && pQueries.length > 0) {
				if (closed)
					connection = getConnection();
				if (connection != null) {
					connection.setAutoCommit(false);

					for (int j = 0; j < pQueries.length; j++) {
						refreshValues(pQueries[j], false);
						if (query != null) {
							execute(result);
						}
					}
				}
			}

		} catch (SQLException e) {
			result.setSuccess(false);
			result.setMessage(BaseConstants.MESSAGE_SAVE_ERROR);
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			rollback(closed);
		} finally {
			commit(closed);
		}

		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public IResult<String> saveAtomic(Object... pParams) {
		IResult<String> result = new Result<>();
		if (pParams != null && pParams.length > 0) {
			IResult success = new Result<>();
			try {
				connection = getConnection();
				if (connection != null) {
					connection.setAutoCommit(false);
					success.setSuccess(true);
					for (int i = 0; success.isSuccess() && i < pParams.length; i++) {
						Object o = pParams[i];
						if (o != null) {
							if (ArrayList.class.isAssignableFrom(o.getClass())) {
								ArrayList<IDataEntity> list = (ArrayList) o;
								if (!list.isEmpty())
									success = saveAll((ArrayList) list);
							} else if (IDataEntity[].class.isAssignableFrom(o.getClass())) {
								IDataEntity[] array = (IDataEntity[]) o;
								if (array.length > 0)
									for (int j = 0; success.isSuccess() && j < array.length; j++) {
										IDataEntity de = array[j];
										if (de != null && !de.isUnchanged())
											success = save(de);
									}
							} else if (IDataEntity.class.isAssignableFrom(o.getClass())) {
								IDataEntity de = (IDataEntity) o;
								if (de != null && !de.isUnchanged())
									success = save(de);
							} else if (DbCommand[].class.isAssignableFrom(o.getClass())) {
								DbCommand[] cmdArray = (DbCommand[]) o;
								success = executeAll(cmdArray);
							} else if (DbCommand.class.isAssignableFrom(o.getClass())) {
								DbCommand cmd = (DbCommand) o;
								success = execute(cmd);
							}
						}

					}

					if (success.isSuccess()) {
						result.setSuccess(true);
						result.setMessage(BaseConstants.MESSAGE_SAVE_SUCCEEDED);
					} else {
						result.setSuccess(false);
						result.setMessage(BaseConstants.MESSAGE_SAVE_ERROR);
						rollback(true);
					}
				}

			} catch (SQLException e) {
				result.setSuccess(false);
				result.setMessage(BaseConstants.MESSAGE_SAVE_ERROR);
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
				rollback(true);
			} finally {
				commit(true);
			}
		}

		return result;
	}

	private String generatePkQuery(IDataEntity pEntity, List<String> pkList) {
		StringBuilder select = new StringBuilder(SELECT);
		StringBuilder where = new StringBuilder(WHERE);
		for (Map.Entry<String, IElement> e : pEntity.getPrimaryKeys().entrySet()) {
			if (e.getValue().getValue() == null || "-1".equals(e.getValue().getValue().toString())) {
				select.append(" MAX(").append(e.getKey()).append(") + 1 AS ").append(e.getKey())
						.append(BaseConstants.COMMA_WITH_SPACE);
				pkList.add(e.getKey());
			} else {
				where.append(e.getKey()).append(" = ?").append(AND);
				paramList.add(e.getKey());
				valueList.add(e.getValue().getValue());
			}
		}
		if (select.length() > SELECT.length()) {
			select.setLength(select.length() - BaseConstants.COMMA_WITH_SPACE.length());
			select.append(FROM);
			select.append(getSchemaName(pEntity)).append(schemaSeperator).append(pEntity.getTableName());
			if (where.length() > WHERE.length()) {
				where.setLength(where.length() - AND.length());
				select.append(where);
			}
			return select.toString();
		}
		return null;
	}

	private void checkAndGeneratePk(IDataEntity pEntity) {
		try {
			clear();
			ArrayList<String> pkList = new ArrayList<>();
			if (pEntity.getPrimaryKeys().size() > 0) {
				query = generatePkQuery(pEntity, pkList);
				if (query != null) {
					IDataEntity vs = findPk(query);
					for (String key : pkList) {
						if (vs != null && !vs.isNull(key))
							pEntity.set(key, vs.get(key));
						else
							pEntity.set(key, 0);
					}
				}
			}
		} finally {
			clear();
		}
	}

	public IDataEntity findPk(String pQuery) {
		IDataEntity de = null;
		if (pQuery != null) {
			query = pQuery;
			boolean closed = connection == null;
			try {
				if (closed)
					connection = getConnection();
				de = readOne(DataEntity.class);
			} catch (SQLException e) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			} finally {
				close(closed);
			}
		}
		return de;
	}

	public java.lang.String getUser() {
		return config.getProperty(USER);
	}

	public java.lang.String getPassword() {
		return config.getProperty(PWD);
	}

	public void setUser(String pUser) {
		if (!StringTool.isNull(pUser))
			config.setProperty(USER, pUser);
	}

	public void setPassword(String pPassword) {
		if (!StringTool.isNull(pPassword))
			config.setProperty(PWD, decrypt(pPassword));
	}

	public static String decrypt(String pString) {
		StringEncrypter se = new StringEncrypter(TEST);
		return se.decrypt(pString);
	}

	public static String encrypt(String pString) {
		StringEncrypter se = new StringEncrypter(TEST);
		return se.encrypt(pString);
	}

	private void generateSellectCommand(IDataEntity pDataEntity) {
		checkFields(pDataEntity);
		StringBuilder sb = new StringBuilder(SELECT_FROM);
		sb.append(getSchemaName(pDataEntity)).append(schemaSeperator).append(pDataEntity.getTableName());
		query = appendFilter(pDataEntity, sb);
	}

	private void generateDeleteCommand(IDataEntity pDataEntity) {
		checkFields(pDataEntity);
		StringBuilder sb = new StringBuilder(DELETE);
		sb.append(FROM).append(getSchemaName(pDataEntity)).append(schemaSeperator).append(pDataEntity.getTableName());
		query = appendFilter(pDataEntity, sb);
	}

	private String generateInsertCommand(String[] fieldNames, String pTableFullName) {
		if (fieldNames.length > 0) {
			StringBuilder sb = new StringBuilder(INSERT_INTO);
			sb.append(pTableFullName);
			sb.append(" (");
			StringBuilder values = new StringBuilder(VALUES);
			values.append(" (");
			for (String k : fieldNames) {
				sb.append(k).append(", ");
				values.append("?, ");
			}
			sb.setLength(sb.length() - ", ".length());
			sb.append(") ");
			values.setLength(values.length() - ", ".length());
			values.append(") ");
			sb.append(values);

			return sb.toString();
		}
		return null;
	}

	private void generateInsertCommand(IDataEntity pDataEntity) {
		checkFields(pDataEntity);
		if (!paramList.isEmpty()) {
			StringBuilder sb = new StringBuilder(INSERT_INTO);
			sb.append(getSchemaName(pDataEntity)).append(schemaSeperator).append(pDataEntity.getTableName());
			sb.append(" (");
			StringBuilder values = new StringBuilder(VALUES);
			values.append(" (");
			for (String k : paramList) {
				sb.append(k).append(", ");
				values.append("?, ");
			}
			sb.setLength(sb.length() - ", ".length());
			sb.append(") ");
			values.setLength(values.length() - ", ".length());
			values.append(") ");
			sb.append(values);

			query = sb.toString();
		}
	}

	private void generateUpdateCommand(IDataEntity pDataEntity) {
		checkFields(pDataEntity);
		if (!paramList.isEmpty()) {
			StringBuilder sb = new StringBuilder(UPDATE);
			sb.append(getSchemaName(pDataEntity)).append(schemaSeperator).append(pDataEntity.getTableName());
			sb.append(SET);
			for (String k : paramList) {
				sb.append(k).append(" = ?, ");
			}
			sb.setLength(sb.length() - ", ".length());
			query = appendFilter(pDataEntity, sb);
		}
	}

	private void refreshValues(IDataEntity pDataEntity) {
		int countPK = pDataEntity.getPrimaryKeys().size();
		int i = 0;
		if (!pDataEntity.isDeleted()) {
			for (i = 0; i < fieldCount; i++) {
				valueList.set(i, pDataEntity.get(paramList.get(i)));
			}
		}
		if (!pDataEntity.isNew()) {
			Map<String, IElement> pk = pDataEntity.getPrimaryKeys();
			for (int j = 0; j < countPK; j++) {
				valueList.set(i + j, pk.get(paramList.get(i + j)).getValue());
			}
		}
	}

	private void refreshValues(DbCommand pQuery, boolean pCount) {
		query = null;
		valueList.clear();
		paramList.clear();
		FnParam[] ps = pQuery.getParams();
		if (ps != null && ps.length > 0)
			for (FnParam fp : pQuery.getParams()) {
				paramList.add(fp.getName());
				valueList.add(fp.getValue());
			}
		query = pQuery.getQuery().replace(REGEX_SCHEMA_SEPERATOR, schemaSeperator);
		for (Iterator<Entry<Object, Object>> iterator = schemas.entrySet().iterator(); iterator.hasNext();) {
			Entry<Object, Object> e = iterator.next();
			query = query.replaceAll((String) e.getKey(), (String) e.getValue());
		}
		if (pCount) {
			countQuery = SELECT_COUNT + query.substring(query.indexOf(FROM));
			System.out.println("select count :" + countQuery);
		}
	}

	private void clear() {
		query = null;
		fieldCount = 0;
		paramList.clear();
		valueList.clear();
	}

	private void checkFields(IDataEntity pDataEntity) {
		clear();
		if (pDataEntity.isNew() || pDataEntity.isUpdated())
			for (Map.Entry<String, IElement> e : pDataEntity.getFields().entrySet()) {
				if (e.getValue() != null && !e.getValue().isReadOnly() && e.getValue().isChanged()) {
					paramList.add(e.getKey());
					valueList.add(e.getValue().getValue());
					fieldCount += 1;
				}

			}
	}

	private String appendFilter(IDataEntity pDataEntity, StringBuilder pCommand) {
		if (pDataEntity.getPrimaryKeys().size() > 0) {
			pCommand.append(WHERE);
			for (Map.Entry<String, IElement> e : pDataEntity.getPrimaryKeys().entrySet()) {
				pCommand.append(e.getKey()).append(" = ?").append(AND);
				paramList.add(e.getKey());
				valueList.add(e.getValue().getValue());

			}
			pCommand.setLength(pCommand.length() - AND.length());
		}
		return pCommand.toString();
	}

	public Connection getConnection() throws SQLException {
		if (dataSource != null)
			return dataSource.getConnection();
		else {
			if (config.size() > 0)
				return DriverManager.getConnection(url, config);
			else
				return DriverManager.getConnection(url);
		}
	}

	public Connection getConnection(String pUser, String pPassword) throws SQLException {
		setUser(pUser);
		setPassword(pPassword);
		return getConnection();
	}

	public Connection getConnection(DbConnInfo pConnconf) throws SQLException {
		Properties config1 = new Properties();
		config1.setProperty(USER, pConnconf.getDbUser());
		config1.setProperty(PWD, decrypt(pConnconf.getDbPassword()));

		return DriverManager.getConnection(pConnconf.getDbUrl(), config1);
	}

	private void generateFieldNames(ResultSetMetaData pRsMd) {
		try {
			fieldNames = new String[pRsMd.getColumnCount()];
			for (int dI = 0; dI < fieldNames.length; dI++) {
				fieldNames[dI] = pRsMd.getColumnName(dI + 1).toLowerCase(Locale.US);
			}
		} catch (SQLException h) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, h.getMessage(), h);
		}
	}

	private IDataEntity fetchData(PreparedStatement pPs, Type pType) {
		IDataEntity de = null;
		try (ResultSet rs = pPs.executeQuery()) {
			generateFieldNames(rs.getMetaData());
			if (rs.next() && (de = EntityFactory.newInstance(pType)) != null) {
				de.load(fieldNames, rs);
				de.checkValues();
				de.accept();
				de.setRowNum(1);
			} // @deger while sonu
		} catch (SQLException e) {
			de = null;
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return de;
	}

	private IDataEntity fetchData(PreparedStatement pPs, IDataEntity pDataEntity) {
		IDataEntity de = null;
		try (ResultSet rs = pPs.executeQuery()) {
			generateFieldNames(rs.getMetaData());
			if (rs.next() && (de = EntityFactory.newInstance(pDataEntity.getClass())) != null) {
				de.load(fieldNames, rs);
				de.checkValues();
				de.accept();
				de.setRowNum(1);
			} // @deger while sonu
		} catch (SQLException e) {
			de = null;
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return de;
	}

	private ArrayList<IDataEntity> fetchAllData(ResultSet pRs, Type pType) throws SQLException {
		IDataEntity de = null;
		ArrayList<IDataEntity> list = new ArrayList<>();
		generateFieldNames(pRs.getMetaData());
		int i = 1;
		while (pRs.next() && (de = EntityFactory.newInstance(pType)) != null) {
			de.load(fieldNames, pRs);
			de.checkValues();
			de.accept();
			de.setRowNum(i++);
			list.add(de);
		} // @deger while sonu

		return list;
	}

	private ArrayList<IDataEntity> fetchAllData(PreparedStatement pPs, Type pType) {
		IDataEntity de = null;
		ArrayList<IDataEntity> list = new ArrayList<>();
		try (ResultSet rs = pPs.executeQuery()) {
			generateFieldNames(rs.getMetaData());
			int i = 1;
			while (rs.next() && (de = EntityFactory.newInstance(pType)) != null) {
				de.load(fieldNames, rs);
				de.checkValues();
				de.accept();
				de.setRowNum(i++);
				list.add(de);
			} // @deger while sonu
		} catch (SQLException e) {
			de = null;
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return list;
	}

	private void setParameters(PreparedStatement pPs, ParameterMetaData pMd) throws SQLException {
		Object v = null;
		for (int i = 1; i <= paramList.size(); i++) {
			v = valueList.get(i - 1);
			if (v == null) {
				pPs.setNull(i, pMd.getParameterType(i));
			} else {
				pPs.setObject(i, v);
			}
		}
	}

	@Override
	public IResult<AXlsAktar> exportToXls(DbCommand pQuery, Type pOutType, AXlsAktar pXls) {
		IResult<AXlsAktar> res = new Result<>();
		List<IDataEntity> list = findAny(pQuery, pOutType);
		if (!BaseConstants.isEmpty(list)) {
			res.setSuccess(true);
			pXls.yaz(list);
			res.setData(pXls);
		}
		return res;
	}

	public ArrayList<IDataEntity> findDbTables(String pLibrary, String pSchema) {
		try {
			connection = getConnection();
			if (connection != null) {
				DatabaseMetaData meta = connection.getMetaData();
				try (ResultSet rs = meta.getTables(pLibrary, pSchema, "%", new String[] { "TABLE" })) {
					return fetchAllData(rs, DataEntity.class);
				}
			}
		} catch (SQLException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		} finally {
			close(true);
		}
		return null;
	}

	public IResult<IExport> exportDb(DbConnInfo pTarget, IExport pTransfer, OnExportListener proceedListener) {
		Connection targetConn = null;
		Integer count = 0;
		IResult<IExport> res = new Result<>(false, BaseConstants.MESSAGE_DATA_TRANSFER_ERROR);
		try {
			connection = getConnection();
			connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			targetConn = getConnection(pTarget);

			if (connection != null && targetConn != null) {

				String sourceQuery = pTransfer.getQuery();
				String souceTable = pTransfer.getSourceSchema() + schemaSeperator + pTransfer.getSourceTable();
				String targetTable = pTransfer.getTargetSchema() + pTarget.getDbSeperator()
						+ pTransfer.getTargetTable();
				if (StringTool.isNull(sourceQuery) || "*".equals(sourceQuery))
					sourceQuery = SELECT_FROM + souceTable;

				if (!StringTool.isNull(sourceQuery)) {
					DbCommand countQuery = new DbCommand(SELECT_COUNT_FROM  +  souceTable);
					IDataEntity vs = (IDataEntity) findOne(countQuery);
					if (vs != null && !vs.isNull("count")) {
						Object value = vs.get("count");
						if (value instanceof Integer)
							count = (Integer) value;
						else
							count = Integer.parseInt(value.toString());

					}
					if (count != null && count > 0) {
						if (pTransfer.isDeleteTargetTableRows()) {
							String deleteQuery = DELETE + FROM + targetTable;
							try (PreparedStatement targetPs = targetConn.prepareStatement(deleteQuery)) {
								targetPs.execute();
							}
						}
						res = export(pTransfer, targetConn, sourceQuery, targetTable, count, proceedListener);
					} else {
						res.setMessage(String.format(FORMATED_EXPORT_MESSAGE, pTransfer.getExportId(),
								BaseConstants.MESSAGE_DATA_TRANSFER_ERROR_NODATA));
						res.setSuccess(false);

					}
				}
			}
		} catch (SQLException e) {
			String msg = String.format(FORMATED_EXPORT_MESSAGE, pTransfer.getExportId(),
					BaseConstants.MESSAGE_DATA_TRANSFER_ERROR);
			res.setMessage(msg);
			res.setSuccess(false);
			res.setData(pTransfer);
			proceedListener.onProceed(PHASE.FAILS, 0.0, count, msg);
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		} finally {
			close(true);
			close(targetConn, true);
		}
		return res;
	}

	private static final String FORMATED_EXPORT_MESSAGE = "%s: %s";
	private static final String FORMATED_EXPORT_MESSAGE2 = "%s: %s%s";

	private IResult<IExport> export(IExport pTransfer, Connection targetConn, String sourceQuery, String targetTable,
			Integer count, OnExportListener proceedListener) throws SQLException {
		final String msgExportStarts = BaseConstants.getString("DbHandler.Transfer.Starts");
		final String msgExportSaves = BaseConstants.getString("DbHandler.Transfer.Saves");
		final String msgExportSaved = BaseConstants.getString("DbHandler.Transfer.Saved");
		final String msgExportEnds = BaseConstants.getString("DbHandler.Transfer.Ends");
		final String msgExportProceeds = "...";

		IResult<IExport> res = new Result<>(false, BaseConstants.MESSAGE_DATA_TRANSFER_ERROR);

		Double batchSize = 1000.0;
		Double progres = 0.0;
		pTransfer.setSourceCount(count);

		float div = 100f;
		if (count < 100)
			div = (float) count;
		else if (count > 9999)
			div = 1000f;

		long proceed = (long) Math.ceil(count / div);
		float ratio = 1f / proceed;

		try (PreparedStatement ps = connection.prepareStatement(sourceQuery); ResultSet rs = ps.executeQuery()) {
			ResultSetMetaData md = rs.getMetaData();
			int colomnCount = md.getColumnCount();
			generateFieldNames(md);
			String targetQuery = generateInsertCommand(fieldNames, targetTable);
			try (PreparedStatement targetPs = targetConn.prepareStatement(targetQuery)) {
				int i = 0;
				proceedListener.onProceed(PHASE.STARTS, 0.0, count,
						String.format(FORMATED_EXPORT_MESSAGE, pTransfer.getExportId(), msgExportStarts));
				while (rs.next()) {
					i++;
					for (int dJ = 1; dJ <= colomnCount; dJ++) {
						Object value = rs.getObject(dJ);
						if (value != null)
							targetPs.setObject(dJ, value);
						else
							targetPs.setNull(dJ, md.getColumnType(dJ));
					}
					targetPs.addBatch();

					if (i % batchSize == batchSize - 1) {
						proceedListener.onProceed(PHASE.SAVE_BEFORE, progres, i,
								String.format(FORMATED_EXPORT_MESSAGE, pTransfer.getExportId(), msgExportSaves));

						targetPs.executeBatch();
						proceedListener.onProceed(PHASE.SAVE_AFTER, progres, i,
								String.format(FORMATED_EXPORT_MESSAGE2, pTransfer.getExportId(), i, msgExportSaved));

					}
					if (i % div == div - 1) {
						progres += ratio;
						proceedListener.onProceed(PHASE.PROCEED, progres, i,
								String.format(FORMATED_EXPORT_MESSAGE, pTransfer.getExportId(), msgExportProceeds));

					}
				}
				if (i % batchSize != 0) {
					proceedListener.onProceed(PHASE.SAVE_BEFORE, progres, i,
							String.format(FORMATED_EXPORT_MESSAGE, pTransfer.getExportId(), msgExportSaves));

					targetPs.executeBatch();
					proceedListener.onProceed(PHASE.SAVE_AFTER, progres, i,
							String.format(FORMATED_EXPORT_MESSAGE2, pTransfer.getExportId(), i, msgExportSaved));

				}
				String msg = String.format(FORMATED_EXPORT_MESSAGE2, pTransfer.getExportId(), count, msgExportEnds);
				proceedListener.onProceed(PHASE.ENDS, (double) count, count, msg);

				res.setSuccess(true);
				res.setData(pTransfer);
				res.setMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public Class<T> getTypeParameterClass() {
		Type type = callerClass.getGenericSuperclass();
		ParameterizedType paramType = (ParameterizedType) type;
		return (Class<T>) paramType.getActualTypeArguments()[0];
	}

	@Override
	public IResult<String> sendMail(FnParam... pParams) {
		return null;
	}

}
