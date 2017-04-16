package maptest.service.model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.linearref.LocationIndexedLine;


public class Path {

    public LineString pathLines;
    
    public LocationIndexedLine locationInPathIndex;
    
    
    public Path(Coordinate[] coords) {
        
        pathLines = new LineString(
             new CoordinateArraySequence(coords, 2),
             geometryFactory);
        
        locationInPathIndex = new LocationIndexedLine(pathLines);
    }
    
    
    static GeometryFactory geometryFactory = new GeometryFactory();
}