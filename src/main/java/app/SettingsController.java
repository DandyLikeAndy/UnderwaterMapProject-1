package app;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
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
    public void setTileCash(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(main.getParent().getScene().getWindow());
        SettingsProperties.getInstance().setTileCash(file.toString());
    }

    @FXML
    public void saveSettings() throws IOException {
        if (webRadio.isSelected()) {
            SettingsProperties.getInstance().setTileSource("web");
        } else if (cashRadio.isSelected()){
            SettingsProperties.getInstance().setTileSource("cash");
        }

    }

    public void initialize(){
        tileCashField.setText(SettingsProperties.getInstance().getTileCash());
        String tileSource = SettingsProperties.getInstance().getTileSource();
        setRadio(tileSource);
    }

    private void setRadio(String value){
        if (value.equals("web")){
            webRadio.setSelected(true);
            cashRadio.setSelected(false);
        } else {
            webRadio.setSelected(false);
            cashRadio.setSelected(true);
        }
    }

}
