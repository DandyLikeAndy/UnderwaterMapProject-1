package controllers;

import db.DBRecord;
import db.DBRecordType;
import db.DBService;
import db.DBSession;
import geoJson.GeoJsonParser;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Pair;
import javafx.util.StringConverter;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import utills.CSVUtills;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class LoadDbController {
    @FXML
    private TreeView<DBSession> sessionTree;

    @FXML
    private CheckBox redWaveCheck;

    @FXML
    private CheckBox innerGpsCheck;

    @FXML
    private CheckBox inertialCheck;

    @FXML
    private TextField redWaveName;

    @FXML
    private TextField innerGpsName;

    @FXML
    private TextField inertialName;

    @FXML
    private ColorPicker redWaveColor;

    @FXML
    private ColorPicker innerGpsColor;

    @FXML
    private ColorPicker inertialColor;

    @FXML
    private Label dateLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label recordsLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private GridView<DBRecordType> gridView;


    private ObjectProperty<Path> dbPathProperty = new SimpleObjectProperty<>();
    private DBService dbService;
    private ArrayList<DBTypesElement> dBValues;

    @FXML
    private void loadDb(){
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(sessionTree.getScene().getWindow());

        if (file==null) return;

        dbPathProperty.setValue(file.toPath());

    }

    @FXML
    private void saveToFile(){
        DBSession session = sessionTree.getSelectionModel().getSelectedItems().get(0).getValue();
        dbService.uploadRecords(session);
        ArrayList<DBRecord> records = session.getRecords();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(redWaveCheck.getScene().getWindow());

        if (dir!=null){
            if (redWaveCheck.isSelected()) saveTrack(records, DBRecordType.GPS_REDWAVE, redWaveName.getText(), toRGBCode(redWaveColor.getValue()), dir.toPath());
            if (inertialCheck.isSelected()) saveTrack(records, DBRecordType.TRACK, inertialName.getText(), toRGBCode(inertialColor.getValue()), dir.toPath());
            if (innerGpsCheck.isSelected()) saveTrack(records, DBRecordType.GPS_BOARD, innerGpsName.getText(), toRGBCode(innerGpsColor.getValue()), dir.toPath());

        }


        //double[][] result = new double[records.size()][2];


    }

    @FXML
    private void convertToCsv() throws IOException {
        DBSession session = sessionTree.getSelectionModel().getSelectedItems().get(0).getValue();
        dbService.uploadRecords(session);
        ArrayList<DBRecord> records = session.getRecords();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(redWaveCheck.getScene().getWindow());
        File pathToFile = dir.toPath().resolve("logs.csv").toFile();
        Files.createFile(pathToFile.toPath());
        FileWriter fileWriter = new FileWriter(pathToFile);

        ArrayList<String> names = new ArrayList<>();
        names.add("Time");
        dBValues.forEach(v->{
            for (int i = 0; i < v.checkBoxes.size(); i++) {
                if (v.checkBoxes.get(i).isSelected()) names.add(v.getName()+" "+v.checkBoxes.get(i).getText());
            }
        });
        CSVUtills.writeLine(fileWriter, names);
        records.forEach(r->{

            ArrayList<String> values = new ArrayList<>();
            values.add(String.valueOf(r.getTime()));
            dBValues.forEach(v->{
                String[] arr = r.getValues(v.type);
                for (int i = 0; i < v.checkBoxes.size(); i++) {
                    if (v.checkBoxes.get(i).isSelected()) {
                        values.add(arr[i]);
                    }
                }
            });

            try {
                CSVUtills.writeLine(fileWriter, values);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*String[] arr = r.getValues(DBRecordType.BATTERIES);
            try {
                CSVUtills.writeLine(fileWriter, Arrays.asList(String.valueOf(r.getTime()), arr[0], arr[1], arr[2]));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        });
        fileWriter.close();
    }

    private void saveTrack(ArrayList<DBRecord> records, DBRecordType type, String name, String color, Path dir){
        ArrayList<double[]> result = new ArrayList<>();
        HashMap<String, Integer> fildsNum = new HashMap<>();

        if (type.equals(DBRecordType.GPS_BOARD) || type.equals(DBRecordType.GPS_REDWAVE)){
            fildsNum.put("lat", 0);
            fildsNum.put("lng", 1);
        } else {
            fildsNum.put("lat", 3);
            fildsNum.put("lng", 4);
        }

        records.forEach(r->{
            String[] values = r.getValues(type);
            double lat = Double.parseDouble(values[fildsNum.get("lat")]);
            double lng = Double.parseDouble(values[fildsNum.get("lng")]);
            if (lat != 0.0 || lng !=0.0) {
                double[] arr = {lng, lat};
                result.add(arr);
            }
        });

        String json = GeoJsonParser.toGeoJsonLineString(result, name, color);
        Path file = dir.resolve(Paths.get(name+".json"));

        try {
            Files.write(file, json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initialize(){
        dBValues = new ArrayList<>();

        initTreeView();
        initGridView();
       /* dbPathLabel.textProperty().bindBidirectional(dbPathProperty, new StringConverter<Path>() {
            @Override
            public String toString(Path object) {
                return object.toString();
            }

            @Override
            public Path fromString(String string) {
                return Paths.get(string);
            }
        });*/
    }

    private void initTreeView() {
        TreeItem<DBSession> sessionRoot = new TreeItem<>(new DBSession(0, "session", "", 0, "", 0));
        sessionTree.setRoot(sessionRoot);
        sessionTree.setShowRoot(false);
        sessionRoot.setExpanded(true);

        sessionTree.setCellFactory(new Callback<TreeView<DBSession>, TreeCell<DBSession>>() {
            @Override
            public TreeCell<DBSession> call(TreeView<DBSession> param) {
                return new TreeCell<DBSession>(){
                    @Override
                    public void updateItem(DBSession session, boolean empty){
                        super.updateItem(session, empty);
                        if (empty) {
                            setText("");
                            setGraphic(null);
                        } else {
                            setText(session.getName()+" "+session.getTime());
                        }
                    }
                };
            }
        });

        dbPathProperty.addListener((observable, oldValue, newValue) -> {
            dbService = new DBService(newValue.toString());

            if (dbService.init()){
                dbService.getSessions().forEach(s->{
                    TreeItem<DBSession> treeItem = new TreeItem<>(s);
                    /*s.getRecords().forEach(r->{
                        TreeItem<String> subTreeItem = new TreeItem<>(r.toString());
                        treeItem.getChildren().add(subTreeItem);
                    });*/
                    sessionRoot.getChildren().add(treeItem);
                });
            }
        });

        sessionTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            fillSessionInfo(newValue.getValue());
        });
    }

    private void initGridView(){
        gridView.setItems(FXCollections.observableArrayList(Arrays.asList(DBRecordType.values())));

        gridView.setCellFactory((Callback<GridView<DBRecordType>, GridCell<DBRecordType>>) param -> {
            return new GridCell<DBRecordType>(){
                @Override
                public void updateItem(DBRecordType item, boolean empty){
                    if (empty || item==null){
                        setGraphic(null);
                        setText(null);
                    } else {
                        DBTypesElement elem = new DBTypesElement(item);
                        dBValues.add(elem);
                        setGraphic(elem);
                        setPrefWidth(70);
                    }
                }
            };
        });

    }

    private String toRGBCode( Color color )
    {
        return String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }

    private void fillSessionInfo(DBSession session){
        nameLabel.setText(session.getName());
        dateLabel.setText(session.getDate().toString());
        timeLabel.setText(formatTime(session.getTime()));
        recordsLabel.setText(String.valueOf(session.getRecordsSize()));
    }

    private String formatTime(long time){
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("mm:ss:SSS");
        return formatter.format(date);
    }

    private class DBTypesElement extends VBox {
        String name;
        ArrayList<String> elementNames;
        Label nameLabel;
        ArrayList<CheckBox> checkBoxes;
        DBRecordType type;

        DBTypesElement(DBRecordType type){
            super();
            this.type = type;
            this.elementNames = new ArrayList();
            this.checkBoxes = new ArrayList<>();

            this.name = type.getName();
            nameLabel = new Label(name);
            this.getChildren().add(nameLabel);
            for (int i = 0; i < type.getParameters().length; i++) {
                CheckBox checkBox = new CheckBox(type.getParameters()[i]);
                elementNames.add(type.getParameters()[i]);
                checkBoxes.add(checkBox);
                this.getChildren().add(checkBox);
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<String> getElementNames() {
            return elementNames;
        }

        public void setElementNames(ArrayList<String> elementNames) {
            this.elementNames = elementNames;
        }

        public Label getNameLabel() {
            return nameLabel;
        }

        public void setNameLabel(Label nameLabel) {
            this.nameLabel = nameLabel;
        }

        public ArrayList<CheckBox> getCheckBoxes() {
            return checkBoxes;
        }

        public void setCheckBoxes(ArrayList<CheckBox> checkBoxes) {
            this.checkBoxes = checkBoxes;
        }
    }


}
