package maptest.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Coordinate;

import maptest.core.model.LocationPoint;
import maptest.core.model.Route;
import maptest.core.model.Transport;
import maptest.remote.serialize.LonLatRectangle;
import maptest.web.resp.TransportEstimate;


@Component
public class ModelContainer {
    
    Map<Integer, Transport> transports = new HashMap<>();
    
    Map<Integer, Route> routes = new HashMap<>();
    
    
    public synchronized Transport getTransport(Integer transportId) {
        
        return transports.get(transportId);
    }
    

    public synchronized void addTransport(Transport transport) {
        
        transports.put(transport.vehicleId, transport);
    }
    
    
    public synchronized Route getRoute(Integer routeId) {
        
        return routes.get(routeId);
    }
 
    
    public synchronized void addRoute(Route route) {
        
        routes.put(route.routeId, route);
    }
    
    public synchronized List<TransportEstimate> getTransportEstimatesInRect(LonLatRectangle rect) {
        
        List<TransportEstimate> result = new ArrayList<>();
        
        for (Transport transport : transports.values()) {
                
            LocationPoint recent = transport.getRecentLocationPoint();
            
            if (recent != null) {
                
                Coordinate coord = recent.coord;
                
                if (
                    rect.topLeft.lon <= coord.x && coord.x < rect.bottomRight.lon
                    &&
                    rect.topLeft.lat <= coord.y && coord.y < rect.bottomRight.lat)
                {
                    result.add(new TransportEstimate(
                        transport.vehicleId,
                        transport.getEstimatedLocationPoints()
                    ));
                }
            }
        }
        
        return result;
    }
    
    
    public synchronized void printModelState() {

        System.out.println("Total transports: " + transports.size());
        System.out.println("Total routes: " + routes.size());
    }
}
