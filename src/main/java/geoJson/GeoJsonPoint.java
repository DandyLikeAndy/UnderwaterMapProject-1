package geoJson;

import java.util.ArrayList;

public class GeoJsonPoint implements GeoJsonGeometry {
    String type;
    ArrayList<double[]> coordinates;

    public GeoJsonPoint(){
        type = "Point";
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public ArrayList<double[]> getCoordinates() {
        return coordinates;
    }

    @Override
    public void setCoordinates(ArrayList<double[]> coordinates) {
        this.coordinates = coordinates;
    }
}
