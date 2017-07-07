package geoJson.JSONConverters;

import com.google.gson.*;
import geoJson.GeoJsonFeatures;
import geoJson.GeoJsonGeometry;

import java.lang.reflect.Type;

public class GeometryConverter implements JsonDeserializer<GeoJsonGeometry>, JsonSerializer<GeoJsonGeometry> {
    @Override
    public GeoJsonGeometry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return null;
    }

    @Override
    public JsonElement serialize(GeoJsonGeometry geoJsonGeometry, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", geoJsonGeometry.getType());
        JsonArray coordinates = new JsonArray();
        geoJsonGeometry.getCoordinates().forEach(c->{
            coordinates.add(jsonSerializationContext.serialize(c));
        });
        result.add("coordinates", coordinates);
        return result;
    }
}
