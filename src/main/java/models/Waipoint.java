package models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import models.behavors.Behavior;

import java.util.List;


/**
 * Created by User on 12.05.2017.
 */
public class Waipoint implements TrackItem{
    private int id;
    private DoubleProperty lat = new SimpleDoubleProperty();
    private DoubleProperty lng = new SimpleDoubleProperty();
    private double azimuth;
    private double distance;
    private double depth;
    private double capture_radius;
    private List<PointTask> tasks;
    private List<Behavior> behaviors;
    private String name;
    private PointTypes type;

    public Waipoint() {
        this.name = "Track Point";
    }

    public Waipoint(double lat, double lng) {
        this.lat.set(lat);
        this.lng.set(lng);
        this.name = "Track Point";
    }

    public Waipoint(int id, double lat, double lng) {
        this.id = id;
        this.lat.set(lat);
        this.lng.set(lng);
        this.azimuth = 0;
        this.distance = 0;
        this.name = "Track Point";
    }

    public Waipoint(int id, double lat, double lng, double azimuth, double distance) {
        this.id = id;
        this.lat.set(lat);
        this.lng.set(lng);
        this.azimuth = azimuth;
        this.distance = distance;
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
        return azimuth;
    }

    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
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

    public List<PointTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<PointTask> tasks) {
        this.tasks = tasks;
    }

    public List<Behavior> getBehaviors() {
        return behaviors;
    }

    public void setBehaviors(List<Behavior> behaviors) {
        this.behaviors = behaviors;
    }

    @Override
    public String toString() {
        return  id +
                ": lat=" + lat +
                ", lng=" + lng;
    }

    public static enum PointTypes{
        START, END, MIDDLE
    }
}
