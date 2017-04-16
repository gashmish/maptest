package maptest;


import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import maptest.api.serialize.LonLat;
import maptest.api.serialize.LonLatRectangle;
import maptest.service.LocationPointApproxymator;
import maptest.service.ModelContainer;
import maptest.service.RemoteUpdateManager;
import maptest.service.callback.NewTransportsAddedCallback;
import maptest.service.model.LocationPoint;
import maptest.service.model.Path;
import maptest.service.model.Route;
import maptest.service.model.Transport;
import processing.core.PApplet;
import processing.core.PGraphics;

import com.vividsolutions.jts.geom.Coordinate;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap.OpenStreetMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;


public class MapTestApplet extends PApplet {

    ModelContainer modelContainer = new ModelContainer();
    
    RemoteUpdateManager remoteUpdateManager;

    UnfoldingMap map;

    
    public class TransportMarker extends SimplePointMarker {

        Transport transport;

        public TransportMarker(Transport transport) {
            
            super(toLocation(transport.getRecentLocationPoint().coord));

            this.transport = transport;
        }

        @Override
        public void draw(PGraphics pg, float x, float y) {
            
            pg.pushStyle();  
            
            LocationPoint recentLocationPoint = transport.getRecentLocationPoint();
            List<LocationPoint> estimatedLocationPoints = transport.getEstimatedLocationPoints();

            Coordinate approximatedLocationCoord = recentLocationPoint.coord;

            if (estimatedLocationPoints != null) {
            
                approximatedLocationCoord =
                    LocationPointApproxymator.getApproximatedCoordAtTime(
                        System.currentTimeMillis(),
                        estimatedLocationPoints);
            }
            
            // 1) Move marker to next location
            Location approxymatedLocation = toLocation(approximatedLocationCoord);
            setLocation(approxymatedLocation);
            
            if (isSelected()) {
                
                // 1.2) Draw text
 
                fill(0);
                ScreenPosition approximated = map.getScreenPosition(approxymatedLocation);
                text("#" + transport.vehicleId, approximated.x + 10, approximated.y - 20); 
                
                fill(0);
                Integer seconds = (int) ((System.currentTimeMillis() - recentLocationPoint.timestamp) / 1000);
                text("+" + seconds.toString() + " c", approximated.x + 10, approximated.y - 10);
                
                
                // 2) Draw recent location
                
//                fill(200, 0, 200, 100);
//                ScreenPosition recent = map.getScreenPosition(toLocation(recentLocationPoint.coord));
//                ellipse(recent.x, recent.y, 15, 15);
                
                // 3) Draw route
                
                Route route = transport.getRoute();  
            
                if (route != null) {
                    
                    Path path = route.paths.get(recentLocationPoint.pathId);
                    
                    Coordinate[] coords = path.pathLines.getCoordinates();
                    
                    Coordinate prevCoord = null;
                                        
                    for (Coordinate coord : coords) {
                        
                        if (prevCoord != null) {
                            ScreenPosition from = map.getScreenPosition(toLocation(prevCoord));
                            ScreenPosition to = map.getScreenPosition(toLocation(coord));
                            fill(0);
                            line(from.x, from.y, to.x, to.y);
                        }
                        
                        prevCoord = coord;
                    }
                }
                
                // 4) Draw estimated points
                
                if (estimatedLocationPoints != null) {
                    
                    for (LocationPoint p : estimatedLocationPoints) {
                      
                        ScreenPosition position = map.getScreenPosition(toLocation(p.coord));
                        
                        fill(200, 0, 0, 100);
                        ellipse(position.x, position.y, 10, 10);
                        
                        fill(0);
                        seconds = (int) ((p.timestamp - recentLocationPoint.timestamp) / 1000);
                        text("+" + seconds.toString() + " c", position.x + 10, position.y + 10);
                    }
                }
                
                // 5) Draw reachedEstimatedLocation
                
                Coordinate reachedEstimatedCoord = transport.getReachedEstimatedCoord();
                
                if (reachedEstimatedCoord != null) {
                    
                    fill(0, 0, 200, 100);
                    ScreenPosition p = map.getScreenPosition(toLocation(reachedEstimatedCoord));
                    ellipse(p.x, p.y, 10, 10);
                }
            }
            
            pg.popStyle();
            
            super.draw(pg, x, y);
        }
    } 
    
    
    public void setup() {
        
        /* 1) Setun map */
        
        size(1680, 1050);
        map = new UnfoldingMap(this, new OpenStreetMapProvider());
        map.zoomToLevel(12);
        map.panTo(new Location(60.0, 30.0));
 
        MapUtils.createDefaultEventDispatcher(this, map);

        
        /* 2) Init TransportLocationService */
        
        remoteUpdateManager = new RemoteUpdateManager(
            
            modelContainer,
            
            /* Setting callback to obtain newly received transports */    
                
            new NewTransportsAddedCallback() {
                
                @Override
                public void onNewTransportsAdded(List<Transport> transports) {
                                        
                    synchronized (map) {    
                    
                        System.out.println("Add markers");
                        
                        for (Transport transport : transports) {
                            
                            map.addMarker(
                                new TransportMarker(transport)); 
                        }
                    }
                }
            });
        
        
        /* 3) Locations update task */
        
        new Timer().scheduleAtFixedRate(new TimerTask() {
                
                @Override
                public void run() {
                    
                    Location topLeft = map.getTopLeftBorder();
                    Location bottomRight = map.getBottomRightBorder(); 
                    
                    LonLatRectangle displayedMapRectangle =
                        new LonLatRectangle(
                            new LonLat(
                                topLeft.getLon(),
                                topLeft.getLat()),
                            new LonLat(
                                bottomRight.getLon(),
                                bottomRight.getLat()));
                    
                    remoteUpdateManager.updateLocations(displayedMapRectangle);
                }
            }, 
            0,
            10000 
        );
        
    }
    
    
    @Override
    public void mouseClicked()
    {        
        Marker hitMarker =
            map.getFirstHitMarker(mouseX, mouseY);
        
        if (hitMarker != null) {
        
            hitMarker.setSelected(!hitMarker.isSelected());
        }
    }
    
    
    public void draw() {
        
        synchronized (map) {
            
            map.draw();
        }
    }
     
    
    protected Location toLocation(Coordinate coord) {
        
        return new Location(coord.y, coord.x);
    }
    
    
    private static final long serialVersionUID = 1L;
}
