package maptest.service.model;

import java.util.ArrayList;
import java.util.List;

public class Route {

    public int routeId;
    
    public List<Path> paths = new ArrayList<>(2);

    
    public Route(int routeId) {
        
        this.routeId = routeId;
    }
}
