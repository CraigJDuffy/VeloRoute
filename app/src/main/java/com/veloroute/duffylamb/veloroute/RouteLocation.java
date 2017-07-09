package com.veloroute.duffylamb.veloroute;


import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mapzen.pelias.gson.Feature;
/**
 * Created by Tommy on 06/07/2017.
 */

public class RouteLocation {

	private ConstraintLayout container;
	private TextView TxtLocationName;
	private ImageButton BtnGPS;

	private Feature feature;

	public RouteLocation(ConstraintLayout container) {

		this.container = container;
		this.TxtLocationName = (TextView) container.getChildAt(1);
		this.BtnGPS = (ImageButton) container.getChildAt(2);
	}

	public RouteLocation(ConstraintLayout container, Feature feature) {

		this(container);
		this.TxtLocationName.setText(feature.properties.name);
		this.feature = feature;
		this.BtnGPS.setVisibility(View.GONE);
	}

	public void setFeature(Feature feature) {

		this.feature = feature;
		this.BtnGPS.setVisibility(View.GONE);
		this.TxtLocationName.setText(feature.properties.name);
	}

	public void removeFeature() {

		this.feature = null;
		this.BtnGPS.setVisibility(View.VISIBLE);
		this.TxtLocationName.setText(R.string.location_placeholder);
	}

	/**
	 * @return The feature associated with this UI element. Will return <code>null</code> if
	 */
	public Feature getFeature() {

		return this.feature;
	}

	public ConstraintLayout getContainer() {

		return this.container;
	}

	public void setVisibility(int visibility) {

		this.container.setVisibility(visibility);
	}

}
