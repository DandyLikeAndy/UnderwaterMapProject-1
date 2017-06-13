package JSBridge;

import com.google.gson.reflect.TypeToken;
import models.repository.Repository;
import netscape.javascript.JSObject;

import java.lang.reflect.Type;

/**
 * Created by Anton on 22.05.2017.
 */
public class JsBridge {
    Repository repository;
    JSObject window;

    public JsBridge(JSObject window, Repository repository){
        this.window = window;
        this.repository = repository;
    }
    public void resizeMap(double width, double height) {
        window.call("resizeMap", width, height);
    }
    public void selectLineToWeb(int id) {
        window.call("selectLine", id);
    }


    public void zoomMinus() {
        window.call("zoomMinus");
    }


    public void zoomPlus() {
        window.call("zoomPlus");
    }


    public void getTiles() {
        window.call("getTilesImg");
    }

    public void startTrac() {
        window.call("startTrack");
    }

    public void startRegion() {
        window.call("startRegion");
    }

    public void addMarker() {
        window.call("addMarker");
    }

    public void initJavaController() {
        window.call("initJavaController");
    }

    public void deletePoint(int pointId, int layerId){
        window.call("deletePoint", pointId, layerId);
    }


    public void deleteLine(int id) {
        window.call("deleteLine", id);
    }

    public void setMapUrl(String url, String subdomains){
        window.call("setMapUrl", url, subdomains);
    }

    public void setPosition(){
        window.call("setPosition");
    }

    public String addActiveLine(String coords){
        //System.out.println(coords);
        return window.call("addActiveLine", coords).toString();
    }

    public void addMarker(String lat, String lon){
        window.call("addMarker", Double.valueOf(lat), Double.valueOf(lon));
    }
}
