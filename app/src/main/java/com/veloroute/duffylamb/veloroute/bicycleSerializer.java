package com.veloroute.duffylamb.veloroute;


import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by Tommy on 28/06/2017.
 */

public class bicycleSerializer implements JsonSerializer<bicycleCostings> {

	@Override
	public JsonElement serialize(bicycleCostings src, Type typeOfSrc, JsonSerializationContext context) {

		JsonObject json = (JsonObject) new GsonBuilder().create().toJsonTree(src);
		if (src.getCycling_speed() == 0) json.remove("cycling_speed");
		if (src.getUse_hills() == -1) json.remove("use_hills");
		if (src.getUse_roads() == -1) json.remove("use_roads");

		return json;
	}
}
