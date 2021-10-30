package com.yp.core.entity;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.yp.core.log.MyLogger;

public class EntityFactory {

	public static IDataEntity newInstance() {
		return new DataEntity() {
			private static final long serialVersionUID = -2248629083827024674L;

			@Override
			public String getTableName() {
				return "BASE_TABLE";
			}

			@Override
			public String getSchemaName() {
				return "BASE_SCHEMA";
			}
		};
	}

	public static IDataEntity newInstance(Type pType) {
		try {
			return (IDataEntity) Class.forName(pType.getTypeName()).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	public static IDataEntity newInstance(Class<? extends IDataEntity> pClass) {
		if (pClass == null || pClass.equals(DataEntity.class))
			return newInstance();
		else if (DataEntity.class.isAssignableFrom(pClass))
			try {
				return pClass.newInstance();
			} catch (InstantiationException | IllegalAccessException h) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, h.getMessage(), h);
			}
		return null;
	}

	public static IDataEntity newInstance(String[] pFieldNames, Object[] pValues) {
		IDataEntity entity = newInstance();
		entity.load(pFieldNames, pValues);
		return entity;
	}
}
