package maptest.service.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import maptest.service.LocationPointApproxymator;

import com.vividsolutions.jts.geom.Coordinate;


public class Transport {
        
    public int vehicleId;
    
    public String vehicleLabel;
    
    public Integer orderNumber;
    
    public String licensePlate;
    
    
    protected Route route;
        
    
    protected LinkedList<LocationPoint> recentLocationPoints = new LinkedList<>();

    protected List<LocationPoint> estimatedLocationPoints;
    
    protected Coordinate reachedEstimatedCoord;
    
    
    public Transport(
        int vehicleId,
        String vehicleLabel,
        Integer orderNumber,
        String licensePlate)
    {
        this.vehicleId = vehicleId;
        this.vehicleLabel = vehicleLabel;
        this.orderNumber = orderNumber;
        this.licensePlate = licensePlate;
    }
    
    
    public synchronized Route getRoute() {
        
        return route;
    }
    
    
    public synchronized void setRoute(Route route) {
        
        this.route = route;
        
        updateEstimatedLocations();
    }

    
    public synchronized Coordinate getReachedEstimatedCoord() {
        
        return reachedEstimatedCoord;
    }
    
    
    public synchronized LocationPoint getRecentLocationPoint() {
        
        return recentLocationPoints.getLast();
    }
    
    
    public synchronized void addLocationPoint(LocationPoint locationPoint) {
        
        /* 1) Check if we already have this LocationPoint */
       
        if (!recentLocationPoints.isEmpty()) {
            
            LocationPoint last = recentLocationPoints.getLast();
            
            if (last.timestamp == locationPoint.timestamp) {
                return;
            }
        }
        
        /* 2) Add new point, maintain list size */
        
        recentLocationPoints.add(locationPoint);
        if (recentLocationPoints.size() > LocationPointApproxymator.RECENT_LOCATION_POINTS) {
            recentLocationPoints.pop();
        }
        
        /* 3) Udate estimates */
        
        if (estimatedLocationPoints != null) {
            reachedEstimatedCoord =
                LocationPointApproxymator.getApproximatedCoordAtTime(
                    locationPoint.timestamp,
                    estimatedLocationPoints);
        } else {
            reachedEstimatedCoord = null;
        }
        
        updateEstimatedLocations();
    }
    
    
    public synchronized List<LocationPoint> getEstimatedLocationPoints() {
        
        if (estimatedLocationPoints == null) {
            
            return null;
        }
        
        return new ArrayList<>(estimatedLocationPoints);
    }
        
    
    protected void updateEstimatedLocations() {
        
        if (!recentLocationPoints.isEmpty()) {
            
            estimatedLocationPoints = LocationPointApproxymator.getEstimatedLocationPoints(this);
        }
    }
}
