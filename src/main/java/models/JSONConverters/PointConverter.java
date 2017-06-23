package models.JSONConverters;

import com.google.gson.*;
import models.PointTask;
import models.Waypoint;
import models.behavors.Behavior;

import java.lang.reflect.Type;

/**
 * Created by User on 17.05.2017.
 */
public class PointConverter implements JsonSerializer<Waypoint>, JsonDeserializer<Waypoint> {
    @Override
    public Waypoint deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        JsonArray tasks = object.getAsJsonArray("tasks");
        double lat = object.get("lat").getAsDouble();
        double lng = object.get("lon").getAsDouble();
        int id = object.get("id").getAsInt();
        int position = object.get("index").getAsInt();
        int radius = object.get("capture_radius").getAsInt();
        Waypoint waypoint = new Waypoint(lat, lng);
        if (object.get("fix_gps")!=null){
            boolean fixGps = object.get("fix_gps").getAsBoolean();
            waypoint.setGpsFix(fixGps);
        }

        if (tasks !=null){
            tasks.forEach(t->{
                waypoint.addTask(jsonDeserializationContext.deserialize(t, PointTask.class));
            });
        }


        waypoint.setId(id);
        waypoint.setPosition(position);
        waypoint.setCapture_radius(radius);
        waypoint.setDistance(object.get("distance").getAsDouble());
        waypoint.setAzimuth(object.get("azimuth").getAsDouble());
        return waypoint;

    }

    @Override
    public JsonElement serialize(Waypoint point, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("lat", point.getLat());
        result.addProperty("lon", point.getLng());
        result.addProperty("azimuth", point.getAzimuth());
        result.addProperty("distance", point.getDistance());
        result.addProperty("depth", point.getDepth());
        result.addProperty("capture_radius", point.getCapture_radius());
        result.addProperty("index", point.getPosition());
        result.addProperty("id", point.getId());
        result.addProperty("fix_gps", point.isGpsFix());

        JsonArray tasks = new JsonArray();
        result.add("tasks", tasks);
        point.getTasks().forEach(t->tasks.add(jsonSerializationContext.serialize(t, PointTask.class)));

        return result;
    }
}
