/**
 * Created by Anton on 19.05.2017.
 */
class Point {
    constructor(lat, lng, id, vertex, circle, pos){
        this._lng = lng;
        this._lat = lat;
        this._vertex = vertex;
        this._circle = circle;
        this._id = id;
        this._pos = pos;
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
    }
}

class Track{
    constructor(id, name, length, points){
        this._id = id;
        this._name = name;
        this._length = length;
        this._points = new Map();
        for (let p in points){
            this._points.set(points[p].id, points[p]);
        }
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
}
let trac = new Track();
