package maptest.core.utils;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import maptest.remote.serialize.LonLat;

public class Convertion {

    public static Coordinate toCoordinate(LonLat point) {
        return new Coordinate(point.lon, point.lat);        
    }
    
    public static Coordinate[] toCoordinates(List<LonLat> points) {
        Coordinate[] coords = new Coordinate[points.size()];
        for (int i = 0; i < points.size(); i++) {
            coords[i] = toCoordinate(points.get(i));
        }
        return coords;
    }
}
