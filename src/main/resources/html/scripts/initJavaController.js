
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

    },
    updatePoint: function (p) {

    },
    updateTrack: function (t) {

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

    obj.addPoint = function (point, trackId) {
        javaController.addPoint(JSON.stringify(point), trackId);
        //javaController.log("add point: "+JSON.stringify(obj));
    };

    obj.deletePoint = function (pointId, layerId) {
        javaController.deletePoint(pointId, layerId);
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
    };
    obj.updatePoint = function (point) {
        javaController.updatePoint(JSON.stringify(point));
    };
    obj.updateTrack = function (track) {

        javaController.updateTrack(JSON.stringify(track));
    }

    return obj;
}