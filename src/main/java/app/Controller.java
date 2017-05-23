package app;

import JSBridge.JsBridge;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.StringConverter;
import models.*;
import models.JSONConverters.LineConverter;
import models.JSONConverters.PointConverter;
import models.repository.Repository;
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
    @FXML
    private Label azumuthLabel;
    @FXML
    private Label indexLabel;
    @FXML
    private Label latLabel;
    @FXML
    private Label lngLabel;
    @FXML
    private Label depthLabel;
    @FXML
    private Label pointDistanceLabel;
    @FXML
    private Label radiusLabel;
    @FXML
    private AnchorPane tasksPane;
    @FXML
    private AnchorPane behaviorsPane;

    private TreeView<String> tasksTree;
    private TreeView<String> behaviorsTree;


    ObservableList<Waypoint> points = FXCollections.observableArrayList();
    ObservableList<TrackLine> lines = FXCollections.observableArrayList();

    WebEngine webEngine;

    JSObject window;

    Gson gson;

    JsBridge jsBridge;

    Repository repository;

    SimpleIntegerProperty zoomProperty = new SimpleIntegerProperty();
    SimpleObjectProperty<String> status = new SimpleObjectProperty<>();

    @FXML
    public void zoomMinus() {
        jsBridge.zoomMinus();
    }

    @FXML
    public void zoomPlus() {
        jsBridge.zoomPlus();
    }

    @FXML
    public void openMenu() {

    }

    @FXML
    public void getTilesFromWeb() {
        jsBridge.getTiles();
    }

    @FXML
    public void startTrack() {
        jsBridge.startTrac();
    }

    @FXML
    Label distanceLabel;

    @FXML
    public void startRegion() {
        jsBridge.startRegion();
    }

    @FXML
    public void addMarker() {
        jsBridge.addMarker();
    }

    public void initialize() {

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Waypoint.class, new PointConverter());
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
                                repository = Repository.getInstance();
                                jsBridge = new JsBridge(window, repository);
                                jsBridge.initJavaController();

                                jsBridge.resizeMap(webView.getWidth(), webView.getHeight());

                                setHandlers();

                                initTreeView();

                                //initTasksBehaviorsPane();

                                setZoom((int) window.call("getZoom"));
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
            jsBridge.resizeMap(webView.getWidth(), webView.getHeight());
        });

        webView.heightProperty().addListener((observable, oldValue, newValue) -> {
            jsBridge.resizeMap(webView.getWidth(), webView.getHeight());
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

    private void initTasksBehaviorsPane(){
        tasksTree = new TreeView<>();
        TreeItem<String> root = new TreeItem<>();
        tasksTree.setRoot(root);
        tasksPane.getChildren().add(tasksTree);

        repository.currentPointProperty().addListener((ListChangeListener<Waypoint>) c -> {
            c.next();
            Waypoint point = c.getList().get(0);
            tasksTree.getRoot().getChildren().clear();
            point.getTasks().forEach(t->{
                tasksTree.getRoot().getChildren().add(new TreeItem<>(t.getName()));
            });
        });
    }

    private void initTreeView() {

        TreeItem<TrackItem> root = new TreeItem<>(new TrackLine());
        tracksTreeView.setRoot(root);
        tracksTreeView.setShowRoot(false);

        tracksTreeView.setCellFactory(param -> new TreeCell<TrackItem>() {
            @Override
            public void updateItem(TrackItem item, boolean empty) {
                super.updateItem(item, empty);
                //setDisclosureNode(null);

                if (empty) {
                    setText("");
                    setGraphic(null);
                } else {
                    String name = item.getName();
                    if (item.getClass().getName().equals("models.Waypoint")) {
                        setText(((Waypoint) item).getPosition() + " point");
                    } else {
                        setText(name);
                    }
                    /*if (name.equals("root")){
                        name = "Files";
                        //pseudoClassStateChanged(firstElementPseudoClass, true);
                        setDisclosureNode(null);

                    }*/


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

        tracksTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            TreeItem<TrackItem> selectedItem = (TreeItem<TrackItem>) newValue;
            TrackItem item = selectedItem.getValue();
            if (item.getClass().getName().equals("models.Waypoint")) {
                repository.setCurrentPoint((Waypoint) item);
            }
        });

        repository.getLines().addListener((ListChangeListener<TrackItem>) c -> {
            c.next();
            if (c.wasAdded()) {
                TrackItem addedTrack = c.getAddedSubList().get(0);
                TreeItem<TrackItem> item = new TreeItem<>(addedTrack);

                ((TrackLine) addedTrack).getPoints().forEach(p -> {
                    TreeItem<TrackItem> point = new TreeItem<>(p);
                    item.getChildren().add(point);
                });

                root.getChildren().add(item);

                ((TrackLine) addedTrack).getPoints().addListener((ListChangeListener<Waypoint>) c1 -> {
                    c1.next();
                    if (c1.wasAdded()) {
                        Waypoint addedPoint = c1.getAddedSubList().get(0);
                        TreeItem<TrackItem> point = new TreeItem<>(addedPoint);
                        item.getChildren().add(point);
                        item.getChildren().sort(this::comparator);
                    } else if (c1.wasRemoved()) {
                        System.out.println("removed");
                        Waypoint removedPoint = c1.getRemoved().get(0);
                        item.getChildren().stream()
                                .filter(trackItemTreeItem -> trackItemTreeItem.getValue().equals(removedPoint))
                                .findFirst().ifPresent(trackItemTreeItem -> {
                            item.getChildren().remove(trackItemTreeItem);
                        });
                        item.getChildren().sort(this::comparator);
                    }
                });
            }

            /*if (c.wasUpdated()){
                TrackItem updatedTrack = c.getList().get(0);
                System.out.println();
            }*/

        });

        repository.currentPointProperty().addListener((ListChangeListener<Waypoint>) c -> {
            c.next();
            if (!c.wasRemoved()) {
                fillPointDescription(c.getList().get(0));
            }
        });
    }

    private int comparator(TreeItem<TrackItem> o1, TreeItem<TrackItem> o2) {

        if (((Waypoint) o1.getValue()).getPosition() > ((Waypoint) o2.getValue()).getPosition()) {
            return 1;
        }
        if (((Waypoint) o1.getValue()).getPosition() < ((Waypoint) o2.getValue()).getPosition()) {
            return -1;
        }
        return 0;
    }

    private void fillPointDescription(Waypoint point) {
        indexLabel.setText(String.valueOf(point.getPosition()));
        latLabel.setText(String.valueOf(point.getLat()));
        lngLabel.setText(String.valueOf(point.getLng()));
        depthLabel.setText(String.valueOf(point.getDepth()));
        radiusLabel.setText(String.valueOf(point.getCapture_radius()));
        pointDistanceLabel.setText(String.valueOf(point.getDistance()));
        azumuthLabel.setText(String.valueOf(point.getAzimuth()));
    }


    public void log(String value) {
        System.out.println("From web: " + value);
    }

    public void addPoint(String point, String trackId) {
        Waypoint waypoint = gson.fromJson(point, Waypoint.class);
        //////
        waypoint.addTask(new PointTask("jkll"));
        waypoint.addTask(new PointTask("asss"));
        /////////

        repository.getLines().stream().filter(l -> l.getId() == Integer.parseInt(trackId))
                .findFirst().ifPresent(t -> t.addPoint(waypoint));
    }

    public void addLineFromWeb(String line) {
        //System.out.println(line);
        TrackLine trackLine = gson.fromJson(line, TrackLine.class);
        repository.addLine(trackLine);
    }

    public void deletePoint(int id, int lineId) {
        System.out.println("delete");
        repository.deletePoint(id, lineId);
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public void setMouseCoordsFromWeb(String coords) {
        mouseCoords.setText(coords);
    }

    public void setDistanceFromWeb(String distance) {
        distanceLabel.setText(distance);
    }

    public void setZoom(int zoom) {
        zoomProperty.set(zoom);
    }

    public void updateTrack(String track) {
        System.out.println(track);
        TrackLine trackLine = gson.fromJson(track, TrackLine.class);
        repository.getLines().stream().filter(t -> trackLine.getId() == t.getId()).findFirst().ifPresent(t -> {
            repository.updateTrack(t, trackLine);
        });
    }


    public void updatePoint(String point, int lineId) {
        Waypoint waypoint = gson.fromJson(point, Waypoint.class);

        TrackLine line = repository.getLines().filtered(line1 -> line1.getId() == lineId).get(0);
        line.updatePoint(waypoint);
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
