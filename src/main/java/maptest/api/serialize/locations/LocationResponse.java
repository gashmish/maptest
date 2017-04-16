
package maptest.api.serialize.locations;

import maptest.api.serialize.LonLat;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationResponse {

    @JsonProperty("vehicleId")
    public int vehicleId;
    
    @JsonProperty("timestamp")
    public String timestamp;
  
    @JsonProperty("position")
    public LonLat position;

    @JsonProperty("velocity")
    public int velocity;
    
    @JsonProperty("direction")
    public int direction;
    
    @JsonProperty("routeId")
    public Integer routeId;
    
    @JsonProperty("directionId")
    public int directionId;
    
    @JsonProperty("vehicleLabel")
    public String vehicleLabel;
    
    @JsonProperty("orderNumber")
    public Integer orderNumber;
    
    @JsonProperty("licensePlate")
    public String licensePlate;
    
    
    public LocationResponse() {
    }

    public LocationResponse(
        String timestamp,
        LonLat position,
        int direction,
        Integer routeId,
        String vehicleLabel,
        int velocity,
        int vehicleId,
        Integer orderNumber,
        String licensePlate,
        int directionId)
    {
        this.timestamp = timestamp;
        this.position = position;
        this.direction = direction;
        this.routeId = routeId;
        this.vehicleLabel = vehicleLabel;
        this.velocity = velocity;
        this.vehicleId = vehicleId;
        this.orderNumber = orderNumber;
        this.licensePlate = licensePlate;
        this.directionId = directionId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
