package maptest.api;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import maptest.api.serialize.LonLat;
import maptest.api.serialize.LonLatRectangle;
import maptest.api.serialize.locations.LocationResponse;
import maptest.api.serialize.locations.LocationsListResponse;
import maptest.api.serialize.routes.RouteRequest;
import maptest.api.serialize.routes.RouteResponse;
import maptest.api.serialize.routes.RoutesListResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;


public class TransportAPI {
        
    String serverString =
        "http://portal.gpt.adc.spb.ru" +
        "/Portal/transport/internalapi";
    
    
    public TransportAPI() {
    }
    
    
    public TransportAPI(String serverString) {
        this.serverString = serverString;
    }

    
    public List<LocationResponse> getLocationsInRectangle(LonLatRectangle rectangle) {

        String query = serverString +
            "/vehicles/positions/?" +
            "transports=bus,trolley,tram&bbox=" +
            rectangle.topLeft.lon     + "," +
            rectangle.topLeft.lat     + "," +
            rectangle.bottomRight.lon + "," +
            rectangle.bottomRight.lat;
        
        System.out.println("Requesting transport locations: " + query);
                
        try {
            
            LocationsListResponse response =
                getHttpResponse(query, LocationsListResponse.class);
            
            if (response.success) {
                
                System.out.println("Transport locations received: " + response.result.size());
                
                return response.result;
            }
        
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        
        System.out.println("Failed to get transport location!");
        
        return Collections.emptyList();
    }
    

    public List<RouteResponse> getRoutes(List<RouteRequest> routeIdentifier) {

        String routes = "";
        String routeDirections = "";
        
        int i = 0;
        
        for (RouteRequest routeId : routeIdentifier) {
            
            routes += routeId.routeId;
            routeDirections += routeId.routeDirection;
            
            if (i < routeIdentifier.size() - 1) {
                routes += ",";
                routeDirections += ",";
            }
            
            i++;
        }
        
        String query = serverString +
            "/routes/stops/?" + 
            "routeIDs=" + routes +
            "&directions=" + routeDirections;
        
        System.out.println("Requesting transport routes: " + query);
  
         
        try {
            
            RoutesListResponse response =
                getHttpResponse(query, RoutesListResponse.class);
            
            if (response.success) {
                
                System.out.println("Transport routes received: " + response.result.size());
                
                return response.result;
            }
        
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        
        System.out.println("Failed to get transport routes!");
        
        return Collections.emptyList();
    }
    
    
    protected HttpClient httpClient = HttpClientBuilder.create().build();
    
    protected ObjectMapper jsonMapper = new ObjectMapper();
    
    
    protected <T> T getHttpResponse(String query, Class<T> responseClass) throws Exception {
        
        HttpResponse response = httpClient.execute(new HttpGet(query));
        String result = new BasicResponseHandler().handleResponse(response);

        return jsonMapper.readValue(result, responseClass);
    }    


    public static void main(String[] args) throws UnknownHostException, Exception {

        TransportAPI access = new TransportAPI();
        
        List<LocationResponse> transportLocations =
            access.getLocationsInRectangle(
                new LonLatRectangle(
                    new LonLat(29.675824, 60.153766),
                    new LonLat(30.626141, 59.813023)));
        
        for (LocationResponse location : transportLocations) {
            
            //System.out.println(location);
        }
        
        List<RouteResponse> transportRoutes =
            access.getRoutes(
                Collections.singletonList(
                    new RouteRequest(1278, 0)));
        
        for (RouteResponse route : transportRoutes) {
            
            //System.out.println(route);
        }
    }
}
