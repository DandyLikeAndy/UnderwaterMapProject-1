package utills;

import app.Main;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.*;
import java.util.Properties;

public class SettingsProperties {
    private ObjectProperty<String> tileSource = new SimpleObjectProperty<>();
    private ObjectProperty<String> tileCash = new SimpleObjectProperty<>();
    private ObjectProperty<String> tileUrl = new SimpleObjectProperty<>();
    private Properties properties;

    private static SettingsProperties instance;

    private SettingsProperties(){
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            System.out.println( );
            properties = new Properties();
            FileInputStream fis = new FileInputStream(new File("src/main/resources/properties/settings.properties").getAbsolutePath());
            properties.load(fis);
            fis.close();
            tileCash.setValue(properties.getProperty("tile.cash"));
            tileSource.setValue(properties.getProperty("tile.source"));
            tileUrl.setValue(properties.getProperty("tile.url"));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static synchronized SettingsProperties getInstance(){
        if (instance == null) instance = new SettingsProperties();
        return instance;
    }




    public String getTileSource() {
        return tileSource.get();
    }

    public void setTileSource(String tileSource) throws IOException {
        this.tileSource.setValue(tileSource);
        FileOutputStream fos = new FileOutputStream(new File("src/main/resources/properties/settings.properties").getAbsolutePath());
        properties.setProperty("tile.source", tileSource);
        properties.store(fos, null);
        fos.close();
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
}
