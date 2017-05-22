package models;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by User on 12.05.2017.
 */
public class TrackLine implements TrackItem{

    private int id;
    private double length;
    private ObservableList<Waypoint> points = FXCollections.observableArrayList();
    private SortedList<Waypoint> waypointSortedList = new SortedList<>(points, (o1, o2) -> ((Integer)o1.getPosition()).compareTo(o2.getPosition()));
    private String name;
    private LineTypes type;



    public TrackLine(){
        name = "Unnamed Track";
    }
    public TrackLine(int id){
        this.id = id;
    }
    public TrackLine(int id, List<Waypoint> points){
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

    public ObservableList<Waypoint> getPoints() {
        return waypointSortedList;
    }

    public void setPoints(ObservableList<Waypoint> points) {
        this.points = points;
    }

    public void addPoint(Waypoint point){
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
        return id +" "+points;
    }

    public void updatePoint(Waypoint waypoint) {
        Waypoint target = points.filtered(new Predicate<Waypoint>() {
            @Override
            public boolean test(Waypoint p) {
                return p.getId() == waypoint.getId();
            }
        }).get(0);

        target.setPosition(waypoint.getPosition());
        target.setAzimuth(waypoint.getAzimuth());
        target.setLat(waypoint.getLat());
        target.setLng(waypoint.getLng());
    }

    public static enum LineTypes{
        TRACK, RREGION, CIRCLE
    }
}
