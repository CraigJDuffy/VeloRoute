package com.veloroute.duffylamb.veloroute;


import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mapzen.android.graphics.MapzenMap;
import com.mapzen.android.graphics.model.Marker;
import com.mapzen.android.routing.MapzenRouter;
import com.mapzen.pelias.gson.Feature;
import com.mapzen.valhalla.RouteCallback;

import java.util.ArrayList;


public class RoutePlanner {

	private LinearLayout container;
	private Button BtnDirectionTo;
	private Button BtnDirectionFrom;

	private RouteOptions routeOptions;
	private MapzenRouter router;
	private MapzenMap map;

	private Context context;
	private int smartVisibility;
	private ArrayList<Feature> routeLocations;
	private Feature currentLocation;

	public RoutePlanner(LinearLayout container) {

		this.container = container;


		this.context = container.getContext();
		this.smartVisibility = 0;
		this.routeLocations = new ArrayList<>();

		this.routeOptions = new RouteOptions(this.context);

		setupRouter();
		setupButtons();
	}

	public void setMap(MapzenMap map) {

		this.map = map;
	}

	private void setupRouter() {

		router = new MapzenRouter(context);
		router.setBiking();
		router.getRouter().setHttpHandler(routeOptions);
	}

	private void setupButtons() {

		this.BtnDirectionFrom = (Button) container.findViewById(R.id.btn_direction_from);
		this.BtnDirectionTo = (Button) container.findViewById(R.id.btn_direction_to);

		BtnDirectionFrom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				addStartPoint(currentLocation);
				map.addMarker(new Marker(currentLocation.geometry.coordinates.get(0), currentLocation.geometry.coordinates.get(1)));
				map.clearSearchResults();
			}
		});

		BtnDirectionTo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				addEndPoint(currentLocation);
				map.addMarker(new Marker(currentLocation.geometry.coordinates.get(0), currentLocation.geometry.coordinates.get(1)));
				map.clearSearchResults();
				fetchRoute();

			}
		});
	}

	/**
	 * Method to set the visibility of the UI elements of the route planner
	 *
	 * @param visibility One of <code>View.VISIBLE, View.INVISIBLE, View.GONE</code>
	 *
	 * @see android.view.View#setVisibility(int)
	 */
	public void setVisibility(int visibility) {

		this.container.setVisibility(visibility);
	}

	/**
	 * Convenience method for temporarily hiding the route planner UI. When used in combination with <code>smartShow</code> allows the UI visibility to be toggled
	 * without checking if the UI was visible in the first place. <strong>Multiple calls to this function will prevent <code>smartShow</code> from dsiplaying the UI</strong>
	 */
	public void smartHide() {

		smartVisibility = 0;
		if (container.getVisibility() == View.VISIBLE) {
			setVisibility(View.GONE);
			smartVisibility = 1;
		}
	}

	/**
	 * When used in combination with <code>smartHide</code> this function only shows the UI if it was visible before the call to <code>smartHide</code>. Multiple calls to this function
	 * will perform identically, unless <code>smartHide</code> is called.
	 */
	public void smartShow() {

		if (smartVisibility == 1) {
			setVisibility(View.VISIBLE);
		}
	}

	public void setCurrentLocation(Feature feature) {

		currentLocation = feature;
	}

	public void setRouteCallback(RouteCallback callback) {

		router.setCallback(callback);
	}

	public void addStartPoint(Feature location) {

		routeLocations.add(0, location);
	}

	public void addEndPoint(Feature location) {

		routeLocations.add(location);
	}

	/**
	 * @param index    Index of the location in the desired route. Starts at at 0, representing the route start point
	 * @param location
	 */
	public void addMidPoint(int index, Feature location) {

		routeLocations.add(index, location);
	}

	private void fetchRoute() {

		for (Feature point : routeLocations) {
			router.setLocation(new double[]{point.geometry.coordinates.get(1), point.geometry.coordinates.get(0)});
		}

		router.fetch();
	}

}
