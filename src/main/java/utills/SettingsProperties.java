package utills;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.TrackLine;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

public class SettingsProperties {

    static final Logger logger = LogManager.getLogger(SettingsProperties.class);
    private final String tempFolderName = "gliderApp";

    private ObjectProperty<String> tileSource = new SimpleObjectProperty<>();
    private ObjectProperty<String> tileCash = new SimpleObjectProperty<>();
    private ObjectProperty<String> tileUrl = new SimpleObjectProperty<>();
    private ObjectProperty<MapSource> currentMapSource = new SimpleObjectProperty<>();
    private ObservableList<MapSource> mapSources = FXCollections.observableArrayList();
    private Properties properties;

    private Gson gson;

    private static SettingsProperties instance;

    private SettingsProperties() throws IOException {
        logger.debug("temp user path: "+ System.getProperty("java.io.tmpdir"));

        gson = new Gson();
        InputStreamReader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("properties/mapSources.json"));
        String result = new BufferedReader(reader)
                .lines().collect(Collectors.joining("\n"));
        reader.close();
        Type mapSourceListType = new TypeToken<List<MapSource>>() {
        }.getType();
        mapSources.addAll((Collection<? extends MapSource>) gson.fromJson(result, mapSourceListType));
        properties = new Properties();

        try {
            loadProperties();
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
        if (instance == null) try {
            instance = new SettingsProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        FileOutputStream fos = new FileOutputStream(getTempPath().toFile());
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

    private boolean checkTempDir(){
        if (Files.exists(getTempPath())){
            return true;
        } else {
            logger.debug("settings file is not exist");
            return false;
        }

    }
    private boolean checkVersion() throws IOException {
        InputStream sourceFis =  getClass().getClassLoader().getResourceAsStream("properties/settings.properties");
        InputStream targetFis = Files.newInputStream(getTempPath());
        Properties sourceProp = new Properties();
        Properties targetProp = new Properties();
        sourceProp.load(sourceFis);
        targetProp.load(targetFis);
        if (sourceProp.getProperty("settings.version").equals(targetProp.getProperty("settings.version"))) {
            logger.info("settings version is equals");
            sourceFis.close();
            targetFis.close();
            return true;
        }
        logger.info("settings version is not equals");
        sourceFis.close();
        targetFis.close();
        return false;
    }

    private Path getTempPath(){
        String tempDir = System.getProperty("java.io.tmpdir");
        return Paths.get(tempDir).resolve(tempFolderName).resolve("settings.properties");
    }

    private Path createTempSettingsFile() throws IOException {
        Path dir = Paths.get(System.getProperty("java.io.tmpdir")).resolve(tempFolderName);
        if (!Files.exists(dir)) {
            Files.createDirectory(dir);
        }
        if (Files.exists(getTempPath())) return getTempPath();
        return Files.createFile(getTempPath());
    }

    private void loadProperties() throws IOException {

        if (!checkTempDir() || !checkVersion()) {
           copyProperties();
        }
        InputStream fis = Files.newInputStream(getTempPath());
        properties.load(fis);
        fis.close();

    }

    private void copyProperties() throws IOException {
        System.out.println("");
        InputStream fis =  getClass().getClassLoader().getResourceAsStream("properties/settings.properties");
        createTempSettingsFile();
        Files.copy(fis, getTempPath(), StandardCopyOption.REPLACE_EXISTING);
        fis.close();
    }

}
