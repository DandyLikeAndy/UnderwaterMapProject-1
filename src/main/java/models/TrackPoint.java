package models;

/**
 * Created by User on 12.05.2017.
 */
public class TrackPoint implements TrackItem{
    int id;
    double lat;
    double lng;
    double azimuth;
    double distance;
    String name;

    public TrackPoint() {
        this.name = "Track Point";
    }

    public TrackPoint(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
        this.name = "Track Point";
    }

    public TrackPoint(int id, double lat, double lng) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.azimuth = 0;
        this.distance = 0;
        this.name = "Track Point";
    }

    public TrackPoint(int id, double lat, double lng, double azimuth, double distance) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
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
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
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

    @Override
    public String toString() {
        return  id +
                ": lat=" + lat +
                ", lng=" + lng;
    }
}
