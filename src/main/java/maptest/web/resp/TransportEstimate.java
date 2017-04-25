package maptest.web.resp;

import java.util.List;

import maptest.core.model.LocationPoint;

public class TransportEstimate {

    public int vehicleId;
    
    public List<LocationPoint> estimatedLocationPoints;

    public TransportEstimate(
        int vehicleId,
        List<LocationPoint> estimatedLocationPoints)
    {
        this.vehicleId = vehicleId;
        this.estimatedLocationPoints = estimatedLocationPoints;
    }
}
