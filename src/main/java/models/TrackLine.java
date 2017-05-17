package models;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 12.05.2017.
 */
public class TrackLine implements TrackItem{

    private int id;
    private double length;
    private ObservableList<TrackPoint> points = FXCollections.observableArrayList();
    private String name;
    private LineTypes type;



    public TrackLine(){
        name = "Unnamed Track";
    }
    public TrackLine(int id){
        this.id = id;
    }
    public TrackLine(int id, List<TrackPoint> points){
        this();
        this.id = id;
        this.points.addAll(points);
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

    public ObservableList<TrackPoint> getPoints() {
        return points;
    }

    public void setPoints(ObservableList<TrackPoint> points) {
        this.points = points;
    }

    public void addPoint(TrackPoint point){
        points.add(point);
    }

    public void setName(String name){
        this.name = name;
    }

    public LineTypes getType() {
        return type;
    }

    public void setType(LineTypes type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return id +" " + name;
    }

    public static enum LineTypes{
        TRACK, RREGION, CIRCLE
    }
}
