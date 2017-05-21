package models.JSONConverters;

import com.google.gson.*;
import models.Waypoint;

import java.lang.reflect.Type;

/**
 * Created by User on 17.05.2017.
 */
public class PointConverter implements JsonSerializer<Waypoint>, JsonDeserializer<Waypoint> {
    @Override
    public Waypoint deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        double lat = object.get("lat").getAsDouble();
        double lng = object.get("lng").getAsDouble();
        int id = object.get("id").getAsInt();
        int position = object.get("pos").getAsInt();
        Waypoint waypoint = new Waypoint(lat, lng);
        waypoint.setId(id);
        waypoint.setPosition(position);
        return new Waypoint(lat, lng);

    }

    @Override
    public JsonElement serialize(Waypoint point, Type type, JsonSerializationContext jsonSerializationContext) {
        return null;
    }
}
