package utills;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.TrackLine;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class SettingsProperties {
    private ObjectProperty<String> tileSource = new SimpleObjectProperty<>();
    private ObjectProperty<String> tileCash = new SimpleObjectProperty<>();
    private ObjectProperty<String> tileUrl = new SimpleObjectProperty<>();
    private ObjectProperty<MapSource> currentMapSource = new SimpleObjectProperty<>();
    private ObservableList<MapSource> mapSources = FXCollections.observableArrayList();
    private Properties properties;

    private Gson gson;

    private static SettingsProperties instance;

    private SettingsProperties(){
        gson = new Gson();
        InputStreamReader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("properties/mapSources.json"));
        String result = new BufferedReader(reader)
                .lines().collect(Collectors.joining("\n"));
        Type mapSourceListType = new TypeToken<List<MapSource>>() {
        }.getType();
        mapSources.addAll((Collection<? extends MapSource>) gson.fromJson(result, mapSourceListType));
        System.out.println("DONE!");

        try {
            //System.getProperty("java.io.tmpdir")
            properties = new Properties();
            InputStream fis =  getClass().getClassLoader().getResourceAsStream("properties/settings.properties");
            properties.load(fis);
            fis.close();
            tileCash.setValue(properties.getProperty("tile.cash"));
            tileSource.setValue(properties.getProperty("tile.source"));
            tileUrl.setValue(properties.getProperty("tile.url"));
            currentMapSource.setValue(mapSources.filtered(mapSource -> mapSource.getName().equals(properties.getProperty("tile.url"))).get(0));
        } catch (IOException e){
            e.printStackTrace();
        }
        currentMapSource.addListener((observable, oldValue, newValue) -> {
            if (newValue != null){
                properties.setProperty("tile.url", newValue.getName());
            }
        });
    }

    public static synchronized SettingsProperties getInstance(){
        if (instance == null) instance = new SettingsProperties();
        return instance;
    }




    public String getTileSource() {
        return tileSource.get();
    }

    public void setTileSource(String tileSource){
        this.tileSource.setValue(tileSource);
        properties.setProperty("tile.source", tileSource);

    }

    public String getTileCash() {
        return tileCash.get();
    }

    public void setTileCash(String tileCash) {
        this.tileCash.set(tileCash);
        properties.setProperty("tile.cash", tileCash);
    }

    public String getTileUrl() {
        return tileUrl.get();
    }

    public void setTileUrl(String tileUrl) {
        this.tileUrl.set(tileUrl);
        properties.setProperty("tile.url", tileUrl);
    }

    public void saveSettings() throws IOException {

        FileOutputStream fos = new FileOutputStream(new File("src/main/resources/properties/settings.properties").getAbsolutePath());
        properties.store(fos, null);
        fos.close();
    }

    public ObjectProperty<String> tileUrlProperty() {
        return tileUrl;
    }
    public ObjectProperty<String> tileCashProperty() {
        return tileCash;
    }
    public ObjectProperty<String> tileSourceProperty() {
        return tileSource;
    }
    public ObservableList<MapSource> mapSourcesList(){
        return mapSources;
    }

    public MapSource getCurrentMapSource() {
        return currentMapSource.get();
    }

    public ObjectProperty<MapSource> currentMapSourceProperty() {
        return currentMapSource;
    }

    public void setCurrentMapSource(MapSource currentMapSource) {
        this.currentMapSource.set(currentMapSource);
    }

    public void close() throws IOException {
        saveSettings();
    }

}
