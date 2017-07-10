package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.controlsfx.control.SegmentedButton;
import utills.MapSource;
import utills.SettingsProperties;

import java.io.File;
import java.io.IOException;

public class SettingsController {
    @FXML
    private TextField tileCashField;

    @FXML
    private RadioButton cashRadio;

    @FXML
    private RadioButton webRadio;

    @FXML
    private AnchorPane main;

    @FXML
    private HBox mapSource;

    ToggleGroup toggleGroup;
    ToggleGroup mapSourceToggleGroup;

    @FXML
    public void setTileCash() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(main.getScene().getWindow());
        tileCashField.setText(file.toString());
        SettingsProperties.getInstance().setTileCash(file.toString());

    }

    @FXML
    public void saveSettings() throws IOException {
        if (webRadio.isSelected()) {
            SettingsProperties.getInstance().setTileSource("web");
        } else if (cashRadio.isSelected()) {
            SettingsProperties.getInstance().setTileSource("cash");
        }

        SettingsProperties.getInstance().setTileCash(tileCashField.getText());
        //SettingsProperties.getInstance().setTileUrl(mapUrl.getText());
        SettingsProperties.getInstance().saveSettings();

        Stage stage = (Stage) main.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void close(){
        Stage stage = (Stage) main.getScene().getWindow();
        stage.close();

    }
    public void initialize() {
        mapSourceToggleGroup = new ToggleGroup();
        toggleGroup = new ToggleGroup();
        cashRadio.setToggleGroup(toggleGroup);
        webRadio.setToggleGroup(toggleGroup);
        tileCashField.setText(SettingsProperties.getInstance().getTileCash());
        //mapUrl.setText(SettingsProperties.getInstance().getTileUrl());
        String tileSource = SettingsProperties.getInstance().getTileSource();
        setRadio(tileSource);
        setMapSourceToggleGroup();
        mapSourceToggleGroup.selectToggle(mapSourceToggleGroup
                .getToggles().filtered(t->t.getUserData().equals(SettingsProperties.getInstance()
                        .getCurrentMapSource())).get(0));
        mapSourceToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            SettingsProperties.getInstance().setCurrentMapSource((MapSource) newValue.getUserData());
        });
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(webRadio)){
                SettingsProperties.getInstance().setCurrentMapSource((MapSource) mapSourceToggleGroup.getSelectedToggle().getUserData());
                SettingsProperties.getInstance().setTileSource("web");
            } else {
                SettingsProperties.getInstance().setTileSource("cash");
            }
        });
    }

    private void setRadio(String value) {
        if (value.equals("web")) {
            toggleGroup.selectToggle(webRadio);
        } else {
            toggleGroup.selectToggle(cashRadio);

        }
    }

    private void setMapSourceToggleGroup(){
        SettingsProperties.getInstance().mapSourcesList().forEach(m->{
            RadioButton radioButton = new RadioButton(m.getName());
            radioButton.setUserData(m);
            mapSource.getChildren().add(radioButton);
            radioButton.setToggleGroup(mapSourceToggleGroup);
        });
    }

}
