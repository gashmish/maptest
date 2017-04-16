package maptest.utils;

import java.awt.geom.Point2D;

import maptest.api.serialize.LonLat;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.GeodeticCalculator;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;


public class GeometryUtils {

    static GeodeticCalculator gc = new GeodeticCalculator();
    

    public static double getDistance(Coordinate a, Coordinate b) {
        
        try {
            
            gc.setStartingPosition(JTS.toDirectPosition(a, gc.getCoordinateReferenceSystem())) ;
            gc.setDestinationPosition(JTS.toDirectPosition(b, gc.getCoordinateReferenceSystem()));
        
        } catch (TransformException e) {
            e.printStackTrace();
        }
        
        return gc.getOrthodromicDistance();
    }
    
	public static LonLat getPointOnDirection(LonLat a, double angle, double meters) {

		// get point on direction
	    gc.setStartingGeographicPoint(a.lon, a.lat);
	    gc.setDirection(angle, meters);
		Point2D destination = gc.getDestinationGeographicPoint();

		return new LonLat(
			destination.getX(),
			destination.getY());
	}
	
}
