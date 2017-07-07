package geoJson.JSONConverters;

import com.google.gson.*;
import geoJson.GeoJsonFeatures;
import geoJson.GeoJsonGeometry;

import java.lang.reflect.Type;
import java.util.Map;

public class FeaturesConverter implements JsonDeserializer<GeoJsonFeatures>, JsonSerializer<GeoJsonFeatures> {
    @Override
    public GeoJsonFeatures deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return null;
    }

    @Override
    public JsonElement serialize(GeoJsonFeatures features, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", features.getType());
        JsonElement geometry = jsonSerializationContext.serialize(features.getGeometry(), GeoJsonGeometry.class);
        result.add("geometry", geometry);
        JsonObject properties = new JsonObject();
        for (Map.Entry<String, String> e :
                features.getProperties().entrySet()) {
            properties.addProperty(e.getKey(), e.getValue());
        }
        result.add("properties", properties);
        return result;
    }
}
