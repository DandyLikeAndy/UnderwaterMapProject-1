package geoJson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import geoJson.JSONConverters.FeaturesCollectionConverter;
import geoJson.JSONConverters.FeaturesConverter;
import geoJson.JSONConverters.GeometryConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GeoJsonParser {
    public static String toGeoJsonLineString(double[][] latlngs){
        ArrayList<double[]> list = new ArrayList<>(Arrays.asList(latlngs));
        return toGeoJsonLineString(list);
    }

    public static String toGeoJsonLineString(ArrayList<double[]> latlngs){
        GeoJsonFeaturesCollection collection = new GeoJsonFeaturesCollection();
        GeoJsonFeatures features = new GeoJsonFeatures();
        GeoJsonLineString lineString = new GeoJsonLineString();
        lineString.setCoordinates(latlngs);
        features.setGeometry(lineString);
        collection.addFeatures(features);
        return convertCollection(collection);
    }

    public static String toGeoJsonLineString(ArrayList<double[]> latlngs, String name, String color){
        ArrayList<double[]> start = (ArrayList<double[]>) latlngs.subList(0,1);
        ArrayList<double[]> end = (ArrayList<double[]>) latlngs.subList(latlngs.size()-1, latlngs.size());
        GeoJsonFeatures startFeatures = new GeoJsonFeatures();
        GeoJsonFeatures endFeatures = new GeoJsonFeatures();
        GeoJsonGeometry startGeometry = new GeoJsonPoint();
        GeoJsonGeometry endGeometry = new GeoJsonPoint();
        startGeometry.setCoordinates(start);
        endGeometry.setCoordinates(end);
        startFeatures.setGeometry(startGeometry);
        endFeatures.setGeometry(endGeometry);

        GeoJsonFeaturesCollection collection = new GeoJsonFeaturesCollection();
        GeoJsonFeatures features = new GeoJsonFeatures();

        HashMap<String, String> properties = new HashMap<>();
        properties.put("style", "{\"color\":\""+color+"\"}");
        properties.put("name", name);
        features.setProperties(properties);

        GeoJsonLineString lineString = new GeoJsonLineString();
        lineString.setCoordinates(latlngs);
        features.setGeometry(lineString);
        collection.addFeatures(features);
        collection.addFeatures(startFeatures);
        collection.addFeatures(endFeatures);
        return convertCollection(collection);
    }

    private static String convertCollection(GeoJsonFeaturesCollection collection){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting()
                .registerTypeAdapter(GeoJsonFeaturesCollection.class, new FeaturesCollectionConverter())
                .registerTypeAdapter(GeoJsonFeatures.class, new FeaturesConverter())
                .registerTypeAdapter(GeoJsonGeometry.class, new GeometryConverter())
                .create();
        return gson.toJson(collection);

    }

    public static void main(String[] args) {
        double[][] arr = {{2,1}, {3,5}};
        System.out.println(toGeoJsonLineString(arr));
    }
}
