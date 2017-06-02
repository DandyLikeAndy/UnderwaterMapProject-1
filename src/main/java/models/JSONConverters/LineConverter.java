package models.JSONConverters;

import com.google.gson.*;
import models.TrackLine;
import models.Waypoint;
import models.behavors.Behavior;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by User on 17.05.2017.
 */
public class LineConverter implements JsonSerializer<TrackLine>, JsonDeserializer<TrackLine> {
    @Override
    public TrackLine deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        ArrayList<Waypoint> pointsArr = new ArrayList<>();
        int id = object.get("id").getAsInt();
        JsonArray points = object.getAsJsonArray("points");
        points.forEach(p->{
            pointsArr.add(jsonDeserializationContext.deserialize(p, Waypoint.class));
        });
        return new TrackLine(id, pointsArr);
    }

    @Override
    public JsonElement serialize(TrackLine trackLine, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("name", trackLine.getName());

        JsonArray points = new JsonArray();
        result.add("waypoints", points);
        trackLine.getPoints().forEach(p->{
            points.add(jsonSerializationContext.serialize(p, Waypoint.class));
        });
        JsonArray behaviors = new JsonArray();
        result.add("behaviors", behaviors);
        trackLine.getBehaviors().forEach(b->behaviors.add(jsonSerializationContext.serialize(b, Behavior.class)));
        return result;
    }
}
