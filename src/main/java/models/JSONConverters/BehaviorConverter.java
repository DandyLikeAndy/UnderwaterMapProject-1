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
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        Behavior behavior = new Behavior(name);
        Behavior.BEHAVIOR_TYPE behaviorType = Behavior.BEHAVIOR_TYPE.valueOf(name.toUpperCase());
        behavior.setType(behaviorType);
        behavior.getOptions().keySet().forEach(k->{
            behavior.setOption(k, jsonObject.get(k).getAsString());
        });

        return behavior;
    }

    @Override
    public JsonElement serialize(Behavior behavior, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("name", behavior.getName());

        behavior.getOptions().forEach((s, o) -> {
            result.addProperty(s, Integer.parseInt((String) o));
        });
        return result;
    }
}
