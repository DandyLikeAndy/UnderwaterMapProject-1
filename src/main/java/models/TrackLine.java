package models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 12.05.2017.
 */
public class TrackLine {
    int id;
    double length;
    List<TrackPoint> points = new ArrayList<>();
    public TrackLine(){

    }
    public TrackLine(int id){
        this.id = id;
    }
    public TrackLine(int id, List<TrackPoint> points){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public List<TrackPoint> getPoints() {
        return points;
    }

    public void setPoints(List<TrackPoint> points) {
        this.points = points;
    }
}
