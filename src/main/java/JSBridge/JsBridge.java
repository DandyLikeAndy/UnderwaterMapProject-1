package JSBridge;

import models.repository.Repository;
import netscape.javascript.JSObject;

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

    public void setMapUrl(String url){
        window.call("setMapUrl", url);
    }
}
