package maptest.service;

import java.util.ArrayList;
import java.util.List;

import maptest.service.model.LocationPoint;
import maptest.service.model.Path;
import maptest.service.model.Route;
import maptest.service.model.Transport;
import maptest.utils.GeometryUtils;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;

public class LocationPointApproxymator {

    /* How much recent location points to use in approximation */

    public static int RECENT_LOCATION_POINTS = 15;
    public static int ESTIMATED_LOCATION_POINTS = 15;
    
    
    public static Coordinate getApproximatedCoordAtTime(long timestamp, List<LocationPoint> estimatedLocationPoints) {
        
        int i = 0;
        
        while (i < estimatedLocationPoints.size()) {
            
            LocationPoint p = estimatedLocationPoints.get(i);
            
            if (timestamp <= p.timestamp) {
                break;
            }
            
            i++;
        }
        
        if (i == 0) {
            return estimatedLocationPoints.get(0).coord;
        }
        
        if (i == estimatedLocationPoints.size()) {
            return estimatedLocationPoints.get(estimatedLocationPoints.size() - 1).coord;
        }
        
        LocationPoint from = estimatedLocationPoints.get(i - 1);
        LocationPoint to = estimatedLocationPoints.get(i);
               
        double a = (timestamp - from.timestamp) / (double) (to.timestamp - from.timestamp);
        
        return new Coordinate(
            (1 - a) * from.coord.x + a * to.coord.x,
            (1 - a) * from.coord.y + a * to.coord.y);
    }
    
    
    public static List<LocationPoint> getEstimatedLocationPoints(Transport transport) {
        

        LocationPoint recentLocationPoint = transport.getRecentLocationPoint();
        
        
        /* 1) Get path geometry data */
        
        Route route = transport.getRoute();
        
        if (route == null) {
            
            /* No approximation without route */
            return null;
        }
        
        Path path = route.paths.get(recentLocationPoint.pathId);

        LineString pathLines = path.pathLines;
        LocationIndexedLine locationInPathIndex = path.locationInPathIndex;
        
        
        /* 2) Calculate location in path if not present */
        
        LinearLocation recentLocationInPath = recentLocationPoint.getLocationInPath();
        
        Coordinate recentLocationInPathCoord = null;
        
        if (recentLocationInPath == null) {
            
            /* 2.1) Find nearest point in path */
            
            recentLocationInPath = locationInPathIndex.project(recentLocationPoint.coord);
            
            if (recentLocationInPath == null) {
                System.err.println("recentLocationInPath is not valid!");
                return null;
            }
            
            /* 2.2) Check if transport not too far from it's route */
            
            recentLocationInPathCoord = locationInPathIndex.extractPoint(recentLocationInPath);
            
            double distance = GeometryUtils.getDistance(recentLocationPoint.coord, recentLocationInPathCoord);
            
            if (distance > 50) { // 20 meters
                
                System.out.println("Transport " + transport.vehicleId + " is too far (" + distance + ") from it's route, no approximation!");
                // Transport is too far from route, no approximation
                return null;
            }
            
            recentLocationPoint.setLocationInPath(recentLocationInPath);
        }
        
        
        if (recentLocationInPathCoord == null) {
            
            recentLocationInPathCoord = locationInPathIndex.extractPoint(recentLocationInPath);
        }
        

        /* 3) Approximate using velocity */
        
        double velocity = recentLocationPoint.velocity / 3.6;
        
        velocity = Math.max(1, velocity);
        
        /* 4) Отмеряем расстояние на пути начиная с recentLocationInPathCoord */
        
        List<LocationPoint> estimatedLocationPoints = new ArrayList<>(ESTIMATED_LOCATION_POINTS);
        
        
        /* 4.1) First point is simply recent point projected on path */
        
        estimatedLocationPoints.add(
            new LocationPoint(
                recentLocationPoint.timestamp,
                recentLocationInPathCoord,
                0,
                0,
                recentLocationPoint.pathId));
                
        /* 4.2) Estimated points are at subsequent path vertices */
        
        long prevTimestamp = recentLocationPoint.timestamp;
        Coordinate prevCoord = recentLocationInPathCoord;
        int vertex = recentLocationInPath.getSegmentIndex() + 1;
        
        while (
            estimatedLocationPoints.size() < ESTIMATED_LOCATION_POINTS
            &&
            vertex < path.pathLines.getNumPoints()
        ) {
            
            Coordinate coord = pathLines.getPointN(vertex).getCoordinate();
            
            double distance = GeometryUtils.getDistance(prevCoord, coord);

            double elapsedTime = distance / velocity; // in seconds
            
            //System.out.println(distance + " " + elapsedTime);
            
            long timestamp = prevTimestamp + (long)(elapsedTime * 1000); // in milliseconds
            
            estimatedLocationPoints.add(
                new LocationPoint(
                    timestamp,
                    coord,
                    0,
                    0,
                    recentLocationPoint.pathId));
            
            prevTimestamp = timestamp;
            prevCoord = coord;
            vertex++;
        }
        
        return estimatedLocationPoints;
    }
    
    
    /* 3) Approximate using velocity */
    
    /*
     * !!! Время на удаленном сервере может отличаться !!!
     * TODO: нужно запоминать последнее время из данных
     */
    
//    double velocity = recentLocationPoint.velocity / 3.6;
//    
//    DateTime currentTime = DateTime.now();
//    
//    double d_t =
//        (currentTime.getMillis() - recentLocationPoint.timestamp.getMillis())
//        / 1000.0;
//    
//    if (d_t < 1) {
//        System.out.println("LocationPointApproxymator: invalid time");
//        return;
//    }
//    
//    double length = velocity * d_t;
//    
//    if (length < 1) {
//        //System.err.println("LocationPointApproxymator: invalid length");
//        return;
//    }
    
    //System.out.println("d_t:" + d_t);
    //System.out.println("LEngth:" + length);
    
    
    
    //public static void applyApproximation(Transport transport) {

        //List<LocationPoint> locationPoints = transport.getRecentLocationPoints();
        
        //LocationPoint recentLocationPoint = locationPoints.get(locationPoints.size() - 1);
        
        /* 1) Получаем значения средних скоростей на каждом из N последних известных отрезков */
        
        /*
        
        if (locationPoints.size() < 2) {
            transport.setApproxymatedLocationPoint(recentLocationPoint);
            return;
        }
        
        double a = 0;
        double b = 0;
        
        for (int i = 0; i < Math.min(RECENT_LOCATION_POINTS, locationPoints.size()) - 1; i++) {

            int j = locationPoints.size() - i - 1;
            
            LocationPoint p1 = locationPoints.get(j);
            
            LocationPoint p2 = locationPoints.get(j - 1);

            double d_t = (p2.timestamp.getMillis() - p1.timestamp.getMillis()) / 1000.0;
            
            if (d_t < 0.1) {
                continue;
            }
            
            double d_s = LonLatUtils.distance(p1.position, p2.position, LengthUnit.METER);
            
            double v = d_s / d_t;
            
            double alpha = j + 1;
            
            a += (v * alpha);
            b += alpha;
        }
        
        // 2) Вычисляем среднее значение из этих скоростей (с учетом весовых коэффициэнтов)
        
        if (b > 0) {
             v_mean = a / b;
        }
        */
        
        //double v_mean = recentLocationPoint.velocity / 3.6;
                
        //System.out.println(recentLocationPoint.velocity);
        
        /* 3) Получаем аппроксимированное расстояние */
        
        /*
         * !!! Время на удаленном сервере может отличаться!
         * TODO: нужно запоминать последнее время из данных
         */
        
//        DateTime currentTime = DateTime.now();
//        
//        double d_t = (
//            //currentTime.getMillis() -
//            System.currentTimeMillis() -
//            recentLocationPoint.timestamp.getMillis()) / 1000.0;
//        
//        if (d_t < 1) {
//            transport.setApproxymatedLocationPoint(recentLocationPoint);
//            System.out.println("LocationPointApproxymator: invalid time");
//            return;
//        }
//        
//        double length = v_mean * d_t;
//        
//        if (length < 1) {
//            transport.setApproxymatedLocationPoint(recentLocationPoint);
//            return;
//        }
        
        //System.out.println("d_t:" + d_t);
        //System.out.println("LEngth:" + length);
        
        /* 4) Отмеряем расстояние на пути начиная с recentLocationPoint */
        
        /*
        TransportRoute route = transport.getRoute(recentLocationPoint.directionId);
        
        if (route == null) {
            transport.setApproxymatedLocationPoint(recentLocationPoint);
            return;
        }
        
        List<LonLat> pathPoints = route.path;
        */

    //}
}
