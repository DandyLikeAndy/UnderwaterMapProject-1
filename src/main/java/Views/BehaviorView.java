package Views;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import models.behavors.Behavior;

import javax.lang.model.AnnotatedConstruct;
import java.util.ArrayList;

/**
 * Created by User on 25.05.2017.
 */
public class BehaviorView extends ListCell<Behavior> {
    private Behavior behavior;
    private ChoiceBox<Behavior.BEHAVIOR_TYPE> choiceBox;
    private GridPane gridPane;
    private TitledPane titledPane;


    @Override
    public void updateItem(Behavior item, boolean empty){
        super.updateItem(item, empty);
        if (empty){
            setText("");
            setGraphic(null);
        } else {
            behavior = item;
            ObservableList<Behavior.BEHAVIOR_TYPE> behavior_types = FXCollections.observableArrayList(Behavior.BEHAVIOR_TYPE.values());
            choiceBox = new ChoiceBox<>(behavior_types);
            gridPane = new GridPane();
            titledPane = new TitledPane();
            titledPane.setContent(gridPane);
            titledPane.setGraphic(choiceBox);
            setGraphic(titledPane);

            choiceBox.setValue(item.getType());

            fillOptions();

            choiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                behavior.setType(newValue);
                fillOptions();
            });
        }
    }

    private void fillOptions(){
        gridPane.getChildren().clear();
        ArrayList<String> options = behavior.getType().getOptionsList();
        final int[] rowIndex = {0};
        options.forEach(o->{
            gridPane.add(new Label(o), 0, rowIndex[0]);
            gridPane.add(new Label("empty"), 1, rowIndex[0]);
            rowIndex[0]++;
        });
    }
}
