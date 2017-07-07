package app;

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
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LoadDbController {
    @FXML
    private TreeView<DBSession> sessionTree;
    @FXML
    private TreeView<String> recordTree;


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

        double[][] result = new double[records.size()][2];

        for (int i = 0; i < records.size(); i++) {
            String[] values = records.get(i).getValues(DBRecordType.TRACK);
            double lat = Double.parseDouble(values[3]);
            double lng = Double.parseDouble(values[4]);
            double[] arr = {lat, lng};
            result[i] = arr;
        }

        String json = GeoJsonParser.toGeoJsonLineString(result);
        Path file = Paths.get("C:/Users/User/Desktop/result.json");

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
}
