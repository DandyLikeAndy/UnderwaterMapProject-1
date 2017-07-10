package controllers;

import JSBridge.JsBridge;
import Views.BehaviorView;
import Views.TaskView;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;


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
    @FXML
    Button addMarkerBtn;
    @FXML
    MenuButton addTrackBtn;
    @FXML
    CheckBox fixGpsCheck;
    @FXML
    ProgressBar downloadProgressBar;


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
    public void addMarkerByClick(ActionEvent e) {

        jsBridge.startMarker();
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
        if (repository.currentLineProperty().getValue() == null) {
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
            alert.setHeaderText("File " + dirPath + " already exists!");
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
    public void showSettings() {
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
    public void loadActiveTrack() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(properties.getScene().getWindow());
        if (file != null) {
            try {
                Files.lines(file.toPath()).reduce((s, s2) -> s + s2).ifPresent(s -> {
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

    @FXML
    public void loadTrackFromGeoJson() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(properties.getScene().getWindow());
        System.out.println("read file "+file);
        if (file!=null){
            StringBuilder stringBuilder = new StringBuilder();
            try {
                System.out.println("create string");
                Files.lines(file.toPath()).forEach(stringBuilder::append);
            } catch (IOException e) {
                e.printStackTrace();
            }
            jsBridge.addTrackFromGeoJson(stringBuilder.toString());
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

                                setAddMarkerContextMenu();


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


        //addTrackBtn.setTooltip(new Tooltip("Settings"));

    }

    private void setAddMarkerContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem addMarkerByCoordsMenu = new MenuItem("Add Marker By Coords");
        MenuItem addMarkerByClickMenu = new MenuItem("Add Marker By Click");

        addMarkerByClickMenu.setOnAction(this::addMarkerByClick);
        addMarkerByCoordsMenu.setOnAction(this::addMarkerByCoords);

        contextMenu.getItems().addAll(addMarkerByClickMenu, addMarkerByCoordsMenu);
        addMarkerBtn.setContextMenu(contextMenu);
    }

    private void addMarkerByCoords(ActionEvent actionEvent) {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Add Marker By Coords");
        dialog.setHeaderText("Add New Marker By Coordinates");

// Set the icon (must be included in the project).
        dialog.setGraphic(null);

// Set the button types.
        ButtonType loginButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField lat = new TextField();
        lat.setPromptText("Lat");
        TextField lon = new TextField();
        lon.setPromptText("Lon");
        TextField name = new TextField();
        name.setPromptText("Name");
        name.setText("Custom marker");

        grid.add(new Label("Latitude:"), 0, 0);
        grid.add(lat, 1, 0);
        grid.add(new Label("Longitude:"), 0, 1);
        grid.add(lon, 1, 1);
        grid.add(new Label("Name:"), 0, 2);
        grid.add(name, 1, 2);

// Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        lat.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
        Platform.runLater(() -> lat.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                String[] s = {lat.getText(), lon.getText(), name.getText()};
                return s;
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();

        result.ifPresent(latLons -> {
            jsBridge.addMarker(latLons[0], latLons[1], latLons[2]);
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

        fixGpsCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (repository.currentPointProperty().get(0).isGpsFix() != newValue) {
                repository.currentPointProperty().get(0).setGpsFix(newValue);
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
                    } else if (item.getClass().getName().equals("models.Marker")) {
                        setText(name);
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
            } else if (c.wasRemoved()) {
                TrackLine line = (TrackLine) c.getRemoved().get(0);
                TreeItem<TrackItem> item = tracksTreeView.getRoot().getChildren().stream().filter(i -> i.getValue().equals(line)).findFirst().get();
                tracksTreeView.getRoot().getChildren().remove(item);
            }

            /*if (c.wasUpdated()){
                TrackItem updatedTrack = c.getList().get(0);
                System.out.println();
            }*/

        });

        ///add markers tree item

        Marker markers = new Marker();
        markers.setName("Markers");
        TreeItem<TrackItem> markersItem = new TreeItem<>(markers);
        //

        repository.getMarkers().addListener((ListChangeListener<Marker>) c -> {
            c.next();
            if (c.getList().size() > 0) {
                if (!root.getChildren().contains(markersItem)) {
                    root.getChildren().add(markersItem);
                }
                Marker addedMarker = c.getAddedSubList().get(0);
                markersItem.getChildren().add(new TreeItem<>(addedMarker));
            } else if (c.getList().isEmpty()) {
                root.getChildren().remove(markersItem);
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
        fixGpsCheck.setSelected(point.isGpsFix());

    }

    private void setMapUrl() {
        String url;
        String subdomains;
        if (settingsProperties.getTileSource().equals("web")) {
            url = settingsProperties.getCurrentMapSource().getUrl();
            subdomains = settingsProperties.getCurrentMapSource().getSubdomains();
        } else {
            url = "file:///" + settingsProperties.getTileCash() + "/" + settingsProperties.getCurrentMapSource().getName() + "/{z}/{x}/{y}.png";
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

    public void deleteLine(int id) {
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
        int tilesLength = tilesImgs.length;
        double progressStep = 1.0/tilesLength;
        final double[] progress = {0};
        System.out.println("Progress step: "+progressStep);
        System.out.println("Progress length: "+tilesLength);
        downloadProgressBar.setVisible(true);
        downloadProgressBar.setProgress(0);

        HttpDownloadUtility.addCallbacks(msg -> {
            Platform.runLater(() -> {
                setStatus(msg);
                progress[0] += progressStep;
                downloadProgressBar.setProgress(progress[0]);
            });
        });
        HttpDownloadUtility.setOnFinishedDownload(msg->{
            Platform.runLater(()->{
                downloadProgressBar.setVisible(false);
                setStatus(msg);
            });
        });
        HttpDownloadUtility.loadTiles(Arrays.asList(tilesImgs));
    }

    public void addCustomMarker(String marker) {
        System.out.println("new marker " + marker);
        repository.addMarker(gson.fromJson(marker, Marker.class));
    }


}
