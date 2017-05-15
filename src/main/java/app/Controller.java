package app;

import com.google.gson.Gson;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import models.TrackLine;
import models.TrackPoint;
import netscape.javascript.JSObject;

import java.util.stream.Collectors;

public class Controller {

    @FXML
    WebView webView;

    @FXML
    Slider zoomSlider;

    @FXML
    Label zoomLabel;

    @FXML
    ListView<TrackPoint> pointList;

    @FXML
    ListView<TrackLine> linesList;

    ObservableList<TrackPoint> points = FXCollections.observableArrayList();
    ObservableList<TrackLine> lines = FXCollections.observableArrayList();

    WebEngine webEngine;

    IntegerProperty zoom = new SimpleIntegerProperty();

    JSObject window;

    Gson gson = new Gson();


    @FXML
    public void sendBtnAction() {
        sendToWeb();
    }

    @FXML
    public void getTilesFromWeb(){
        window.call("getTilesImg");
    }

    public void initialize() {

        pointList.setItems(points);
        linesList.setItems(lines);

        linesList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                TrackLine selectedLine = linesList.getSelectionModel().getSelectedItem();
                int id = selectedLine.getId();
                selectLineToWeb(id);
                points.clear();
                points.addAll(selectedLine.getPoints());
            }
        });


        webView.setZoom(1);
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(getClass().getResource("/html/start.html").toExternalForm());

/*
        zoomLabel.textProperty().bindBidirectional(zoom, new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return String.valueOf(object);
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });*/

        /*zoom.bindBidirectional(zoomSlider.valueProperty());

        zoom.addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setZoomToMap(newValue.intValue());
            }
        });
*/
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

    private void setHandlers(){
        webView.widthProperty().addListener((observable, oldValue, newValue) -> {
            resizeMap();
        });
    }


    //jsObject.call("setMap", sExtend);
   /* private void setZoomToMap(int zoom) {
        webEngine.executeScript("setZoom(" + String.valueOf(zoom) + ")");
    }*/


    /*public void setZoom(int zoom) {
        this.zoom.setValue(zoom);
    }*/

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

    public void clickPoint(int id) {
        points.stream().filter(p -> p.getId() == id).findFirst().ifPresent(p -> {
            pointList.getSelectionModel().select(p);
        });
    }

    private void selectLineToWeb(int id) {
        window.call("selectLine", id);
    }

    private void resizeMap() {
        window.call("resizeMap", webView.getWidth(), webView.getHeight());
    }

    public void getTilesImgFromWeb(String imgs){
        String[] tilesImgs = imgs.split(",");
        for (String i :
                tilesImgs) {
            System.out.println("img = "+i);
        }
    }


}
