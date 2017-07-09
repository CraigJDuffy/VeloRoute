package com.veloroute.duffylamb.veloroute;


import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.mapzen.android.graphics.MapzenMap;
import com.mapzen.android.graphics.model.Marker;
import com.mapzen.android.routing.MapzenRouter;
import com.mapzen.pelias.gson.Feature;
import com.mapzen.valhalla.RouteCallback;

import java.util.ArrayList;


public class RoutePlanner implements View.OnLongClickListener {

	private LinearLayout promptContainer;
	private ScrollView routeContainer;
	private ArrayList<RouteLocation> routeLocations;
	private Button BtnDirectionTo;
	private Button BtnDirectionFrom;

	private RouteOptions routeOptions;
	private MapzenRouter router;
	private MapzenMap map;

	private Context context;
	private int smartVisibility;
	private Feature currentLocation;
	private boolean hasStart;
	private boolean hasEnd;

	public RoutePlanner(LinearLayout promptContainer) {

		this.context = promptContainer.getContext();
		this.promptContainer = promptContainer;
		this.routeContainer = (ScrollView) ((Activity) context).findViewById(R.id.route_container);



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

		this.BtnDirectionFrom = (Button) promptContainer.findViewById(R.id.btn_direction_from);
		this.BtnDirectionTo = (Button) promptContainer.findViewById(R.id.btn_direction_to);

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

		if (routeLocations.isEmpty()) {

		ConstraintLayout start = (ConstraintLayout) ((Activity) context).findViewById(R.id.location_container_start);
		ConstraintLayout end = (ConstraintLayout) ((Activity) context).findViewById(R.id.location_container_end);
			routeContainer.setVisibility(View.VISIBLE);

			routeLocations.add(new RouteLocation(start));
			routeLocations.add(new RouteLocation(end));

			start.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {

					hasStart = false;
					routeLocations.get(0).removeFeature();
					removeLocationFromUI();

					return true; //Return true to prevent event propagating to any other listeners
				}
			});

			end.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {

					hasEnd = false;
					routeLocations.get(routeLocations.size() - 1).removeFeature();
					removeLocationFromUI();

					return true;
				}
			});
		}

	}

	public void setMap(MapzenMap map) {

		this.map = map;
	}

	private void removeLocationFromUI() {

		map.removePolyline();
		map.removeMarker();

		if (!hasStart && !hasEnd) {
			routeContainer.setVisibility(View.GONE);
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
		if (promptContainer.getVisibility() == View.VISIBLE) {
			promptContainer.setVisibility(View.GONE);
			smartVisibility = 1;
		}
	}

	/**
	 * When used in combination with <code>smartHide</code> this function only shows the UI if it was visible before the call to <code>smartHide</code>. Multiple calls to this function
	 * will perform identically, unless <code>smartHide</code> is called.
	 */
	public void smartShow() {

		if (smartVisibility == 1) {
			promptContainer.setVisibility(View.VISIBLE);
		}
	}

	public void searchResult(Feature feature) {

		currentLocation = feature;

		if (!hasEnd && !hasStart) {
			promptContainer.setVisibility(View.VISIBLE);
		} else {
			if (hasStart && hasEnd) {
				addMidPoint(1, feature);
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
		map.addMarker(new Marker(location.geometry.coordinates.get(0), location.geometry.coordinates.get(1)));

		hasStart = true;
		routeLocations.get(0).setFeature(location);

		if (hasEnd) {
			fetchRoute();
		}

		promptContainer.setVisibility(View.GONE);
	}

	public void addEndPoint(Feature location) {

		populateRouteLocations();

		map.clearSearchResults();
		map.addMarker(new Marker(location.geometry.coordinates.get(0), location.geometry.coordinates.get(1)));

		hasEnd = true;
		routeLocations.get(routeLocations.size() - 1).setFeature(location);

		if (hasStart) {
			fetchRoute();
		}

		promptContainer.setVisibility(View.GONE);
	}

	/**
	 * @param index    Index of the location in the desired route. Starts at at 0, representing the route start point
	 * @param location
	 *
	 * @see <a href="https://developer.android.com/reference/android/view/LayoutInflater.html#inflate(int, android.view.ViewGroup)">Layout Inflator</a>
	 * @see <a href="https://developer.android.com/reference/android/view/ViewGroup.html#addView(android.view.View, int)">AddView by index</a>
	 * @see <a href="https://possiblemobile.com/2013/05/layout-inflation-as-intended/">Inflate layout as intended</a>
	 */
	public void addMidPoint(int index, Feature location) {

		map.clearSearchResults();
		map.addMarker(new Marker(location.geometry.coordinates.get(0), location.geometry.coordinates.get(1)));

		LayoutInflater inflater = LayoutInflater.from(context);
		LinearLayout root = (LinearLayout) routeContainer.getChildAt(0);

		ConstraintLayout routeLocationView = (ConstraintLayout) inflater.inflate(R.layout.route_location_view, root, false); //Inflate the XMl with the child LinearLayout of MaxSizeScrollView as root, auto-attach = false to allow positioning
		routeLocationView.setOnLongClickListener(this);

		root.addView(routeLocationView, index);
		routeLocations.add(index, new RouteLocation(routeLocationView, location));

		fetchRoute();
	}

	private void fetchRoute() {

		router.clearLocations();

		for (RouteLocation point : routeLocations) {
			router.setLocation(new double[]{point.getFeature().geometry.coordinates.get(1), point.getFeature().geometry.coordinates.get(0)});
		}

		router.fetch();
	}

	@Override
	public boolean onLongClick(View v) {

		LinearLayout root = (LinearLayout) routeContainer.getChildAt(0);
		int index = root.indexOfChild(v);

		if (index >= 0) {
			routeLocations.remove(index);
			root.removeViewAt(index);
			removeLocationFromUI();
			fetchRoute();
			return true;
		}
		return false; //Return false here as the long click should remove a location, but none have been removed if here
	}
}
