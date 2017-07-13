package com.veloroute.duffylamb.veloroute;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.ContextCompat;

import com.mapzen.android.lost.api.LocationServices;
import com.mapzen.android.lost.api.LostApiClient;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
/**
 * Created by Tommy on 13/07/2017.
 */

public class LocationProvider {

	private static final LocationProvider instance = new LocationProvider();
	private LostApiClient client;

	private LocationProvider() {

		client = new LostApiClient.Builder(mainScreen.APPCONTEXT).build();
		client.connect();
	}

	public static LocationProvider getInstance() {

		return instance;
	}

	@RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
	public Location getLastLocation() throws SecurityException {

		return LocationServices.FusedLocationApi.getLastLocation(client);
	}


	public boolean hasPermission() {

		return (ContextCompat.checkSelfPermission(mainScreen.APPCONTEXT, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(mainScreen.APPCONTEXT, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
	}

}
