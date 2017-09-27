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

    },

    deleteLine: function(l){
    },

    addCustomMarker : function (m) {

    },

    addDoneTrack: function(g) {

    }
};


function initJava() {
    let obj = {};
    obj.log = function (msg) {
       javaController.log(msg);
    };

    obj.addLine = function (line) {
        javaController.addLineFromWeb(line);
        console.log('javaController: ', 'addLine: ', line);
    };

    obj.addPoint = function (point, trackId) {
        javaController.addPoint(JSON.stringify(point), trackId);
        //javaController.log("add point: "+JSON.stringify(obj));
        console.log('javaController: ', 'addPoint point, trackId: ', point, trackId);
    };

    obj.deletePoint = function (pointId, layerId) {
        javaController.deletePoint(pointId, layerId);
        console.log('javaController: ', 'deletePoint pointId, layerId: ', pointId, layerId);
    };

    obj.clickPoint = function (point) {
        let id = point.vertex._leaflet_id;
        //javaController.clickPoint(id);
    };

    obj.returnTilesImg = function(img){
        javaController.getTilesImgFromWeb(img);
        console.log('javaController: ', 'returnTilesImg: ', img);
    };

    obj.setStatus = function (msg) {
        javaController.setStatus(msg);
        console.log('javaController: ', 'setStatus: ', msg);
    };

    obj.setMouseCoords = function (string) {
        javaController.setMouseCoordsFromWeb(string);
        //console.log('javaController: ', 'setMouseCoords: ', string);
    };
    obj.setDistance = function (msg) {
        javaController.setDistanceFromWeb(msg);
        //console.log('javaController: ', 'setDistance: ', msg);
    };
    obj.setZoom = function (zoom) {
        javaController.setZoom(zoom);
        console.log('javaController: ', 'setZoom: ', zoom);
    };
    obj.updatePoint = function (point) {
        javaController.updatePoint(JSON.stringify(point));
        console.log('javaController: ', 'updatePoint: ', point);
    };
    obj.updateTrack = function (track) {
        javaController.updateTrack(JSON.stringify(track));
        console.log('javaController: ', 'updateTrack:', track);

    };

    obj.deleteLine = function(id){
        javaController.deleteLine(id);
        console.log('javaController: ', 'deleteLine id: ', id);
    };

    obj.addMarker = function (m) {
        JAVA.log("Add new marker");
        javaController.addCustomMarker(JSON.stringify(m));
        console.log('javaController: ', 'addMarker: ', m);
    };

    obj.addGeoJson = function (g) {
        javaController.addDoneTrack(g);
        console.log('javaController: ', 'addGeoJson g: ', g);
    };

    return obj;
}