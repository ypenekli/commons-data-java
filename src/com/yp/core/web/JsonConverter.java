package com.yp.core.web;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yp.core.entity.DataEntity;
import com.yp.core.entity.IDataEntity;
import com.yp.core.entity.IResult;
import com.yp.core.entity.Result;

public class JsonConverter {

	public static IDataEntity fromJson(InputStream in) {
		// JsonReader reader = Json.createReader(new StringReader(pJson));
		// JsonStructure jsonst = reader.read();

		GsonBuilder gb = new GsonBuilder();
		gb.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);

		// gb.registerTypeAdapter(IElement.class, new ElementSerializer());
		// gb.registerTypeAdapter(IElement.class, new Element());
		// gb.registerTypeAdapter(BaseEntity.class, new
		// BaseEntityDeserializer());

		// Gson gson = gb.serializeNulls().create();
		Gson gson = gb.create();

		IDataEntity bs = gson.fromJson(new InputStreamReader(in), DataEntity.class);
		bs.checkValues();

		// if (bs != null) {
		// IDataEntity vs = EntityFactory.newInstance(pClass);
		// vs.setState(bs.getState().byteValue());
		// bs.getFields().forEach((k, v) -> {
		// vs.setField(k, v.getValue(), v.isChanged());
		// });
		//
		// vs.checkValues();
		// return vs;
		// }

		return bs;
	}

	@SuppressWarnings("rawtypes")
	public static IResult fromJson(InputStream in, Type... pTypes) {
		GsonBuilder gb = new GsonBuilder();
		gb.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);

		// gb.registerTypeAdapter(IElement.class, new ElementSerializer());
		// gb.registerTypeAdapter(IDataEntity.class, new DataEntity());

		Gson gson = gb.create();

		// Type entityType = TypeToken.get(pClass).getType();// new
		// TypeToken<Result< DataEntity>>() {}.getType(pT);
		Type entityType;
		// if (isArray)
		// entityType = new TypeToken<Result<ArrayList<DataEntity>>>() {
		// }.getType();
		// else
		entityType = TypeToken.getParameterized(Result.class, pTypes).getType();

		// Type entityType = TypeToken.getParameterized(Result.class,
		// pTypes).getType();
		return gson.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), entityType);

		// Type type = ParameterizedTypeImpl.make(IResult.class, new
		// Type[]{pClass}, null);

		// return gson.fromJson(new InputStreamReader(in,
		// StandardCharsets.UTF_8), pType);

	}

	public static String toJson(IDataEntity... pEntitiy) {
		GsonBuilder gb = new GsonBuilder();
		gb.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);

		Gson gson = gb.create();
		return gson.toJson(pEntitiy);
	}

	public static String toJson(Object... pEntitiy) {
		GsonBuilder gb = new GsonBuilder();
		gb.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);

		Gson gson = gb.create();
		return gson.toJson(pEntitiy);
	}
}
