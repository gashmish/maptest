package maptest.service;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maptest.api.TransportAPI;
import maptest.api.serialize.LonLatRectangle;
import maptest.api.serialize.locations.LocationResponse;
import maptest.api.serialize.routes.RouteRequest;
import maptest.api.serialize.routes.RouteResponse;
import maptest.service.callback.NewTransportsAddedCallback;
import maptest.service.model.LocationPoint;
import maptest.service.model.Path;
import maptest.service.model.Route;
import maptest.service.model.Transport;
import maptest.service.utils.Convertion;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class RemoteUpdateManager {

    ModelContainer modelContainer;
    
    NewTransportsAddedCallback newTransportsAddedCallback;
    
    TransportAPI api;


    public RemoteUpdateManager(
        ModelContainer modelContainer,
        NewTransportsAddedCallback newTransportsAddedCallback)
    {
        this.modelContainer = modelContainer;
        this.newTransportsAddedCallback = newTransportsAddedCallback;
        this.api = new TransportAPI();
    }
    
    
    static DateTimeFormatter dateTimeFormatter =
        DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    
    public void updateLocations(LonLatRectangle rectangle) {
        
        System.out.println("\nTransportLocationService.updateLocations()");
        
        
        /* 1) Get locations in rectangle from API */
        
        List<LocationResponse> locations =
            api.getLocationsInRectangle(rectangle);
        
        
        /* 2) Process each location and find transports and routes for them */
        
        List<Transport> newTransports = new ArrayList<>();
                        
        THashMap<Integer, List<Transport>> unknownRouteIds2transports = new THashMap<>();
        
        for (LocationResponse locationResponse : locations) {
            
            Transport transport =
                modelContainer.getTransport(locationResponse.vehicleId);
            
            if (transport == null) {
                
                // New transport
                
                // 1. Create
                
                transport = new Transport(
                    locationResponse.vehicleId,
                    locationResponse.vehicleLabel,                
                    locationResponse.orderNumber,
                    locationResponse.licensePlate);
                
                modelContainer.addTransport(transport);

                newTransports.add(transport);
                
                // 2. Find route for new transport

                Route route = modelContainer.getRoute(locationResponse.routeId);
                
                if (route != null) {
                    
                    // Ok, route already exists
                    
                    transport.setRoute(route);
                }
                else {
                    
                    // This route is unknown, adding it to request list
                    
                    List<Transport> transportsWithUnknownRoute =
                        unknownRouteIds2transports.get(locationResponse.routeId);

                    if (transportsWithUnknownRoute == null) {
                        transportsWithUnknownRoute = new ArrayList<>();
                        unknownRouteIds2transports.put(locationResponse.routeId, transportsWithUnknownRoute);
                    }
                    
                    transportsWithUnknownRoute.add(transport);                    
                }        
            }
                        
            LocationPoint locationPoint =
                new LocationPoint(
                    DateTime.parse(locationResponse.timestamp, dateTimeFormatter).getMillis(),
                    Convertion.toCoordinate(locationResponse.position),
                    locationResponse.direction,
                    locationResponse.velocity,
                    locationResponse.directionId);
            
            transport.addLocationPoint(locationPoint);
        }
        
        System.out.println("New transports received: " + newTransports.size());
        
        /* Notify consumer about new transports */
        
        if (!newTransports.isEmpty()) {
            newTransportsAddedCallback.onNewTransportsAdded(newTransports);
        }
        
        
        /* 3) Get unknown routes from API */

        System.out.println("Unknown routes: " + unknownRouteIds2transports.size());
        
        int batchSize = 50; // How much routes API can return at once
        int couter = 0;        
        
        List<RouteRequest> routeRequestsBatch = new ArrayList<>(batchSize);
        
        List<RouteResponse> routeResponses = new ArrayList<>();

        for (Integer unknownRouteId : unknownRouteIds2transports.keySet()) {
            
            /* Two separate requests for both directions */
            routeRequestsBatch.add(new RouteRequest(unknownRouteId, 0));
            routeRequestsBatch.add(new RouteRequest(unknownRouteId, 1));
                        
            couter++;
            
            if (routeRequestsBatch.size() == batchSize
                || couter == unknownRouteIds2transports.size())
            {
                routeResponses.addAll(
                    api.getRoutes(routeRequestsBatch));
                
                routeRequestsBatch.clear();
            }
        }    
        
        
        /* 4) Create new routes from responses */
        
        Map<Integer, Route> newRoutes = new HashMap<>();
        
        for (RouteResponse routeResponse : routeResponses) {
            
            Route route = newRoutes.get(routeResponse.routeId);
            
            if (route == null) {
                route = new Route(routeResponse.routeId);
                newRoutes.put(routeResponse.routeId, route);
            }

            route.paths.add(
                routeResponse.direction,
                new Path(Convertion.toCoordinates(routeResponse.path)));
        }
        
        
        /* 5) Attach new routes to transports */
        
        for (Route newRoute : newRoutes.values()) {
            
            List<Transport> transports =
                unknownRouteIds2transports.get(newRoute.routeId);
            
            if (transports == null) {
                System.err.println("Error in API: unexpected routeId");
                continue;
            }
            
            for (Transport transport : transports) {
                transport.setRoute(newRoute);
            }
            
            modelContainer.addRoute(newRoute);
        }
        
        modelContainer.printModelState();
    }
}
