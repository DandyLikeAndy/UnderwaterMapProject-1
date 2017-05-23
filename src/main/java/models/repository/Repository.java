package models.repository;

import com.sun.org.apache.regexp.internal.RE;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.util.Callback;
import models.TrackLine;
import models.Waypoint;

import java.util.Optional;

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

    private ObservableList<TrackLine> lines = FXCollections.observableArrayList(param -> new Observable[]{param.getPoints()});
    private ObservableList<Waypoint> waypoints = FXCollections.observableArrayList();
    private SortedList<Waypoint> currentPoints = new SortedList<>(waypoints);
    //private ObjectProperty<Waypoint> currentPoint = new SimpleObjectProperty<>();
    private ObjectProperty<TrackLine> currentLine = new SimpleObjectProperty<>();
    private ObservableList<Waypoint> currentPoint = FXCollections.observableArrayList(param -> new Observable[]{param.lngProperty(),param.latProperty(),param.positionProperty(),param.distanceProperty(),param.azimuthProperty()});

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
        return currentPoint.get(0);
    }

    public ObservableList<Waypoint> currentPointProperty() {
        return currentPoint;
    }

    public void setCurrentPoint(Waypoint currentPoint) {
        this.currentPoint.add(0,currentPoint);
    }




    public void addLine(TrackLine line){
        lines.add(line);
    }

    public void setCurrentLine(TrackLine line){

    }

    public void deletePoint(int pointId, int lineId){
        TrackLine track = lines.stream().filter(l->l.getId() == lineId).findFirst().get();
        Waypoint waypoint = track.getPoints().stream().filter(p->p.getId() == pointId).findFirst().get();
        track.removePoint(waypoint);
    }

    public void updateTrack(TrackLine target, TrackLine source){
        source.getPoints().forEach(p->{
            Waypoint targetPoint = target.getPoints().filtered(point->point.getId() == p.getId()).get(0);
            Waypoint sourcePoint = p;
            targetPoint.setAzimuth(sourcePoint.getAzimuth());
            targetPoint.setDistance(sourcePoint.getDistance());
            targetPoint.setLat(sourcePoint.getLat());
            targetPoint.setLng(sourcePoint.getLng());
            targetPoint.setPosition(sourcePoint.getPosition());
            targetPoint.setCapture_radius(sourcePoint.getCapture_radius());
        });
    }


}
