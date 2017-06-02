package models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.behavors.Behavior;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by User on 12.05.2017.
 */
public class Waypoint implements TrackItem{
    private int id;
    private DoubleProperty lat = new SimpleDoubleProperty();
    private DoubleProperty lng = new SimpleDoubleProperty();
    private DoubleProperty azimuth = new SimpleDoubleProperty();
    private DoubleProperty distance = new SimpleDoubleProperty();
    private double depth;
    private double capture_radius;
    private ObservableList<PointTask> tasks = FXCollections.observableArrayList();
    private String name;
    private PointTypes type;
    private IntegerProperty position = new SimpleIntegerProperty();

    public Waypoint() {
        this.name = "Track Point";
    }

    public Waypoint(double lat, double lng) {
        this.lat.set(lat);
        this.lng.set(lng);
        this.name = "Track Point";
    }

    public Waypoint(int id, double lat, double lng) {
        this.id = id;
        this.lat.set(lat);
        this.lng.set(lng);
        this.azimuth.set(0);
        this.distance.set(0);
        this.name = "Track Point";
    }

    public Waypoint(int id, double lat, double lng, double azimuth, double distance) {
        this.id = id;
        this.lat.set(lat);
        this.lng.set(lng);
        this.azimuth.setValue(azimuth);
        this.distance.set(0);
        this.name = "Track Point";
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

    public double getLat() {
        return lat.get();
    }

    public DoubleProperty latProperty() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat.set(lat);
    }

    public double getLng() {
        return lng.get();
    }

    public DoubleProperty lngProperty() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng.set(lng);
    }

    public double getAzimuth() {
        return azimuth.get();
    }

    public void setAzimuth(double azimuth) {
        this.azimuth.setValue(azimuth);
    }

    public double getDistance() {
        return distance.get();
    }

    public void setDistance(double distance) {
        this.distance.set(distance);
    }

    public void setName(String name){
        this.name = name;
    }

    public PointTypes getType() {
        return type;
    }

    public void setType(PointTypes type) {
        this.type = type;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public double getCapture_radius() {
        return capture_radius;
    }

    public void setCapture_radius(double capture_radius) {
        this.capture_radius = capture_radius;
    }

    public ObservableList<PointTask> getTasks() {
        return tasks;
    }

    public void setTasks(ObservableList<PointTask> tasks) {
        this.tasks = tasks;
    }

    public int getPosition() {
        return position.get();
    }

    public void setPosition(int position) {
        this.position.setValue(position);
    }

    public IntegerProperty positionProperty(){
        return position;
    }

    public DoubleProperty distanceProperty(){
        return distance;
    }

    public void addTask(PointTask task){
        tasks.add(task);
    }

    public void deleteTask(PointTask task){
        tasks.remove(task);
    }

    public DoubleProperty azimuthProperty(){
        return azimuth;
    }

    @Override
    public String toString() {
        return  position +
                ": lat=" + lat.get() +
                ", lng=" + lng.get();
    }

    public static enum PointTypes{
        START, END, MIDDLE
    }
}
