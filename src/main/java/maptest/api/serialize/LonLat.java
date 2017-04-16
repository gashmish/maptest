
package maptest.api.serialize;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LonLat {

    @JsonProperty("lon")
    public double lon;
    
    @JsonProperty("lat")
    public double lat;


    public LonLat() {
    }

    public LonLat(double lon, double lat) {
        super();
        this.lon = lon;
        this.lat = lat;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(lon).append(lat).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof LonLat) == false) {
            return false;
        }
        LonLat rhs = ((LonLat) other);
        return new EqualsBuilder().append(lon, rhs.lon).append(lat, rhs.lat).isEquals();
    }

}
