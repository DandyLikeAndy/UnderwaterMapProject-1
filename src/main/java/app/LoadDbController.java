package app;

import db.DBService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LoadDbController {
    @FXML
    private TreeView<String> sessionTree;
    @FXML
    private TreeView<String> recordTree;
    @FXML
    private Label dbPathLabel;

    private ObjectProperty<Path> dbPathProperty = new SimpleObjectProperty<>();
    private DBService dbService;

    @FXML
    private void loadDb(){
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(dbPathLabel.getScene().getWindow());

        if (file==null) return;

        dbPathProperty.setValue(file.toPath());


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
        TreeItem<String> sessionRoot = new TreeItem<>("Sessions");
        sessionTree.setRoot(sessionRoot);

        dbPathProperty.addListener((observable, oldValue, newValue) -> {
            dbService = new DBService(newValue.toString());

            if (dbService.init()){
                dbService.getSessions().forEach(s->{
                    TreeItem<String> treeItem = new TreeItem<>(s.toString());
                    s.getRecords().forEach(r->{
                        TreeItem<String> subTreeItem = new TreeItem<>(r.toString());
                        treeItem.getChildren().add(subTreeItem);
                    });
                    sessionRoot.getChildren().add(treeItem);
                });
            }
        });
    }
}
