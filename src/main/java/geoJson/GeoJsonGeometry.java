package geoJson;

import java.util.ArrayList;

public interface GeoJsonGeometry {
    String getType();
    ArrayList<double[]> getCoordinates();
    void setCoordinates(ArrayList<double[]> coordinates);
}
