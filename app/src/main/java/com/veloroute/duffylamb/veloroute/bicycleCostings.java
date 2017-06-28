package com.veloroute.duffylamb.veloroute;


/**
 * Created by Tommy on 28/06/2017.
 */

public class bicycleCostings {

	public enum BikeType {
		ROAD, CITY, CROSS, MOUNTAIN
	} //NB CROSS == Road/mountain hybrid



	private BikeType bicycle_type;
	private int cycling_speed = 0; //KPH
	private double use_roads = -1; // 0 == hate roads 1 == love roads
	private double use_hills = -1; // 0 == avoid hills 1 == no fear


	public BikeType getBicycle_type() {

		return bicycle_type;
	}

	public void setBicycle_type(BikeType bicycle_type) {

		this.bicycle_type = bicycle_type;
	}

	public int getCycling_speed() {

		return cycling_speed;
	}

	public void setCycling_speed(int cycling_speed) {

		this.cycling_speed = cycling_speed;
	}

	public double getUse_roads() {

		return use_roads;
	}

	public void setUse_roads(float use_roads) {

		this.use_roads = use_roads;
	}

	public double getUse_hills() {

		return use_hills;
	}

	public void setUse_hills(float use_hills) {

		this.use_hills = use_hills;
	}
}
