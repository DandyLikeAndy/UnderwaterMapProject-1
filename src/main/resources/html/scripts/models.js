/**
 * Created by Anton on 19.05.2017.
 */
class Point {
    constructor(lat, lng, id, pos = 8, azimuth = 123, distance = 0){
        this._lng = lng;
        this._lat = lat;
        this._id = id;
        this._pos = pos;
        this._azimuth = azimuth;
        this._distance = distance;
    }

    set id(val){
        this._id = val;
    };

    get id(){
        return this._id;
    }

    set lat(val){
        this._lat = val;
    }

    get lat(){
        return this._lat;
    }

    set lng(value){
        this._lng = value;
    }

    get lng(){
        return this._lng;
    }


    get circle() {
        return this._circle;
    }

    set circle(value) {
        this._circle = value;
    }


    set vertex(value) {
        this._vertex = value;
    }


    get vertex() {
        return this._vertex;
    }


    get pos() {
        return this._pos;
    }

    set pos(value) {
        this._pos = value;
    }

    set circleRadius(r){
        this._circle.setRadius(r);
        this._radius = r;
    }

    get circleRadius(){
        return this._radius;
    }

    set latlngs(val){
        this._lat = val.lat;
        this._lng = val.lng;
    }

    set distance(value){
        this._distance = value;
    }

    get distance(){
        return this._distance;
    }

    set azimuth(val){
        this._azimuth = val;
    }

    get azimuth(){
        return this._azimuth;
    }

    set line(line){
        this._line = line;
    }

    get line(){
        return this._line;
    }

    toJSON(){
        let obj = {};
        obj.id = this.id;
        obj.lat = this.lat;
        obj.lon = this.lng;
        obj.index = this.pos;
        obj.capture_radius = this.circleRadius;
        obj.azimuth = this.azimuth;
        obj.distance = this.distance;
        return obj;
    }
}

class Track{
    constructor(id, name = "Unnamed track", length = 0, points = new Map()){
        this._id = id;
        this._name = name;
        this._length = length;
        this._points = points;
    }

    get id() {
        return this._id;
    }

    set id(value) {
        this._id = value;
    }

    get name() {
        return this._name;
    }

    set name(value) {
        this._name = value;
    }

    get length() {
        return this._length;
    }

    set length(value) {
        this._length = value;
    }

    get points() {
        return this._points;
    }

    addPoint(point){
        this._points.set(point.id,point);
    }

    deletePoint(pointId){
        this._points.delete(pointId);
    }

    set layer(layer){
        this._layer = layer;
    }

    get layer(){
        return this._layer;
    }

    toJSON(){
        let obj = {};
        obj.id = this.id;
        obj.length = this.length;
        obj.name = this.name;
        obj.waypoints = [];
        for(let p of this.points.values()) {
           obj.waypoints.push(p);
        }
        return obj;
    }

}


