package models;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.List;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

/**
 * Created by User on 12.05.2017.
 */
public class TrackLine implements TrackItem{

    private int id;
    private DoubleProperty length = new SimpleDoubleProperty();
    private ObservableList<Waypoint> points = FXCollections.observableArrayList();
    private SortedList<Waypoint> waypointSortedList = new SortedList<>(points, (o1, o2) -> ((Integer)o1.getPosition()).compareTo(o2.getPosition()));
    private ObjectProperty<String> name = new SimpleObjectProperty<>();
    private LineTypes type;



    public TrackLine(){
        name.setValue("Unnamed Track");
        length.setValue(0);
    }
    public TrackLine(int id){
        this();
        this.id = id;
    }
    public TrackLine(int id, List<Waypoint> points){
        this(id);
        this.points.addAll(points);

    }

    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name.getValue();
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLength() {
        return length.get();
    }

    public void setLength(double length) {
        this.length.setValue(length);
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

    public void removePoint(Waypoint waypoint){
        if (points.contains(waypoint)) points.remove(waypoint);
    }

    public void setName(String name){
        this.name.setValue(name);
    }

    public LineTypes getType() {
        return type;
    }

    public void setType(LineTypes type) {
        this.type = type;
    }

    public ObjectProperty<String> nameProperty(){
        return name;
    }

    public DoubleProperty lengthProperty(){
        return length;
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
