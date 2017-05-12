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

}
