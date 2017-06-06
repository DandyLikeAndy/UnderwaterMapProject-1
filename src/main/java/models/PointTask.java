package models;

/**
 * Created by Anton on 21.05.2017.
 */
public class PointTask {
    String name;
    String target;
    String objective;

    public PointTask() {
        name = "empty";
        objective = "empty";
        target = "empty";
    }

    public PointTask(String name){
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }
}
