package com.example.stefan.android_client.pkgModel;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class GsonUTCdateAdapter implements JsonSerializer<Date>,JsonDeserializer<Date> {

	private final DateFormat dateFormat;
	
	public GsonUTCdateAdapter() {
	  dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);      //This is the format I need
	  dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));                               //This is the key line which converts the date to UTC which cannot be accessed with the default serializer
	}
	
	@Override
	public JsonElement serialize(Date date,Type type,JsonSerializationContext jsonSerializationContext) {
	    return new JsonPrimitive(dateFormat.format(date));
	}
	
	@Override
	public Date deserialize(JsonElement jsonElement,Type type,JsonDeserializationContext jsonDeserializationContext) {
	  try {
	    return dateFormat.parse(jsonElement.getAsString());
	  } catch (Exception e) {
	    throw new JsonParseException(e);
	  }
	}
}
