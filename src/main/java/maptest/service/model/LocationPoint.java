package maptest.service.model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.linearref.LinearLocation;


public class LocationPoint {
    
    /* Original data */
    
    public long timestamp;
  
    public Coordinate coord;
    
    public int direction;

    public int velocity;
   
    public int pathId;
    
    
    /* Location in path */
        
    protected LinearLocation locationInPath;  

    
    public LocationPoint(
        long timestamp,
        Coordinate location,
        int direction,
        int velocity,
        int pathId)
    {
        this.timestamp = timestamp;
        this.coord = location;
        this.direction = direction;
        this.velocity = velocity;
        this.pathId = pathId;
    } 
    
    
    public synchronized LinearLocation getLocationInPath() {
        
        return locationInPath;
    }
    
    
    public synchronized void setLocationInPath(LinearLocation locationInPath) {
        
        this.locationInPath = locationInPath;
    }


    @Override
    public String toString() {
        return "LocationPoint [timestamp=" + timestamp + ", coord=" + coord
                + ", direction=" + direction + ", velocity=" + velocity
                + ", pathId=" + pathId + ", locationInPath=" + locationInPath
                + "]";
    }
}
