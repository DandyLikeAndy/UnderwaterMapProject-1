package models.JSONConverters;

import com.google.gson.*;
import models.behavors.Behavior;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;

/**
 * @autor slonikmak on 30.05.2017.
 */
public class BehaviorConverter implements JsonDeserializer<Behavior>, JsonSerializer<Behavior> {
    @Override
    public Behavior deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return null;
    }

    @Override
    public JsonElement serialize(Behavior behavior, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("name", behavior.getName());

        behavior.getOptions().forEach((s, o) -> {
            result.addProperty(s, o.toString());
        });
        return result;
    }
}
