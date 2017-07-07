package geoJson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import geoJson.JSONConverters.FeaturesCollectionConverter;
import geoJson.JSONConverters.FeaturesConverter;
import geoJson.JSONConverters.GeometryConverter;

import java.util.ArrayList;
import java.util.Arrays;

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
