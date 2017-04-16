package maptest.api.serialize;



public class LonLatRectangle {
    
    public LonLat topLeft;
    
    public LonLat bottomRight;

    
    public LonLatRectangle(LonLat topLeft, LonLat bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    @Override
    public String toString() {
        return "LonLatRectangle [topLeft=" + topLeft + ", bottomRight="
                + bottomRight + "]";
    }
}
