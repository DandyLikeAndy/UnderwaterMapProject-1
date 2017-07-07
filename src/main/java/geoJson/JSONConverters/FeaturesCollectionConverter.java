package geoJson.JSONConverters;

import com.google.gson.*;
import geoJson.GeoJsonFeatures;
import geoJson.GeoJsonFeaturesCollection;

import java.lang.reflect.Type;

public class FeaturesCollectionConverter implements JsonDeserializer<GeoJsonFeaturesCollection>, JsonSerializer<GeoJsonFeaturesCollection> {
    @Override
    public GeoJsonFeaturesCollection deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return null;
    }

    @Override
    public JsonElement serialize(GeoJsonFeaturesCollection collection, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", collection.getType());
        JsonArray features = new JsonArray();
        collection.getFeatures().forEach(f->{
            features.add(jsonSerializationContext.serialize(f, GeoJsonFeatures.class));
        });
        result.add("features", features);
        return result;
    }
}
