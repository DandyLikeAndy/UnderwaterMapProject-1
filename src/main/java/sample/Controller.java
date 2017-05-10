package sample;

import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.StringConverter;
import netscape.javascript.JSObject;
import webControllers.SampleController;
import webControllers.WebController;

public class Controller {

    @FXML
    WebView webView;

    @FXML
    Slider zoomSlider;

    @FXML
    Label zoomLabel;

    @FXML
    ListView<String> pointList;

    ObservableList<String> points = FXCollections.observableArrayList();

    WebEngine webEngine;

    IntegerProperty zoom = new SimpleIntegerProperty();

    public void initialize() {

        pointList.setItems(points);

        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(getClass().getResource("/leaflet.html").toExternalForm());

        zoomLabel.textProperty().bindBidirectional(zoom, new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return String.valueOf(object);
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });

        zoom.bindBidirectional(zoomSlider.valueProperty());

        zoom.addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setZoomToMap(newValue.intValue());
            }
        });

        final Controller controller = this;

        webEngine.getLoadWorker()
                .stateProperty()
                .addListener(new ChangeListener<Worker.State>() {
                                 public void changed(ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) {
                                     if (newState == Worker.State.SUCCEEDED) {
                                         System.out.println("Change");
                                         JSObject window = (JSObject) webEngine.executeScript("window");
                                         window.setMember("clickController", new SampleController());
                                         window.setMember("javaController", controller);
                                     }
                                 }
                             }
                );
    }


    private void setZoomToMap(int zoom) {
        webEngine.executeScript("setZoom(" + String.valueOf(zoom) + ")");
    }


    public void setZoom(int zoom) {
        this.zoom.setValue(zoom);
    }

    public void log(String value){
        System.out.println("From web: "+value);
    }

    public void addPoint(String point){
        points.add(point);
    }
}
