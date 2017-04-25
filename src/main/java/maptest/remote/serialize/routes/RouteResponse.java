package maptest.remote.serialize.routes;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import maptest.remote.serialize.LonLat;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteResponse {

    @JsonProperty("routeId")
    public Integer routeId;
    
    @JsonProperty("direction")
    public int direction;
    
    @JsonProperty("path")
    public List<LonLat> path = null;
    
    @JsonProperty("stopIDs")
    public List<Integer> stopIDs = null;

    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}