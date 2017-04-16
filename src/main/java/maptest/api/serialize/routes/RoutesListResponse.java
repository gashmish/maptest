package maptest.api.serialize.routes;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoutesListResponse {

    @JsonProperty("result")
    public List<RouteResponse> result = null;
    
    @JsonProperty("success")
    public boolean success;

    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(result).append(success).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RoutesListResponse) == false) {
            return false;
        }
        RoutesListResponse rhs = ((RoutesListResponse) other);
        return new EqualsBuilder()
            .append(result, rhs.result)
            .append(success, rhs.success).isEquals();
    }
}