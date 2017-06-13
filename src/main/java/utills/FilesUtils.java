package utills;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.JSONConverters.BehaviorConverter;
import models.JSONConverters.LineConverter;
import models.JSONConverters.PointConverter;
import models.TrackLine;
import models.Waypoint;
import models.behavors.Behavior;
import models.repository.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FilesUtils {
    private static FilesUtils instance;
    private Repository repository;
    private Gson gson;

    private Map<Integer, Path> openedTrack = new HashMap<>();

    private FilesUtils(){
        repository = Repository.getInstance();

        //FIXME: хранить в одном месте общий экземпляр
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting()
                .registerTypeAdapter(Waypoint.class, new PointConverter())
                .registerTypeAdapter(TrackLine.class, new LineConverter())
                .registerTypeAdapter(Behavior.class, new BehaviorConverter());

        gson = builder.create();
    }

    public static synchronized FilesUtils getInstance(){
        if (instance==null) instance = new FilesUtils();
        return instance;
    }


    public void addOpenedTrack(TrackLine trackLine, Path path){

    }

    public void saveOpenedTracs(){
        openedTrack.forEach((id, path) -> {
            try {
                Files.write(path, gson.toJson(repository.getTrackById(id)).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void close(){

    }


}
