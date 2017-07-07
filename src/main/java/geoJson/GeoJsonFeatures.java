package geoJson;

import java.util.HashMap;

public class GeoJsonFeatures {
    private String type;
    private GeoJsonGeometry geometry;
    private HashMap<String, String> properties;

    public GeoJsonFeatures(){
        this.type = "Feature";
        this.properties = new HashMap<>();
    }

    public GeoJsonFeatures(GeoJsonGeometry geometry) {
        this();
        this.geometry = geometry;
    }

    public GeoJsonFeatures(GeoJsonGeometry geometry, HashMap<String, String> properties) {
        this(geometry);
        this.properties = properties;
    }

    public GeoJsonGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(GeoJsonGeometry geometry) {
        this.geometry = geometry;
    }

    public HashMap<String, String> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }

    public String getType() {
        return type;
    }
}
