package maptest.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Configuration parameters for latitude and longitude calculations.
 */
public class LonLatConfig {

	public static final NumberFormat DEGREE_FORMAT = new DecimalFormat("0.000000");

	/**
	 * The Earth's mean radius in kilometers. Used as the default radius 
	 * for calculations.
	 */
	public static final double EARTH_MEAN_RADIUS_KILOMETERS = 6371.009;

	/**
	 * Earth's radius stored in all of the support unit types.
	 * This is pre-calculated to eliminate unit conversions
	 * when doing many distance calculations.
	 */
	private static double[] EARTH_RADIUS;

	static {
		// Initialize earth radius using the mean radius.
		setEarthRadius(EARTH_MEAN_RADIUS_KILOMETERS, LengthUnit.KILOMETER);
	}

	/**
	 * Retrieve the Earth's spherical approximation radius in the desired unit.
	 * 
	 * @param unit the desired unit for the result.
	 * @return the Earth's radius in the desired unit.
	 */
	public static double getEarthRadius(LengthUnit unit) {
		return EARTH_RADIUS[unit.ordinal()];
	}

	/**
	 * Sets the Earth's radius for the purposes of all future 
	 * calculations in this library. If there is a radius that 
	 * is more accurate for the locations you most care about,
	 * you can configure that here.
	 * 
	 * @param radius the Earth's spherical approximation radius.
	 * @param unit the unit the radius is given in.
	 */
	synchronized public static void setEarthRadius(double radius, LengthUnit unit) {
		EARTH_RADIUS = new double[LengthUnit.values().length];
		for (LengthUnit toUnit : LengthUnit.values()) {
			EARTH_RADIUS[toUnit.ordinal()] = unit.convertTo(toUnit, radius);
		}
	}
}