package com.veloroute.duffylamb.veloroute;


import android.content.Context;

import com.mapzen.android.routing.TurnByTurnHttpHandler;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Tommy on 28/06/2017.
 */

public class RouteOptions extends TurnByTurnHttpHandler {

	private BicycleCostings bikeCost;
	private Context context;

	public RouteOptions(Context context) {

		this.context = context;
		setBikeCost(new BicycleCostings());

	}


	public void setBikeCost(BicycleCostings bikeCost) {

		this.bikeCost = bikeCost;
	}

	public BicycleCostings getBikeCost() {

		return this.bikeCost;
	}

	@Override
	protected Response onRequest(Interceptor.Chain chain) throws IOException {

		String jsonQuery = chain.request().url().queryParameter("json").replaceFirst(".$", "," + bikeCost.toJSON() + "}"); //Get the current JSON obj from the request and prepare for appending
		final HttpUrl url = chain.request().url().newBuilder().removeAllQueryParameters("json").addQueryParameter("json", jsonQuery).addQueryParameter("api_key", context.getString(R.string.mapzen_api_key)).build();

		return chain.proceed(chain.request().newBuilder().url(url).build());


	}
}
