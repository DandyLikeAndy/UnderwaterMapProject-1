package Views;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
    private HBox hBox;


    @Override
    public void updateItem(Behavior item, boolean empty){
        super.updateItem(item, empty);
        if (empty){
            setText("");
            setGraphic(null);
        } else {
            hBox = new HBox();
            hBox.setPrefWidth(USE_COMPUTED_SIZE);
            Button deleteButton  = new Button("del");
            behavior = item;
            ObservableList<Behavior.BEHAVIOR_TYPE> behavior_types = FXCollections.observableArrayList(Behavior.BEHAVIOR_TYPE.values());
            choiceBox = new ChoiceBox<>(behavior_types);
            gridPane = new GridPane();
            titledPane = new TitledPane();
            titledPane.setContent(gridPane);
            hBox.getChildren().add(choiceBox);
            hBox.getChildren().add(deleteButton);

            titledPane.setGraphic(hBox);
            titledPane.setExpanded(false);
            setGraphic(titledPane);

            choiceBox.setValue(item.getType());


            fillOptions();

            choiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                behavior.setType(newValue);
                fillOptions();
            });

            deleteButton.setOnAction(event -> {
                this.getListView().getItems().remove(behavior);
            });
        }


    }

    private void fillOptions(){
        gridPane.getChildren().clear();
        ArrayList<String> options = behavior.getType().getOptionsList();
        final int[] rowIndex = {0};
        options.forEach(o->{
            gridPane.add(new Label(o), 0, rowIndex[0]);
            String value = (String) behavior.getOptions().get(o);
            TextField textField = new TextField(value);
            gridPane.add(textField, 1, rowIndex[0]);

            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                behavior.getOptions().put(o, newValue);
            });

            rowIndex[0]++;
        });
    }
}
