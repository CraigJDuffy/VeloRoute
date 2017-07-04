package com.veloroute.duffylamb.veloroute;


import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mapzen.android.routing.MapzenRouter;
import com.mapzen.valhalla.RouteCallback;
/**
 * Created by Tommy on 04/07/2017.
 */

public class RoutePlanner {

	private LinearLayout container;
	private Button BtnDirectionTo;
	private Button BtnDirectionFrom;

	private RouteOptions routeOptions;
	private MapzenRouter router;

	private Context context;
	private int smartVisibility;

	public RoutePlanner(LinearLayout container) {

		this.container = container;
		this.BtnDirectionFrom = (Button) container.findViewById(R.id.btn_direction_from);
		this.BtnDirectionTo = (Button) container.findViewById(R.id.btn_direction_to);

		this.context = container.getContext();
		this.smartVisibility = 0;

		this.routeOptions = new RouteOptions(this.context);
		setupRouter();
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

	private void setupRouter() {

		router = new MapzenRouter(context);
		router.setBiking();
		router.getRouter().setHttpHandler(routeOptions);
	}

	public void setRouteCallback(RouteCallback callback) {

		router.setCallback(callback);
	}


}
