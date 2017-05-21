package app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.StringConverter;
import models.*;
import models.JSONConverters.LineConverter;
import models.JSONConverters.PointConverter;
import netscape.javascript.JSObject;
import utills.HttpDownloadUtility;

import java.io.IOException;
import java.util.Arrays;
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

    ObservableList<Waipoint> points = FXCollections.observableArrayList();
    ObservableList<TrackLine> lines = FXCollections.observableArrayList();

    WebEngine webEngine;

    JSObject window;

    Gson gson;

    SimpleIntegerProperty zoomProperty = new SimpleIntegerProperty();
    SimpleObjectProperty<String> status = new SimpleObjectProperty<>();

    @FXML
    public void zoomMinus() {
        window.call("zoomMinus");
    }

    @FXML
    public void zoomPlus() {
        window.call("zoomPlus");
    }

    @FXML
    public void openMenu() {

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

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Waipoint.class, new PointConverter());
        builder.registerTypeAdapter(TrackLine.class, new LineConverter());
        gson = builder.create();

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
                                window.call("initJavaController");

                                resizeMap();

                                setHandlers();

                                initTreeView();

                                setZoom((int)window.call("getZoom"));
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

        tracksTreeView.setCellFactory(param -> new TreeCell<TrackItem>(){
            @Override
            public void updateItem(TrackItem item, boolean empty) {
                super.updateItem(item, empty);
                //setDisclosureNode(null);

                if (empty) {
                    setText("");
                    setGraphic(null);
                } else {
                    String name = item.getName();
                    int id = item.getId();
                    /*if (name.equals("root")){
                        name = "Files";
                        //pseudoClassStateChanged(firstElementPseudoClass, true);
                        setDisclosureNode(null);

                    }*/
                    setText("id: "+id+" "+name);


                    ToggleButton showBtn = new ToggleButton("");
                    FontAwesomeIconView icon1 = new FontAwesomeIconView(FontAwesomeIcon.EYE);
                    showBtn.setGraphic(icon1);
                    Button delBtn = new Button("");
                    FontAwesomeIconView icon2 = new FontAwesomeIconView(FontAwesomeIcon.TIMES);
                    delBtn.setGraphic(icon2);
                    HBox hBox = new HBox(showBtn, delBtn);

                    setGraphic(hBox);

                }
            }
        });

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
        Waipoint waipoint = gson.fromJson(point, Waipoint.class);
        points.add(waipoint);
    }

    public void addLineFromWeb(String line) {
        System.out.println(line);
        TrackLine trackLine = gson.fromJson(line, TrackLine.class);
        lines.add(trackLine);
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

    public void setZoom(int zoom){
        zoomProperty.set(zoom);
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

        HttpDownloadUtility.addCallbacks(msg -> Platform.runLater(() -> setStatus(msg)));
        HttpDownloadUtility.loadTiles(Arrays.asList(tilesImgs));
    }


}
