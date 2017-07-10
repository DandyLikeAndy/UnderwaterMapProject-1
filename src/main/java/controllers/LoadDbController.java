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
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

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


    private ObjectProperty<Path> dbPathProperty = new SimpleObjectProperty<>();
    private DBService dbService;

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
        initTreeView();

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
    }

    private String toRGBCode( Color color )
    {
        return String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }

}
