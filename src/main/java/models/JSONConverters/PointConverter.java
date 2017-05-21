package models.JSONConverters;

import com.google.gson.*;
import models.Waipoint;

import java.lang.reflect.Type;

/**
 * Created by User on 17.05.2017.
 */
public class PointConverter implements JsonSerializer<Waipoint>, JsonDeserializer<Waipoint> {
    @Override
    public Waipoint deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        double lat = object.get("lat").getAsDouble();
        double lng = object.get("lng").getAsDouble();
        return new Waipoint(lat, lng);

    }

    @Override
    public JsonElement serialize(Waipoint point, Type type, JsonSerializationContext jsonSerializationContext) {
        return null;
    }
}
