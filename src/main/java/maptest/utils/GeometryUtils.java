package maptest.utils;

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
}
