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


public class RoutePlanner implements View.OnLongClickListener {

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
			}
		});

		BtnDirectionTo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				addEndPoint(currentLocation);
			}
		});


	}

	private void populateRouteLocations() {

		ConstraintLayout start = (ConstraintLayout) ((Activity) context).findViewById(R.id.location_container_start);
		ConstraintLayout end = (ConstraintLayout) ((Activity) context).findViewById(R.id.location_container_end);

		if (routeLocations.isEmpty()) {
			routeLocations.add(new RouteLocation(start));
			routeLocations.add(new RouteLocation(end));

			start.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {

					hasStart = false;
					routeLocations.get(0).removeFeature();
					redrawMap();

					return true; //Return true to prevent event propagating to any other listeners
				}
			});

			end.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {

					hasEnd = false;
					routeLocations.get(routeLocations.size() - 1).removeFeature();
					redrawMap();

					return true;
				}
			});
		}

	}

	public void setMap(MapzenMap map) {

		this.map = map;
	}

	private void redrawMap() {

		map.clearRouteLine();
		map.removeMarker();

		if (!hasStart && !hasEnd) {
			for (RouteLocation point : routeLocations) {
				point.setVisibility(View.GONE);
			}
			routeLocations.clear();
			return;
		}

		for (RouteLocation point : routeLocations) {
			if (point.getFeature() != null) {
				map.addMarker(new Marker(point.getFeature().geometry.coordinates.get(0), point.getFeature().geometry.coordinates.get(1)));
			}
		}

	}


	//TODO Adjust smartVisibilty functions to function with the new locations UI

	/**
	 * Convenience method for temporarily hiding the route planner UI. When used in combination with <code>smartShow</code> allows the UI visibility to be toggled
	 * without checking if the UI was visible in the first place. <strong>Multiple calls to this function will prevent <code>smartShow</code> from dsiplaying the UI</strong>
	 */
	public void smartHide() {

		smartVisibility = 0;
		if (container.getVisibility() == View.VISIBLE) {
			container.setVisibility(View.GONE);
			smartVisibility = 1;
		}
	}

	/**
	 * When used in combination with <code>smartHide</code> this function only shows the UI if it was visible before the call to <code>smartHide</code>. Multiple calls to this function
	 * will perform identically, unless <code>smartHide</code> is called.
	 */
	public void smartShow() {

		if (smartVisibility == 1) {
			container.setVisibility(View.VISIBLE);
		}
	}

	public void searchResult(Feature feature) {

		currentLocation = feature;

		if (!hasEnd && !hasStart) {
			container.setVisibility(View.VISIBLE);
		} else {
			if (hasStart && hasEnd) {
				//addMidpoint(feature); TODO
			} else if (hasStart) {
				addEndPoint(feature);
			} else if (hasEnd) {
				addStartPoint(feature);
			}
		}
	}


	public void setRouteCallback(RouteCallback callback) {

		router.setCallback(callback);
	}

	//See https://stackoverflow.com/questions/13114966/android-how-to-get-view-from-context

	public void addStartPoint(Feature location) {

		populateRouteLocations();

		map.clearSearchResults();
		map.addMarker(new Marker(currentLocation.geometry.coordinates.get(0), currentLocation.geometry.coordinates.get(1)));

		hasStart = true;
		routeLocations.get(0).setFeature(location);

		if (hasEnd) {
			fetchRoute();
		}

		container.setVisibility(View.GONE);
	}

	public void addEndPoint(Feature location) {

		populateRouteLocations();

		map.clearSearchResults();
		map.addMarker(new Marker(currentLocation.geometry.coordinates.get(0), currentLocation.geometry.coordinates.get(1)));

		hasEnd = true;
		routeLocations.get(routeLocations.size() - 1).setFeature(location);

		if (hasStart) {
			fetchRoute();
		}

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

	@Override
	public boolean onLongClick(View v) {

		for (RouteLocation point : routeLocations) {
			if (v == point.getContainer()) {
				routeLocations.remove(point);
				return true;
			}
		}

		return false; //Return false here as the long click should remove a location, but none have been removed if here
	}
}
