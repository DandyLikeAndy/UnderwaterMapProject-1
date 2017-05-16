package app;

import com.google.gson.Gson;
import com.sun.deploy.net.HttpUtils;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import models.TrackItem;
import models.TrackLine;
import models.TrackPoint;
import netscape.javascript.JSObject;
import utills.HttpDownloadUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {

    @FXML
    WebView webView;

    @FXML
    Label zoomLabel;

    @FXML
    Label statusLabel;

    @FXML
    TreeView<TrackItem> tracksTreeView;

    @FXML
    Label mouseCoords;

    ObservableList<TrackPoint> points = FXCollections.observableArrayList();
    ObservableList<TrackLine> lines = FXCollections.observableArrayList();

    WebEngine webEngine;

    JSObject window;

    Gson gson = new Gson();

    SimpleIntegerProperty zoomProperty = new SimpleIntegerProperty();
    SimpleObjectProperty<String> status = new SimpleObjectProperty<>();

    @FXML
    public void zoomMinus() {
        int val = (int) window.call("zoomMinus");
        zoomProperty.set(val);
    }

    @FXML
    public void zoomPlus() {
        int val = (int) window.call("zoomPlus");
        zoomProperty.set(val);
    }

    @FXML
    public void openMenu() {

    }

    @FXML
    public void sendBtnAction() {
        sendToWeb();
    }

    @FXML
    public void getTilesFromWeb() {
        window.call("getTilesImg");
    }

    @FXML
    public void startTrack() {
        window.call("startTrack");
    }

    @FXML
    Label distanceLabel;

    @FXML
    public void startRegion() {
        window.call("startRegion");
    }

    @FXML
    public void addMarker() {
        window.call("addMarker");
    }

    public void initialize() {


        webView.setZoom(1);
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(getClass().getResource("/html/start.html").toExternalForm());


        final Controller controller = this;

        webEngine.getLoadWorker()
                .stateProperty()
                .addListener((ov, oldState, newState) -> {
                            if (newState == Worker.State.SUCCEEDED) {
                                System.out.println("Change");
                                window = (JSObject) webEngine.executeScript("window");
                                window.setMember("javaController", controller);

                                resizeMap();

                                setHandlers();

                                initTreeView();
                       /* // all next classes are from org.w3c.dom domain
                        org.w3c.dom.events.EventListener listener = (ev) -> {
                            System.out.println("#" + (org.w3c.dom.Element) ev.getTarget());
                        };

                        org.w3c.dom.Document doc = webEngine.getDocument();
                        org.w3c.dom.Element el = doc.getElementById("mapid");
                        ((org.w3c.dom.events.EventTarget) el).addEventListener("click", listener, false);*/

                            }
                        }
                );


    }

    private void setHandlers() {
        webView.widthProperty().addListener((observable, oldValue, newValue) -> {
            resizeMap();
        });

        webView.heightProperty().addListener((observable, oldValue, newValue) -> {
            resizeMap();
        });

        zoomLabel.textProperty().bindBidirectional(zoomProperty, new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return String.valueOf(object);
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });

        statusLabel.textProperty().bindBidirectional(status);

    }

    private void initTreeView() {

        TreeItem<TrackItem> root = new TreeItem<>(new TrackLine());
        tracksTreeView.setRoot(root);
        tracksTreeView.setShowRoot(false);

        /*tracksTreeView.setCellFactory(new Callback<TreeView<TrackItem>, TreeCell<TrackItem>>() {
            @Override
            public TreeCell<TrackItem> call(TreeView<TrackItem> param) {
                return null;
            }
        });
*/
        lines.addListener((ListChangeListener<TrackItem>) c -> {
            c.next();
            TrackItem addedTrack = c.getAddedSubList().get(0);
            TreeItem<TrackItem> item = new TreeItem<>(addedTrack);

            ((TrackLine) addedTrack).getPoints().forEach(p -> {
                TreeItem<TrackItem> point = new TreeItem<>(p);
                item.getChildren().add(point);
            });

            root.getChildren().add(item);
        });


    }


    public void log(String value) {
        System.out.println("From web: " + value);
    }

    public void addPoint(String point) {
        TrackPoint trackPoint = gson.fromJson(point, TrackPoint.class);
        points.add(trackPoint);
    }

    public void addLine(String line) {
        TrackLine trackLine = gson.fromJson(line, TrackLine.class);
        lines.add(gson.fromJson(line, TrackLine.class));
    }

    public void deletePoint(int id) {
        points.removeAll(points.stream().filter(p -> p.getId() == id).collect(Collectors.toList()));
    }

    private void sendToWeb() {
        //window.call("getFromJava", gson.toJson(new User("GHJ", 2)));
    }


    public void setStatus(String status) {
        this.status.set(status);
    }

    public void setMouseCoordsFromWeb(String coords){
        mouseCoords.setText(coords);
    }

    public void setDistanceFromWeb(String distance){
        distanceLabel.setText(distance);
    }

    private void selectLineToWeb(int id) {
        window.call("selectLine", id);
    }

    private void resizeMap() {
        window.call("resizeMap", webView.getWidth(), webView.getHeight());
    }


    public void getTilesImgFromWeb(String imgs) throws IOException {
        String[] tilesImgs = imgs.split(",");
        for (String i :
                tilesImgs) {
            System.out.println("img = " + i);
        }

        HttpDownloadUtility.addCallbacks(new HttpDownloadUtility.Callback() {
            @Override
            public void call(String msg) {
                setStatus(msg);
            }
        });
        HttpDownloadUtility.loadTiles(Arrays.asList(tilesImgs));
    }


}