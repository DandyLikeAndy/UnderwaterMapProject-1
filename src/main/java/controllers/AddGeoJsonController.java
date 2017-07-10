package controllers;

import JSBridge.JsBridge;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AddGeoJsonController {
    JsBridge jsBridge;

    @FXML
    private TextField pathField;

    @FXML
    private ColorPicker colorField;

    @FXML
    private TextField nameField;

    @FXML
    void addTrack(ActionEvent event) {
        Path path = Paths.get(pathField.getText());
        String color = colorField.getValue().toString();
        String name = nameField.getText();
    }

    @FXML
    void cancel(ActionEvent event) {

    }

    @FXML
    void chooseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(pathField.getScene().getWindow());
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


    public void initialize(){
    }

    public void setJsBridge(JsBridge jsBridge){
        this.jsBridge = jsBridge;
    }

}
