package com.veloroute.duffylamb.veloroute;


import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
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
	private ArrayList<RouteLocation> routeLocations;
	Button BtnDirectionTo;
	private Button BtnDirectionFrom;

	private RouteOptions routeOptions;
	private MapzenRouter router;
	private MapzenMap map;

	private Context context;
	private int smartVisibility;
	private Feature currentLocation;
	private boolean hasStart;
	private boolean hasEnd;

	public RoutePlanner(LinearLayout container) {

		this.container = container;


		this.context = container.getContext();
		this.smartVisibility = 0;
		this.routeLocations = new ArrayList<>();
		this.hasEnd = false;
		this.hasStart = false;

		this.routeOptions = new RouteOptions(this.context);

		setupRouter();
		setupButtons();
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

			}
		});


	}

	public void setMap(MapzenMap map) {

		this.map = map;
	}

	public void enable() {

		setVisibility(container, View.VISIBLE);
	}

	/**
	 *
	 * @param view The container view to be modified.
	 * @param visibility
	 *
	 * @see android.view.View#setVisibility(int)
	 */
	private void setVisibility(View view, int visibility) {

		view.setVisibility(visibility);
	}


	//TODO Adjust smartVisibilty functions to function with the new locations UI
	/**
	 * Convenience method for temporarily hiding the route planner UI. When used in combination with <code>smartShow</code> allows the UI visibility to be toggled
	 * without checking if the UI was visible in the first place. <strong>Multiple calls to this function will prevent <code>smartShow</code> from dsiplaying the UI</strong>
	 */
	public void smartHide() {

		smartVisibility = 0;
		if (container.getVisibility() == View.VISIBLE) {
			setVisibility(container, View.GONE);
			smartVisibility = 1;
		}
	}

	/**
	 * When used in combination with <code>smartHide</code> this function only shows the UI if it was visible before the call to <code>smartHide</code>. Multiple calls to this function
	 * will perform identically, unless <code>smartHide</code> is called.
	 */
	public void smartShow() {

		if (smartVisibility == 1) {
			setVisibility(container, View.VISIBLE);
		}
	}

	public void setCurrentLocation(Feature feature) {

		currentLocation = feature;
	}

	public void setRouteCallback(RouteCallback callback) {

		router.setCallback(callback);
	}

	//See https://stackoverflow.com/questions/13114966/android-how-to-get-view-from-context

	public void addStartPoint(Feature location) {

		hasStart = true;
		routeLocations.add(0, new RouteLocation((ConstraintLayout) ((Activity) context).findViewById(R.id.location_container_start), location));
		routeLocations.add(new RouteLocation((ConstraintLayout) ((Activity) context).findViewById(R.id.location_container_end)));
		container.setVisibility(View.GONE);
	}

	public void addEndPoint(Feature location) {

		hasEnd = true;
		routeLocations.add(0, new RouteLocation((ConstraintLayout) ((Activity) context).findViewById(R.id.location_container_start)));
		routeLocations.add(new RouteLocation((ConstraintLayout) ((Activity) context).findViewById(R.id.location_container_end), location));
		container.setVisibility(View.GONE);
	}

//	/**
//	 * @param index    Index of the location in the desired route. Starts at at 0, representing the route start point
//	 * @param location
//	 */
//	public void addMidPoint(int index, Feature location) {
//
//		routeLocations.add(index, location);
//	}

	private void fetchRoute() {

		for (RouteLocation point : routeLocations) {
			router.setLocation(new double[]{point.getFeature().geometry.coordinates.get(1), point.getFeature().geometry.coordinates.get(0)});
		}

		router.fetch();
	}

}
