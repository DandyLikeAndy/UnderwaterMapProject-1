/**
 * Created by User on 12.05.2017.
 */
let javaController = {
    log: function () {

    },
    addLineFromWeb: function (line) {

    },
    addPoint: function (point) {

    },
    deletePoint: function (p) {

    },
    clickPoint: function (p) {

    },
    getTilesImgFromWeb: function (i) {

    },
    setStatus: function (s) {

    },
    setMouseCoordsFromWeb: function (c) {

    },
    setDistanceFromWeb: function (f) {

    },
    setZoom: function (z) {

    }
};

function initJava() {
    var obj = {};
    obj.log = function (msg) {
       javaController.log(msg);
    };

    obj.addLine = function (line) {
        javaController.addLineFromWeb(line);
    };

    obj.addPoint = function (point) {
        var obj = {};
        obj.id = point.vertex._leaflet_id;
        obj.lat = point.latlng.lat;
        obj.lng = point.latlng.lng;
        //javaController.addPoint(JSON.stringify(obj));
        //javaController.log("add point: "+JSON.stringify(obj));
    };

    obj.deletePoint = function (point) {
        var id = point.vertex._leaflet_id;
        //javaController.deletePoint(id);
    };

    obj.clickPoint = function (point) {
        //point.cancel();
        var id = point.vertex._leaflet_id;
        //javaController.clickPoint(id);
    };

    obj.returnTilesImg = function(img){
        javaController.getTilesImgFromWeb(img)
    };

    obj.setStatus = function (msg) {
        javaController.setStatus(msg);
    };

    obj.setMouseCoords = function (string) {
        javaController.setMouseCoordsFromWeb(string);
    };
    obj.setDistance = function (msg) {
        javaController.setDistanceFromWeb(msg);
    };
    obj.setZoom = function (zoom) {
        javaController.setZoom(zoom);
    }

    return obj;
}