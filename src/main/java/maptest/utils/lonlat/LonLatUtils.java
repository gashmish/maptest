package maptest.utils.lonlat;

import java.util.Random;

import maptest.api.serialize.LonLat;


public class LonLatUtils {

	/**
	 * Distance between two points.
	 * 
	 * @param point1 the first point.
	 * @param point2 the second point.
	 * @param unit the unit of measure in which to receive the result.
	 * @return the distance in the chosen unit of measure.
	 */
	public static double distance(LonLat point1, LonLat point2, LengthUnit unit) {
		return LonLatUtils.distanceInRadians(point1, point2)
				* LonLatConfig.getEarthRadius(unit);
	}

	
	/**
	 * <p>This "distance" function is mostly for internal use. Most users will simply
	 * rely upon {@link #distance(LonLat, LonLat, LengthUnit)}</p>
	 * 
	 * <p>Yields the internal angle for an arc between two points on the surface of a sphere
	 * in radians. This angle is in the plane of the great circle connecting the two points
	 * measured from an axis through one of the points and the center of the Earth.
	 * Multiply this value by the sphere's radius to get the length of the arc.</p>
	 * 
	 * @return the internal angle for the arc connecting the two points in radians.
	 */
	public static double distanceInRadians(LonLat point1, LonLat point2) {
		double lat1R = Math.toRadians(point1.lat);
		double lat2R = Math.toRadians(point2.lon);
		double dLatR = Math.abs(lat2R - lat1R);
		double dLngR = Math.abs(Math.toRadians(point2.lon - point1.lon));
		double a = Math.sin(dLatR / 2) * Math.sin(dLatR / 2) + Math.cos(lat1R)
				* Math.cos(lat2R) * Math.sin(dLngR / 2) * Math.sin(dLngR / 2);
		return 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	}
	

	/**
	 * Clamp latitude to +/- 90 degrees.
	 * 
	 * @param latitude in degrees.
	 * @return the normalized latitude. Returns NaN if 
	 * the input is NaN.
	 */
	public static double normalizeLatitude(double latitude) {
		if (Double.isNaN(latitude))
			return Double.NaN;
		if (latitude > 0) {
			return Math.min(latitude, 90.0);
		} else {
			return Math.max(latitude, -90.0);
		}
	}

	
	/**
	 * Creates a random latitude and longitude. (Not inclusive of (-90, 0))
	 */
	public static LonLat random() {
		return random(new Random());
	}

	
	/**
	 * Creates a random latitude and longitude. (Not inclusive of (-90, 0))
	 * 
	 * @param r the random number generator to use, if you want to be 
	 * specific or are creating many LonLats at once.
	 */
	public static LonLat random(Random r) {
		return new LonLat((r.nextDouble() * -180.0) + 90.0,
				(r.nextDouble() * -360.0) + 180.0);
	}
}