package app;

import JSBridge.JsBridge;
import Views.BehaviorView;
import Views.TaskView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import models.*;
import models.JSONConverters.BehaviorConverter;
import models.JSONConverters.LineConverter;
import models.JSONConverters.PointConverter;
import models.behavors.Behavior;
import models.repository.Repository;
import netscape.javascript.JSObject;
import utills.HttpDownloadUtility;
import utills.SettingsProperties;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.BinaryOperator;


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
    private ScrollPane tasksPane;
    @FXML
    private AnchorPane behaviorsPane;
    @FXML
    private ListView<PointTask> tasksListView;
    @FXML
    private ListView<Behavior> behaviorListView;
    @FXML
    Label distanceLabel;
    @FXML
    AnchorPane pointProperties;
    @FXML
    AnchorPane properties;
    @FXML
    AnchorPane trackInfo;
    @FXML
    Label trackLengthLabel;
    @FXML
    TextField trackNameField;
    @FXML
    Label pointsCountLabel;
    @FXML
    Label optionsHeader;


    ObservableList<Waypoint> points = FXCollections.observableArrayList();
    ObservableList<TrackLine> lines = FXCollections.observableArrayList();

    WebEngine webEngine;

    JSObject window;

    Gson gson;

    JsBridge jsBridge;

    Repository repository;

    SimpleIntegerProperty zoomProperty = new SimpleIntegerProperty();
    SimpleObjectProperty<String> status = new SimpleObjectProperty<>();

    SettingsProperties settingsProperties = SettingsProperties.getInstance();

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
    public void startRegion() {
        jsBridge.startRegion();
    }

    @FXML
    public void addMarker() {
        jsBridge.addMarker();
    }

    @FXML
    public void addTask() {
        repository.currentPointProperty().get(0).addTask(new PointTask("new"));
    }

    @FXML
    public void deleteTask() {
        repository.currentPointProperty().get(0).deleteTask(tasksListView.getSelectionModel().getSelectedItems().get(0));
    }

    @FXML
    public void addBehavior() {
        repository.currentLineProperty().getValue().addBehavior(new Behavior());
    }

    @FXML
    public void saveTrack() {
        if (repository.currentLineProperty().getValue()==null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Track not selected!");
            alert.setContentText("Select track for save");
            alert.showAndWait();
            return;
        }
        String track = gson.toJson(repository.currentLineProperty().get(), TrackLine.class);
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(properties.getScene().getWindow());
        String fileName = repository.currentLineProperty().get().getName();
        fileName.replace(" ", "_");
        Path dirPath = dir.toPath().resolve(fileName + ".json");
        if (Files.exists(dirPath)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("File "+dirPath+" already exists!");
            alert.setContentText("Specify new track name or folder");
            alert.showAndWait();
            return;
        } else {
            try {
                Path target = Files.createFile(dirPath);
                Files.write(dirPath, track.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(dirPath);
    }

    @FXML
    public void showSettings(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/settings.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadActiveTrack(){
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(properties.getScene().getWindow());
        if (file != null){
            try {
                Files.lines(file.toPath()).reduce((s, s2) -> s+s2).ifPresent(s->{
                    TrackLine trackLine = gson.fromJson(s, TrackLine.class);
                    String coords = gson.toJson(trackLine.getPointsCoords());
                    TrackLine addedTrack = gson.fromJson(jsBridge.addActiveLine(coords), TrackLine.class);
                    trackLine.setId(addedTrack.getId());
                    for (int i = 0; i < addedTrack.getPoints().size(); i++) {
                        trackLine.getPoints().get(i).setId(addedTrack.getPoints().get(i).getId());
                    }
                    repository.addLine(trackLine);
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void initialize() {

        //FIXME: хранить в одном месте общий экземпляр
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting()
                .registerTypeAdapter(Waypoint.class, new PointConverter())
                .registerTypeAdapter(TrackLine.class, new LineConverter())
                .registerTypeAdapter(Behavior.class, new BehaviorConverter());

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

                                initTasksBehaviorsPane();

                                setZoom((int) window.call("getZoom"));
                       /* // all next classes are from org.w3c.dom domain
                        org.w3c.dom.events.EventListener listener = (ev) -> {
                            System.out.println("#" + (org.w3c.dom.Element) ev.getTarget());
                        };

                        org.w3c.dom.Document doc = webEngine.getDocument();
                        org.w3c.dom.Element el = doc.getElementById("mapid");
                        ((org.w3c.dom.events.EventTarget) el).addEventListener("click", listener, false);*/
                                //jsBridge.setPosition();
                            }

                            setMapUrl();
                        }
                );
        settingsProperties.currentMapSourceProperty().addListener((observable, oldValue, newValue) -> {
            setMapUrl();
        });
        settingsProperties.tileSourceProperty().addListener((observable, oldValue, newValue) -> {
            setMapUrl();
        });


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

        repository.currentLineProperty().addListener((observable, oldValue, newValue) -> {
            properties.setDisable(false);
            trackNameField.setText((newValue).getName());
            trackLengthLabel.setText(String.valueOf((newValue).getLength()));
            pointsCountLabel.setText(String.valueOf(newValue.getPoints().size()));
            trackLengthLabel.textProperty().bindBidirectional(newValue.lengthProperty(), new StringConverter<Number>() {
                @Override
                public String toString(Number object) {
                    return String.valueOf(object);
                }

                @Override
                public Number fromString(String string) {
                    return null;
                }
            });
            newValue.getPoints().addListener((ListChangeListener<Waypoint>) c -> {
                c.next();
                pointsCountLabel.setText(String.valueOf(newValue.getPoints().size()));
            });
        });

        repository.currentPointProperty().addListener((ListChangeListener<Waypoint>) c -> {
            properties.setDisable(false);
            c.next();
            if (!c.wasRemoved()) {
                fillPointDescription(c.getList().get(0));
            }
        });

        trackNameField.textProperty().addListener((observable, oldValue, newValue) -> repository.currentLineProperty().getValue().setName(newValue));


    }

    private void initTasksBehaviorsPane() {
        repository.currentPointProperty().addListener((ListChangeListener<Waypoint>) c -> {
            c.next();
            Waypoint waypoint = c.getList().get(0);
            ObservableList<PointTask> tasks = waypoint.getTasks();
            tasksListView.setItems(tasks);
        });

        repository.currentLineProperty().addListener((observable, oldValue, newValue) -> {
            behaviorListView.setItems(newValue.getBehaviors());
        });

        tasksListView.setCellFactory((ListView<PointTask> l) -> new TaskView());
        behaviorListView.setCellFactory((ListView<Behavior> l) -> new BehaviorView());
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

                HBox hBox = new HBox();
                Button showBtn = new Button("");
                FontAwesomeIconView icon1 = new FontAwesomeIconView(FontAwesomeIcon.CLOSE);
                showBtn.setGraphic(icon1);
                hBox.getChildren().add(showBtn);
                if (empty) {
                    setText("");
                    setGraphic(null);
                } else {
                    String name = item.getName();
                    if (item.getClass().getName().equals("models.Waypoint")) {
                        setText(((Waypoint) item).getPosition() + " point");
                        showBtn.setOnAction(event -> {
                            jsBridge.deletePoint(item.getId(), repository.getLines()
                                    .filtered(trackLine -> trackLine.getPoints().stream()
                                            .anyMatch(waypoint -> waypoint.getId() == item.getId()))
                                    .get(0).getId());
                        });
                    } else if (item.getClass().getName().equals("models.TrackLine")) {
                        setText(name);
                        ((TrackLine) item).nameProperty().addListener((observable, oldValue, newValue) -> {
                            treeViewProperty().getValue().refresh();
                        });
                        ToggleButton delBtn = new ToggleButton("");
                        FontAwesomeIconView icon2 = new FontAwesomeIconView(FontAwesomeIcon.EYE);
                        delBtn.setGraphic(icon2);
                        hBox.getChildren().add(delBtn);
                        showBtn.setOnAction(event -> {
                            jsBridge.deleteLine(item.getId());
                        });
                    }

                    /*if (name.equals("root")){
                        name = "Files";
                        //pseudoClassStateChanged(firstElementPseudoClass, true);
                        setDisclosureNode(null);

                    }*/

                    setGraphic(hBox);

                }
            }

        });

        tracksTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            TreeItem<TrackItem> selectedItem = newValue;
            TrackItem item = selectedItem.getValue();
            if (item.getClass().getName().equals("models.Waypoint")) {
                repository.setCurrentPoint((Waypoint) item);
                trackInfo.setVisible(false);
                pointProperties.setVisible(true);
                optionsHeader.setText("Point properties");
            } else if (item.getClass().getName().equals("models.TrackLine")) {
                repository.setCurrentLine((TrackLine) item);
                pointProperties.setVisible(false);
                trackInfo.setVisible(true);
                optionsHeader.setText("Track properties");
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
                        Waypoint removedPoint = c1.getRemoved().get(0);
                        item.getChildren().stream()
                                .filter(trackItemTreeItem -> trackItemTreeItem.getValue().equals(removedPoint))
                                .findFirst().ifPresent(trackItemTreeItem -> {
                            item.getChildren().remove(trackItemTreeItem);
                        });
                        item.getChildren().sort(this::comparator);
                    }
                });
            } else if(c.wasRemoved()){
                TrackLine line = (TrackLine) c.getRemoved().get(0);
                TreeItem<TrackItem> item = tracksTreeView.getRoot().getChildren().stream().filter(i-> i.getValue().equals(line)).findFirst().get();
                tracksTreeView.getRoot().getChildren().remove(item);
            }

            /*if (c.wasUpdated()){
                TrackItem updatedTrack = c.getList().get(0);
                System.out.println();
            }*/

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

    private void setMapUrl(){
        String url;
        String subdomains;
        if (settingsProperties.getTileSource().equals("web")){
            url = settingsProperties.getCurrentMapSource().getUrl();
            subdomains = settingsProperties.getCurrentMapSource().getSubdomains();
        } else {
            url = "file:///"+settingsProperties.getTileCash()+"/"+settingsProperties.getCurrentMapSource().getName()+"/{z}/{x}/{y}.png";
            subdomains = "";
        }
        jsBridge.setMapUrl(url, subdomains);
    }


    public void log(String value) {
        System.out.println("From web: " + value);
    }

    public void addPoint(String point, String trackId) {
        Waypoint waypoint = gson.fromJson(point, Waypoint.class);

        repository.getLines().stream().filter(l -> l.getId() == Integer.parseInt(trackId))
                .findFirst().ifPresent(t -> t.addPoint(waypoint));
    }

    public void addLineFromWeb(String line) {
        System.out.println(line);
        TrackLine trackLine = gson.fromJson(line, TrackLine.class);
        repository.addLine(trackLine);
    }

    public void deletePoint(int id, int lineId) {
        repository.deletePoint(id, lineId);
    }

    public void deleteLine(int id){
        repository.deleteLine(id);
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
        //System.out.println(track);
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
