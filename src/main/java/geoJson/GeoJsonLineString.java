package geoJson;

import java.util.ArrayList;

public class GeoJsonLineString implements GeoJsonGeometry {
    private String type;
    private ArrayList<double[]> coordinates;

    public GeoJsonLineString(){
        type = "LineString";
    }

    public GeoJsonLineString(ArrayList<double[]> coordinates){
        this.coordinates = coordinates;
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
