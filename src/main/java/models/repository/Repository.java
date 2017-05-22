package models.repository;

import com.sun.org.apache.regexp.internal.RE;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import models.TrackLine;
import models.Waypoint;

/**
 * Created by User on 22.05.2017.
 */
public class Repository {

    static Repository instance = null;

    private Repository(){
    }

    public static synchronized Repository getInstance(){
        if (instance == null){
            instance = new Repository();
        }
        return instance;
    }

    private ObservableList<TrackLine> lines = FXCollections.observableArrayList();
    private SortedList<Waypoint> currentPoints;
    private ObjectProperty<Waypoint> currentPoint = new SimpleObjectProperty<>();
    private ObjectProperty<TrackLine> currentLine = new SimpleObjectProperty<>();

    public static void setInstance(Repository instance) {
        Repository.instance = instance;
    }

    public ObservableList<TrackLine> getLines() {
        return lines;
    }

    public void setLines(ObservableList<TrackLine> lines) {
        this.lines = lines;
    }

    public SortedList<Waypoint> getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(SortedList<Waypoint> currentPoints) {
        this.currentPoints = currentPoints;
    }

    public Waypoint getCurrentPoint() {
        return currentPoint.get();
    }

    public ObjectProperty<Waypoint> currentPointProperty() {
        return currentPoint;
    }

    public void setCurrentPoint(Waypoint currentPoint) {
        this.currentPoint.set(currentPoint);
    }



    public void addLine(TrackLine line){
        lines.add(line);
    }

    public void setCurrentLine(TrackLine line){

    }
}
