package Views;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import models.PointTask;

/**
 * Created by User on 24.05.2017.
 */
public class TaskView extends ListCell<PointTask> {
    private PointTask task;
    Label name;
    Label target;
    Label objective;
    GridPane gridPane;

    @Override
    public void updateItem(PointTask item, boolean empty){
        super.updateItem(item, empty);

        gridPane = new GridPane();
        name = new Label(item.getName());
        target = new Label(item.getTarget());
        objective = new Label(item.getObjectve());

        gridPane.add(new Label("Name"), 0,0);
        gridPane.add(new Label("Target"), 0,1);
        gridPane.add(new Label("Objective"), 0,2);
        gridPane.add(name, 1,0);
        gridPane.add(target, 1,1);
        gridPane.add(objective, 1,2);

        setGraphic(gridPane);

    }


}
