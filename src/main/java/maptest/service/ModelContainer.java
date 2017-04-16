package maptest.service;

import java.util.HashMap;
import java.util.Map;

import maptest.service.model.Route;
import maptest.service.model.Transport;

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
    
    
    public synchronized void printModelState() {

        System.out.println("Total transports: " + transports.size());
        System.out.println("Total routes: " + routes.size());
    }
}
