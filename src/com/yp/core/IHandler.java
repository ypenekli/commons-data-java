package com.yp.core;

import java.lang.reflect.Type;
import java.util.List;

import com.yp.core.db.DbCommand;
import com.yp.core.db.DbConnInfo;
import com.yp.core.db.IExport;
import com.yp.core.db.OnExportListener;
import com.yp.core.db.Pager;
import com.yp.core.entity.IDataEntity;
import com.yp.core.entity.IResult;
import com.yp.core.excel.AXlsAktar;

public interface IHandler<T> {	

	T find(T pDataEntity);

	IResult<List<T>> findAny(DbCommand pQuery, Pager pPager);

	IResult<List<IDataEntity>> findAny(DbCommand pQuery, Type pOutType, Pager pPager);

	List<T> findAny(DbCommand pQuery);

	List<IDataEntity> findAny(DbCommand pQuery, Type pOutType);

	T findOne(DbCommand pQuery);

	IDataEntity findOne(DbCommand pQuery, Type pOutType);

	IResult<IDataEntity> save(IDataEntity pData);

	IResult<T> save(T pData);

	IResult<List<T>> saveAll(List<T> pData);

	IResult<String> execute(DbCommand pQuery);

	IResult<String> executeAll(DbCommand... pQueries);

	IResult<String> saveAtomic(Object... pParams);

	IResult<String> sendMail(FnParam... pParams);

	List<IDataEntity> findDbTables(String pLibrary, String pSchema);
	
	IResult<AXlsAktar> exportToXls(DbCommand pQuery, Type pOutType, AXlsAktar pXls);

	IResult<IExport> exportDb(DbConnInfo pTarget, IExport pTransfer, OnExportListener proceedListener);

}
