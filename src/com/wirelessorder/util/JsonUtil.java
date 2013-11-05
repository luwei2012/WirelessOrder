package com.wirelessorder.util;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONException;
import org.json.JSONObject;

import com.wirelessorder.entity.Result;

public class JsonUtil {

	@SuppressWarnings("deprecation")
	public static <T> Object json2object(String json, TypeReference<T> t) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper
					.getDeserializationConfig()
					.set(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
							false);
			return objectMapper.readValue(json,
					TypeFactory.fromTypeReference(t));
		} catch (Exception e) {
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static Object json2object(String json, Class<?> cls) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializationConfig()
				.set(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
						false);
		try {
			return mapper.readValue(json, cls);
		} catch (JsonParseException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}
		return null;
	}

	/**
	 * java对象转为json对象
	 */
	public static String object2json(Object obj) {
		String result = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			result = mapper.writeValueAsString(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;

	}

	public static Object getObject(String content, Class<?> valueType) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(content, valueType);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Result<String> getJsonResult(String json) {
		boolean success = false;
		String msg = "unknown error";
		try {
			JSONObject obj = new JSONObject(json);
			success = obj.getBoolean("success");
			msg = obj.getString("message");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new Result<String>(success, msg);
	}
}
