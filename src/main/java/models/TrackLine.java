package models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 12.05.2017.
 */
public class TrackLine implements TrackItem{
    int id;
    double length;
    List<TrackPoint> points = new ArrayList<>();
    String name;

    public TrackLine(){
        name = "Unnamed Track";
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

    @Override
    public String getName() {
        return name;
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

    public void setName(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return id +" " + name;
    }
}
