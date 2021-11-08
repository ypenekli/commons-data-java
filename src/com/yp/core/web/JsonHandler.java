package com.yp.core.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import com.yp.core.BaseConstants;
import com.yp.core.FnParam;
import com.yp.core.IHandler;
import com.yp.core.db.DbCommand;
import com.yp.core.db.DbConnInfo;
import com.yp.core.db.IExport;
import com.yp.core.db.OnExportListener;
import com.yp.core.db.Pager;
import com.yp.core.entity.DataEntity;
import com.yp.core.entity.IDataEntity;
import com.yp.core.entity.IElement;
import com.yp.core.entity.IResult;
import com.yp.core.entity.Result;
import com.yp.core.excel.AXlsAktar;
import com.yp.core.log.MyLogger;

public class JsonHandler<T> implements IHandler<T> {
	private String url;
	private String callerClassName;
	private Class<T> callerClass;
	protected static final String CLASS_NAME = "type";
	protected static final String PAGER = "pager";
	private static final String AUTHORIZATION = "Authorization";
	private static final String FIND_ONE = "findOne@";
	private static final String FIND_BY = "findBy@";
	private static final String FIND_PAGE_BY = "findPageBy@";

	private static String token = "";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JsonHandler(String pUrl, Class pCallerClass) {
		super();
		url = pUrl;
		callerClass = pCallerClass;
		callerClassName = callerClass.getSimpleName();
	}

	private static final String CONN_URL_FORMAT = "%s/%s/%s/";
	private static final String JSON_FORMAT = "'%s'";

	public enum HTTP_METHOD {

		GET("GET"), POST("POST"), PUT("PUT");

		private String method = null;

		private HTTP_METHOD(String pMethod) {
			method = pMethod;
		}

		public String getMehod() {
			return method;
		}
	}

	private HttpURLConnection getConnection(String pFnName, HTTP_METHOD pHttpMethod) throws IOException {
		URL newUrl = new URL(String.format(CONN_URL_FORMAT, url, callerClassName, pFnName));
		HttpURLConnection connection;
		connection = (HttpURLConnection) newUrl.openConnection();
		connection.setConnectTimeout(5000);
		connection.setRequestProperty("Content-Type",
				"application/json; application/x-www-form-urlencoded; charset=UTF-8");
		connection.setRequestProperty("enctype", "multipart/form-data");
		connection.setRequestProperty("Accept-Encoding", "gzip");
		connection.setRequestProperty(AUTHORIZATION, token);
		connection.setRequestMethod(pHttpMethod.getMehod());
		connection.setDoOutput(true);
		connection.setDoInput(true);
		return connection;
	}

	private String getJsonString(InputStream is, String contentEncoding) throws IOException {
		String json;
		if ("gzip".equals(contentEncoding)) {
			json = org.apache.commons.io.IOUtils.toString(new GZIPInputStream(is), StandardCharsets.UTF_8);
		} else
			json = org.apache.commons.io.IOUtils.toString(is, StandardCharsets.UTF_8);
		return json;
	}

	// downloadAnyFromServer
	public IResult<List<T>> getAny(String pFnName, Pager pPager, FnParam[] pParams) throws IOException {
		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(IElement.class, new ElementSerializer());
		gb.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);

		Gson gson = gb.create();

		FnParam[] params;
		if (pParams != null && pParams.length > 0) {
			params = new FnParam[pParams.length + 2];
			for (int i = 0; i < pParams.length; i++) {
				params[i + 2] = pParams[i];
			}
		} else
			params = new FnParam[2];

		Class<T> entityType = getTypeParameterClass();
		params[0] = new FnParam(CLASS_NAME, getClassName(entityType));
		params[1] = new FnParam(PAGER, pPager);

		String in = String.format(JSON_FORMAT, gson.toJson(params));

		HttpURLConnection conn = getConnection(pFnName, HTTP_METHOD.GET);

		OutputStream os = conn.getOutputStream();
		os.write(in.getBytes(StandardCharsets.UTF_8));
		os.flush();
		os.close();

		token = conn.getHeaderField(AUTHORIZATION);
		int responseCode = conn.getResponseCode();
		IResult<List<T>> result = null;
		if (responseCode == HttpURLConnection.HTTP_OK) {
			InputStream is = conn.getInputStream();
			String res = getJsonString(is, conn.getContentEncoding());
			Type resultType = TypeToken
					.getParameterized(Result.class, TypeToken.getParameterized(ArrayList.class, entityType).getType())
					.getType();

			result = gson.fromJson(res, resultType);
			is.close();
			List<T> list = null;
			if (result != null && (list = result.getData()) != null) {
				int i = 1;
				for (T e : list) {
					IDataEntity vs = (IDataEntity) e;
					vs.checkValues();
					vs.setRowNum(i);
					i += 1;
				}
			}
		}
		conn.disconnect();
		return result;
	}

	// downloadAnyFromServer
	public IResult<List<IDataEntity>> getAny(String pFnName, Type pOutType, Pager pPager, FnParam... pParams)
			throws IOException {

		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(IElement.class, new ElementSerializer());
		gb.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);

		Gson gson = gb.create();

		FnParam[] params;
		if (pParams != null && pParams.length > 0) {
			params = new FnParam[pParams.length + 2];
			for (int i = 0; i < pParams.length; i++) {
				params[i + 2] = pParams[i];
			}
		} else
			params = new FnParam[2];

		params[0] = new FnParam(CLASS_NAME, getClassName(pOutType));
		params[1] = new FnParam(PAGER, pPager);

		String in = String.format(JSON_FORMAT, gson.toJson(params));
		HttpURLConnection conn = getConnection(pFnName, HTTP_METHOD.GET);

		OutputStream os = conn.getOutputStream();
		os.write(in.getBytes(StandardCharsets.UTF_8));
		os.flush();
		os.close();

		token = conn.getHeaderField(AUTHORIZATION);
		int responseCode = conn.getResponseCode();
		IResult<List<IDataEntity>> result = null;
		if (responseCode == HttpURLConnection.HTTP_OK) {
			InputStream is = conn.getInputStream();
			String res = getJsonString(is, conn.getContentEncoding());			
			Type resultType = TypeToken
					.getParameterized(Result.class, TypeToken.getParameterized(ArrayList.class, pOutType).getType())
					.getType();

			result = gson.fromJson(res, resultType);
			is.close();
			List<IDataEntity> list = null;
			if (result != null && (list = result.getData()) != null) {
				int i = 1;
				for (IDataEntity vs : list) {
					vs.checkValues();
					vs.setRowNum(i);
					i += 1;
				}
			}
		}
		conn.disconnect();
		return result;
	}

	// downloadFromServer
	public T getOne(String pFnName, FnParam... pParams) throws IOException {

		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(IElement.class, new ElementSerializer());
		gb.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);

		Gson gson = gb.create();

		FnParam[] params;
		if (pParams != null && pParams.length > 0) {
			params = new FnParam[pParams.length + 2];
			for (int i = 0; i < pParams.length; i++) {
				params[i + 2] = pParams[i];
			}
		} else
			params = new FnParam[2];

		Class<T> entityType = getTypeParameterClass();
		params[0] = new FnParam(CLASS_NAME, getClassName(entityType));
		params[1] = new FnParam(PAGER, new Pager());

		String in = String.format(JSON_FORMAT, gson.toJson(params));
		HttpURLConnection conn = getConnection(pFnName, HTTP_METHOD.GET);
		OutputStream os = conn.getOutputStream();
		os.write(in.getBytes(StandardCharsets.UTF_8));
		os.flush();
		os.close();

		token = conn.getHeaderField(AUTHORIZATION);
		int responseCode = conn.getResponseCode();
		T result = null;
		if (responseCode == HttpURLConnection.HTTP_OK) {
			InputStream is = conn.getInputStream();
			String res = getJsonString(is, conn.getContentEncoding());
			result = gson.fromJson(res, entityType);
			is.close();
			if (result != null)
				((IDataEntity) result).checkValues();
		}
		conn.disconnect();

		return result;
	}

	@SuppressWarnings("unchecked")
	public Class<T> getTypeParameterClass() {
		Type type = callerClass.getGenericSuperclass();
		ParameterizedType paramType = (ParameterizedType) type;
		return (Class<T>) paramType.getActualTypeArguments()[0];
	}

	// downloadFromServer
	public T getOne(String pFnName, T params) throws IOException {

		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(IElement.class, new ElementSerializer());
		gb.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);

		Gson gson = gb.create();
		String in = String.format(JSON_FORMAT, gson.toJson(params));
		HttpURLConnection conn = getConnection(pFnName, HTTP_METHOD.POST);

		OutputStream os = conn.getOutputStream();

		os.write(in.getBytes(StandardCharsets.UTF_8));
		os.close();

		Class<T> entityType = getTypeParameterClass();

		token = conn.getHeaderField(AUTHORIZATION);
		int responseCode = conn.getResponseCode();
		T result = null;
		if (responseCode == HttpURLConnection.HTTP_OK) {
			InputStream is = conn.getInputStream();
			String res = getJsonString(is, conn.getContentEncoding());
			result = gson.fromJson(res, entityType);
			is.close();
			if (result != null)
				((IDataEntity) result).checkValues();
		}
		conn.disconnect();

		return result;
	}

	// downloadFromServer
	public IDataEntity getOne(String pFnName, Type pOutType, FnParam... pParams) throws IOException {

		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(IElement.class, new ElementSerializer());
		gb.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);

		Gson gson = gb.create();

		FnParam[] params;
		if (pParams != null && pParams.length > 0) {
			params = new FnParam[pParams.length + 2];
			for (int i = 0; i < pParams.length; i++) {
				params[i + 2] = pParams[i];
			}
		} else
			params = new FnParam[2];

		params[0] = new FnParam(CLASS_NAME, getClassName(pOutType));
		params[1] = new FnParam(PAGER, new Pager());

		String in = String.format(JSON_FORMAT, gson.toJson(params));

		HttpURLConnection conn = getConnection(pFnName, HTTP_METHOD.GET);
		OutputStream os = conn.getOutputStream();
		os.write(in.getBytes(StandardCharsets.UTF_8));
		os.flush();
		os.close();
		token = conn.getHeaderField(AUTHORIZATION);
		int responseCode = conn.getResponseCode();
		IDataEntity result = null;
		if (responseCode == HttpURLConnection.HTTP_OK) {
			InputStream is = conn.getInputStream();
			String res = getJsonString(is, conn.getContentEncoding());
			result = gson.fromJson(res, pOutType);
			is.close();
			if (result != null)
				result.checkValues();
		}
		conn.disconnect();

		return result;
	}

	// executeAtServer
	public IResult<List<T>> postList(String pFnName, List<T> params) throws IOException {

		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(IElement.class, new ElementSerializer());
		gb.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);

		Gson gson = gb.create();
		String in = String.format(JSON_FORMAT, gson.toJson(params));

		HttpURLConnection conn = getConnection(pFnName, HTTP_METHOD.POST);
		OutputStream os = conn.getOutputStream();

		os.write(in.getBytes(StandardCharsets.UTF_8));
		os.close();

		Type type = DataEntity.class;
		if (!params.isEmpty()) {
			type = params.get(0).getClass();
		}

		Type entityType = TypeToken
				.getParameterized(Result.class, TypeToken.getParameterized(ArrayList.class, type).getType()).getType();
		token = conn.getHeaderField(AUTHORIZATION);
		int responseCode = conn.getResponseCode();
		Result<List<T>> result = null;
		if (responseCode == HttpURLConnection.HTTP_OK) {
			InputStream is = conn.getInputStream();
			String out = getJsonString(is, conn.getContentEncoding());
			result = gson.fromJson(out, entityType);
			is.close();
			if (result != null && result.getData() != null)
				for (T e : result.getData()) {
					((IDataEntity) e).checkValues();
				}
		} else {
			result = new Result<>(false, BaseConstants.MESSAGE_LOGIN_ERROR);
		}
		conn.disconnect();
		return result;
	}

	public IResult<String> executeAtServer(String pFnName, FnParam[] params) throws IOException {

		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(IElement.class, new ElementSerializer());
		gb.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);

		Gson gson = gb.create();
		String in = String.format(JSON_FORMAT, gson.toJson(params));

		HttpURLConnection conn = getConnection(pFnName, HTTP_METHOD.POST);
		OutputStream os = conn.getOutputStream();

		os.write(in.getBytes(StandardCharsets.UTF_8));
		os.close();

		Type entityType = new TypeToken<Result<String>>() {
		}.getType();
		token = conn.getHeaderField(AUTHORIZATION);
		int responseCode = conn.getResponseCode();
		IResult<String> result = null;
		if (responseCode == HttpURLConnection.HTTP_OK) {
			InputStream is = conn.getInputStream();
			String out = getJsonString(is, conn.getContentEncoding());
			result = gson.fromJson(out, entityType);
			is.close();
		} else {
			result = new Result<String>(false, BaseConstants.MESSAGE_LOGIN_ERROR);
		}
		conn.disconnect();
		return result;
	}

	public IResult<T> executeAtServer(String pFnName, Class<T> pOut, FnParam... pParams) throws IOException {
		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(IElement.class, new ElementSerializer());
		gb.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);
		Gson gson = gb.create();

		FnParam[] params;
		if (pParams != null && pParams.length > 0) {
			params = new FnParam[pParams.length + 1];
			for (int i = 0; i < pParams.length; i++) {
				params[i + 1] = pParams[i];
			}
		} else
			params = new FnParam[1];

		params[0] = new FnParam(CLASS_NAME, getClassName(pOut));

		String in = String.format(JSON_FORMAT, gson.toJson(params));
		HttpURLConnection conn = getConnection(pFnName, HTTP_METHOD.POST);
		OutputStream os = conn.getOutputStream();
		os.write(in.getBytes(StandardCharsets.UTF_8));
		os.close();
		Type entityType = TypeToken.getParameterized(Result.class, pOut).getType();

		token = conn.getHeaderField(AUTHORIZATION);
		int responseCode = conn.getResponseCode();
		IResult<T> result = null;
		if (responseCode == HttpURLConnection.HTTP_OK) {
			InputStream is = conn.getInputStream();
			String out = getJsonString(is, conn.getContentEncoding());
			result = gson.fromJson(out, entityType);
			is.close();
			if (result != null && result.getData() != null)
				((IDataEntity) result.getData()).checkValues();
		} else {
			result = new Result<>(false, BaseConstants.MESSAGE_LOGIN_ERROR);
		}
		conn.disconnect();
		return result;
	}

	// executeAtServer
	public IResult<T> postOne(String pFnName, T params) throws IOException {
		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(IElement.class, new ElementSerializer());
		gb.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);

		Gson gson = gb.create();
		String in = String.format(JSON_FORMAT, gson.toJson(params));

		HttpURLConnection conn = getConnection(pFnName, HTTP_METHOD.POST);
		OutputStream os = conn.getOutputStream();
		os.write(in.getBytes(StandardCharsets.UTF_8));
		os.close();

		Type entityType = TypeToken.getParameterized(Result.class, params.getClass()).getType();
		token = conn.getHeaderField(AUTHORIZATION);
		int responseCode = conn.getResponseCode();
		IResult<T> result = null;
		if (responseCode == HttpURLConnection.HTTP_OK) {
			InputStream is = conn.getInputStream();
			String out = getJsonString(is, conn.getContentEncoding());
			result = gson.fromJson(out, entityType);
			is.close();
			if (result != null && result.getData() != null)
				((IDataEntity) result.getData()).checkValues();
		} else {
			result = new Result<>(false, BaseConstants.MESSAGE_LOGIN_ERROR);
		}
		conn.disconnect();
		return result;
	}

	// executeAtServer
	public IResult<IDataEntity> postOne(String pFnName, IDataEntity params) throws IOException {
		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(IElement.class, new ElementSerializer());
		gb.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);

		Gson gson = gb.create();
		String in = String.format(JSON_FORMAT, gson.toJson(params));

		HttpURLConnection conn = getConnection(pFnName, HTTP_METHOD.POST);
		OutputStream os = conn.getOutputStream();

		os.write(in.getBytes(StandardCharsets.UTF_8));
		os.close();
		Type entityType = TypeToken.getParameterized(Result.class, params.getClass()).getType();
		token = conn.getHeaderField(AUTHORIZATION);
		int responseCode = conn.getResponseCode();
		IResult<IDataEntity> result = null;
		if (responseCode == HttpURLConnection.HTTP_OK) {
			InputStream is = conn.getInputStream();
			String out = getJsonString(is, conn.getContentEncoding());
			result = gson.fromJson(out, entityType);
			is.close();
			if (result != null && result.getData() != null)
				result.getData().checkValues();
		} else {
			result = new Result<>(false, BaseConstants.MESSAGE_LOGIN_ERROR);
		}
		conn.disconnect();
		return result;
	}

	public List<IDataEntity> uploadToServer(String pFnName, IDataEntity params, Type pOut) throws IOException {
		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(IElement.class, new ElementSerializer());
		gb.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);

		Gson gson = gb.create();
		String in = String.format(JSON_FORMAT, gson.toJson(params));

		HttpURLConnection conn = getConnection(pFnName, HTTP_METHOD.POST);
		OutputStream os = conn.getOutputStream();

		os.write(in.getBytes(StandardCharsets.UTF_8));
		os.close();

		InputStream is = conn.getInputStream();

		Type entityType = TypeToken.getParameterized(ArrayList.class, pOut).getType();

		ArrayList<IDataEntity> result = gson.fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), entityType);

		is.close();
		conn.disconnect();

		return result;
	}

	public String uploadToServer(String pFnName, String pData) throws IOException {
		HttpURLConnection conn = getConnection(pFnName, HTTP_METHOD.POST);
		OutputStream os = conn.getOutputStream();

		os.write(pData.getBytes(StandardCharsets.UTF_8));
		os.close();
		InputStream in = conn.getInputStream();

		String result = org.apache.commons.io.IOUtils.toString(in, StandardCharsets.UTF_8);
		in.close();
		conn.disconnect();

		return result;
	}

	protected String getClassName(Type pOutType) {
		String className = pOutType.toString();
		return className.substring(className.lastIndexOf('.') + 1);
	}

	@Override
	public T find(T pDataEntity) {
		T result = null;
		try {
			result = getOne("find", pDataEntity);
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}

		return result;
	}

	@Override
	public IDataEntity findOne(DbCommand pQuery, Type pOutType) {
		IDataEntity result = null;
		String dFnName = FIND_ONE + pQuery.getName();
		try {
			result = getOne(dFnName, pOutType, pQuery.getParams());
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}

		return result;
	}

	@Override
	public T findOne(DbCommand pQuery) {
		String dFnName = FIND_ONE + pQuery.getName();
		try {
			return getOne(dFnName, pQuery.getParams());
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<IDataEntity> findAny(DbCommand pQuery, Type pOutType) {
		String dFnName = FIND_BY + pQuery.getName();
		IResult<List<IDataEntity>> result = new Result<>();
		try {
			result = getAny(dFnName, pOutType, new Pager(), pQuery.getParams());
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return result.getData();
	}

	@Override
	public List<T> findAny(DbCommand pQuery) {
		String dFnName = FIND_BY + pQuery.getName();
		IResult<List<T>> result = new Result<>();
		try {
			result = getAny(dFnName, new Pager(), pQuery.getParams());
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return result.getData();
	}

	@Override
	public IResult<List<IDataEntity>> findAny(DbCommand pQuery, Type pOutType, Pager pPager) {
		String dFnName = FIND_PAGE_BY + pQuery.getName();
		IResult<List<IDataEntity>> res = new Result<>();
		try {
			res = getAny(dFnName, pOutType, pPager, pQuery.getParams());
		} catch (IOException e) {
			res.setSuccess(false);
			res.setMessage(BaseConstants.MESSAGE_READ_ERROR);
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return res;
	}

	@Override
	public IResult<List<T>> findAny(DbCommand pQuery, Pager pPager) {
		String dFnName = FIND_PAGE_BY + pQuery.getName();
		IResult<List<T>> res = new Result<>();
		try {
			res = getAny(dFnName, pPager, pQuery.getParams());
		} catch (IOException e) {
			res.setSuccess(false);
			res.setMessage(BaseConstants.MESSAGE_READ_ERROR);
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return res;
	}

	@Override
	public IResult<T> save(T pData) {
		try {
			return postOne("save", pData);
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	@Override
	public IResult<IDataEntity> save(IDataEntity pData) {
		try {
			return postOne("save", pData);
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	@Override
	public IResult<List<T>> saveAll(List<T> pData) {

		try {
			return postList("saveAll", pData);
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	@Override
	public IResult<String> execute(DbCommand pQuery) {
		String dFnName = "execute@" + pQuery.getName();
		try {
			return executeAtServer(dFnName, pQuery.getParams());
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	@Override
	public IResult<String> executeAll(DbCommand... pQueries) {
		ArrayList<FnParam> fnparams = new ArrayList<>();
		if (pQueries != null && pQueries.length > 0)
			for (DbCommand x : pQueries) {
				fnparams.add(new FnParam(x.getName(), x.getParams()));
			}
		try {
			return executeAtServer("executeAll", fnparams.toArray(new FnParam[] {}));
		} catch (IOException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public IResult<String> saveAtomic(Object... pParams) {
		if (pParams != null && pParams.length > 0) {
			ArrayList<FnParam> fnparams = new ArrayList<>();
			for (Object o : pParams) {
				if (o != null) {
					if (ArrayList.class.isAssignableFrom(o.getClass())) {
						ArrayList<IDataEntity> list = (ArrayList) o;
						if (!list.isEmpty())
							fnparams.add(new FnParam("list", list));
					} else if (IDataEntity[].class.isAssignableFrom(o.getClass())) {
						IDataEntity[] list = (IDataEntity[]) o;
						if (list.length > 0)
							for (IDataEntity de : list)
								fnparams.add(new FnParam("data", de));
					} else if (IDataEntity.class.isAssignableFrom(o.getClass())) {
						IDataEntity de = (IDataEntity) o;
						fnparams.add(new FnParam("data", de));
					} else if (DbCommand[].class.isAssignableFrom(o.getClass())) {
						DbCommand[] list = (DbCommand[]) o;
						if (list.length > 0)
							for (DbCommand cmd : list)
								fnparams.add(new FnParam(cmd.getName(), cmd.getParams()));
					} else if (DbCommand.class.isAssignableFrom(o.getClass())) {
						DbCommand cmd = (DbCommand) o;
						fnparams.add(new FnParam(cmd.getName(), cmd.getParams()));
					}
				}

			}

			try {
				return executeAtServer("saveAtomic", fnparams.toArray(new FnParam[] {}));
			} catch (IOException e) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
			}
		}

		return null;
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

	@Override
	public IResult<String> sendMail(FnParam... pParams) {
		IResult<String> res = new Result<>();
		try {
			return executeAtServer("sendMail", pParams);
		} catch (IOException e) {
			res.setSuccess(false);
			res.setMessage(e.getMessage());
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return res;
	}

	public static boolean isJsonValid(final String json) throws IOException {
		return isJsonValid(new StringReader(json));
	}

	private static boolean isJsonValid(final Reader reader) throws IOException {
		return isJsonValid(new JsonReader(reader));
	}

	private static boolean isJsonValid(final JsonReader jsonReader) throws IOException {
		try {
			JsonToken token;
			loop: while ((token = jsonReader.peek()) != JsonToken.END_DOCUMENT && token != null) {
				switch (token) {
				case BEGIN_ARRAY:
					jsonReader.beginArray();
					break;
				case END_ARRAY:
					jsonReader.endArray();
					break;
				case BEGIN_OBJECT:
					jsonReader.beginObject();
					break;
				case END_OBJECT:
					jsonReader.endObject();
					break;
				case NAME:
					jsonReader.nextName();
					break;
				case STRING:
				case NUMBER:
				case BOOLEAN:
				case NULL:
					jsonReader.skipValue();
					break;
				case END_DOCUMENT:
					break loop;
				default:
					throw new AssertionError(token);
				}
			}
			return true;
		} catch (final MalformedJsonException ignored) {
			return false;
		}
	}

	public List<IDataEntity> findDbTables(String pLibrary, String pSchema) {
		return null;
	}

	@Override
	public IResult<IExport> exportDb(DbConnInfo pTarget, IExport pTransfer, OnExportListener proceedListener) {
		return null;
	}
}
