package Views;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import models.PointTask;


/**
 * Created by User on 24.05.2017.
 */
public class TaskView extends ListCell<PointTask> {
    private PointTask task;
    TextField name;
    TextField target;
    TextField objective;
    GridPane gridPane;

    @Override
    public void updateItem(PointTask item, boolean empty){
        super.updateItem(item, empty);

        if (item != null){
            gridPane = new GridPane();
            name = new TextField(item.getName());
            target = new TextField(item.getTarget());
            objective = new TextField(item.getObjective());

            gridPane.add(new Label("Name"), 0,0);
            gridPane.add(new Label("Target"), 0,1);
            gridPane.add(new Label("Objective"), 0,2);
            gridPane.add(name, 1,0);
            gridPane.add(target, 1,1);
            gridPane.add(objective, 1,2);

            setGraphic(gridPane);

            name.textProperty().addListener((observable, oldValue, newValue) -> {
                item.setName(newValue);
            });
            objective.textProperty().addListener((observable, oldValue, newValue) -> {
                item.setObjective(newValue);
            });
            target.textProperty().addListener((observable, oldValue, newValue) -> {
                item.setTarget(newValue);
            });
        } else {
            setGraphic(null);
        }

    }


}
