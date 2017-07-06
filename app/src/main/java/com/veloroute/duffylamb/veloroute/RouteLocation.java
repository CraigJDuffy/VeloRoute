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
		this.container.setVisibility(View.VISIBLE);

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
	}

	/**
	 * @return The feature associated with this UI element. Will return <code>null</code> if
	 */
	public Feature getFeature() {

		return this.feature;
	}

}
