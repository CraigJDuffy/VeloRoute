package com.veloroute.duffylamb.veloroute;


/**
 * Created by Tommy on 28/06/2017.
 */


public class BicycleCostings {

	private BikeType bicycle_type;
	private int cycling_speed; //Km/h
	private double use_roads; // 0 == hate roads 1 == love roads
	private double use_hills; // 0 == avoid hills 1 == no fear
	/**
	 * Constructor which uses the default route finding API values for bicycle costings
	 */
	public BicycleCostings() {

		this(BikeType.ROAD, 25, 0.2, 1);
	}

	/**
	 * Constructor for initialising costing options to passed values
	 *
	 * @param bicycle_type  The type of bike being used.
	 * @param cycling_speed The estimated average speed of the cyclist over the length of the route in <strong> KPH</strong>.
	 * @param use_hills     Desire to tackle hills on route. Range from 0 to 1, where 0 will avoid inclines and 1 denotes no aversion to steep inclines.
	 * @param use_roads     The cyclist's propensity to use roads alongside traffic. Range from 0 to 1, where 0 will avoid roads where possible, and 1 indicates comfort on roads.
	 *
	 * @see BicycleCostings.BikeType
	 */
	public BicycleCostings(BikeType bicycle_type, int cycling_speed, double use_hills, double use_roads) {

		setBicycle_type(bicycle_type);
		setCycling_speed(cycling_speed);
		setUse_hills(use_hills);
		setUse_roads(use_roads);
	}

	public String toJSON() {

		return "\"costing_options\":{\"bicycle\":{\"bicycle_type\":\"" + getBicycle_type().toString() + "\",\"cycling_speed\":" + getCycling_speed() + ",\"use_hills\":" + getUse_hills() + ", \"use_roads\":" + getUse_roads() + "}}";
	}

	public BikeType getBicycle_type() {

		return this.bicycle_type;
	}

	public void setBicycle_type(BikeType bicycle_type) {

		this.bicycle_type = bicycle_type;
	}

	public int getCycling_speed() {

		return this.cycling_speed;
	}

	/**
	 *
	 * @param cycling_speed The average speed of the cyclist. If <code>cycling_speed <= 0</code> then the default for the current bike type is used instead.
	 */
	public void setCycling_speed(int cycling_speed) {

		if (cycling_speed <= 0) cycling_speed=getDefaultSpeed(getBicycle_type());
		this.cycling_speed = cycling_speed;
	}

	/**
	 * Given a bicycle type, returns the API default speed value.
	 *
	 * @param bicycle_type The type of bicycle
	 *
	 * @return <strong>int</strong> The default speed.
	 */
	private int getDefaultSpeed(BikeType bicycle_type) {

		switch (bicycle_type) {
			case ROAD:
				return 25;
			case CITY:
				return 18;
			case CROSS:
				return 20;
			case MOUNTAIN: return 16;
			default:
				return 25;
		}
	}

	public double getUse_roads() {

		return this.use_roads;
	}

	/**
	 * Sets the costing for road use.
	 *
	 * @param use_roads A double value in range 0 to 1 (inclusive). 0 will avoid roads as much as possible, whilst 1 will not.
	 *
	 * @throws IllegalArgumentException If value outwith range.
	 */
	public void setUse_roads(double use_roads) throws IllegalArgumentException {

		if (use_roads < 0.0 || use_roads>1.0) throw new IllegalArgumentException("Value must lie between 0.0 and 1.0 inclusive");
		this.use_roads = use_roads;
	}

	public double getUse_hills() {

		return this.use_hills;
	}

	/**
	 * Sets the costing for road use.
	 *
	 * @param use_hills A double value in range 0 to 1 (inclusive). 0 will avoid hills as much as possible, whilst 1 will not.
	 *
	 * @throws IllegalArgumentException If value outwith range.
	 */
	public void setUse_hills(double use_hills) throws IllegalArgumentException {

		if (use_hills < 0.0 || use_hills>1.0) throw new IllegalArgumentException("Value must lie between 0.0 and 1.0 inclusive");
		this.use_hills = use_hills;
	}

	/**
	 * Enumerate type of bikes recognised by the route finding API. Note that CITY is equivalent to a 'hybrid' bike.
	 */
	public enum BikeType {
		ROAD, CITY, CROSS, MOUNTAIN
	} //NB CITY == HYBRID
}
