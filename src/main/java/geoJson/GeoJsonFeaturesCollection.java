package geoJson;

import java.util.ArrayList;

public class GeoJsonFeaturesCollection {
    private String type;
    private ArrayList<GeoJsonFeatures> features;

    public GeoJsonFeaturesCollection(){
        type = "FeatureCollection";
        this.features = new ArrayList<>();
    }

    public GeoJsonFeaturesCollection(ArrayList<GeoJsonFeatures> features){
        this();
        this.features = features;
    }

    public ArrayList<GeoJsonFeatures> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<GeoJsonFeatures> features) {
        this.features = features;
    }

    public void addFeatures(GeoJsonFeatures feature){
        features.add(feature);
    }

    public String getType() {
        return type;
    }
}
