package maptest;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.linearref.LengthIndexedLine;

public class Test {

    static GeometryFactory fact = new GeometryFactory();
    static WKTReader rdr = new WKTReader(fact);

    
    public static void main(String[] args) throws Exception {

        Geometry g1 = rdr.read("LINESTRING (10 10, 20 20, 30 30, 40 40)");

        System.out.println("Input Geometry: " + g1);

        LengthIndexedLine indexedLine = new LengthIndexedLine(g1);

        Coordinate point = indexedLine.extractPoint(-1);
        System.out.println("point: " + point);
        
    }
}
