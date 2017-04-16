
package maptest.api.serialize.locations;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationsListResponse {

    @JsonProperty("result")
    public List<LocationResponse> result = null;

    @JsonProperty("success")
    public boolean success;


    public LocationsListResponse() {
    }

    public LocationsListResponse(List<LocationResponse> result, boolean success) {
        this.result = result;
        this.success = success;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(result).append(success).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof LocationsListResponse) == false) {
            return false;
        }
        LocationsListResponse rhs = ((LocationsListResponse) other);
        return new EqualsBuilder().append(result, rhs.result).append(success, rhs.success).isEquals();
    }
}
