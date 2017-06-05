package utills;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class SettingsProperties {
    private ObjectProperty<String> tileSource = new SimpleObjectProperty<>();
    private ObjectProperty<String> tileCash = new SimpleObjectProperty<>();
    private ObjectProperty<String> tileUrl = new SimpleObjectProperty<>();
    private ObjectProperty<MapSource> currentMapSource = new SimpleObjectProperty<>();
    private ObservableList<MapSource> mapSources = FXCollections.observableArrayList();
    private Type mapSourceListType = new TypeToken<List<MapSource>>() {}.getType();
    private Properties properties;

    private Gson gson;

    private static SettingsProperties instance;

    private SettingsProperties(){
        gson = new Gson();
        try {
            Path path = Paths.get("src/main/resources/properties/mapSources.json");
            String mapSourcesString = Files.lines(path).reduce((s, s2) -> s+s2).get();
            mapSources.addAll((Collection<? extends MapSource>) gson.fromJson(mapSourcesString, mapSourceListType));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            properties = new Properties();
            FileInputStream fis = new FileInputStream(new File("src/main/resources/properties/settings.properties").getAbsolutePath());
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
}
