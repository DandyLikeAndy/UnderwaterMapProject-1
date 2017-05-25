package models;

/**
 * Created by Anton on 21.05.2017.
 */
public class PointTask {
    String name;
    String target;
    String objectve;

    public PointTask() {
        name = "empty";
        objectve = "empty";
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

    public String getObjectve() {
        return objectve;
    }

    public void setObjectve(String objectve) {
        this.objectve = objectve;
    }
}
