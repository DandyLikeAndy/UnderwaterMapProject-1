/**
 * Created by User on 12.05.2017.
 */
function initJava() {
    var obj = {};
    obj.log = function (msg) {
       //javaController.log(msg);
    };

    obj.addLine = function (line) {
        //javaController.addLine(line);
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

    return obj;
}