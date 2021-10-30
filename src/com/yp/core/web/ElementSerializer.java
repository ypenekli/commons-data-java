package com.yp.core.web;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.yp.core.entity.Element;
import com.yp.core.entity.IElement;

public class ElementSerializer implements JsonDeserializer<IElement>, JsonSerializer<IElement> {

	@Override
	public IElement deserialize(JsonElement json, Type pArg1, JsonDeserializationContext pArg2)
			throws JsonParseException {
		return pArg2.deserialize(json, Element.class);
	}

	@Override
	public JsonElement serialize(IElement pArg0, Type pArg1, JsonSerializationContext pArg2) {
		return pArg2.serialize(pArg0, Element.class);
	}

}
