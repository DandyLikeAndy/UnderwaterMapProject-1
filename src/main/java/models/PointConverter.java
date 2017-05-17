package models;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by User on 17.05.2017.
 */
public class PointConverter implements JsonSerializer<TrackPoint>, JsonDeserializer<TrackPoint> {
    @Override
    public TrackPoint deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        double lat = object.get("lat").getAsDouble();
        double lng = object.get("lng").getAsDouble();
        return new TrackPoint(lat, lng);

    }

    @Override
    public JsonElement serialize(TrackPoint point, Type type, JsonSerializationContext jsonSerializationContext) {
        return null;
    }
}
