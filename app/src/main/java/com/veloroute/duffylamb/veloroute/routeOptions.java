package com.veloroute.duffylamb.veloroute;


import android.util.Log;

import com.mapzen.android.routing.TurnByTurnHttpHandler;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Tommy on 28/06/2017.
 */

public class routeOptions extends TurnByTurnHttpHandler {

	//private Gson gson = new GsonBuilder().registerTypeAdapter(bicycleCostings.class, bicycleSerializer()).create();
	//public bicycleCostings bikeCost = new bicycleCostings();

	@Override
	protected Response onRequest(Interceptor.Chain chain) throws IOException {

		String jsonQuery = chain.request().url().queryParameter("json").replaceFirst(".$", ","); //Get the current JSON obj from the request and prepare for appending
		Log.d("onRequest", jsonQuery);

		//	Log.d("Stuff", gson.toJson(bikeCost));
		return super.onRequest(chain);
	}
}
