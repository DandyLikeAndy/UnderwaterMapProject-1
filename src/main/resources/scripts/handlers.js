/**
 * Created by User on 11.05.2017.
 */
var handlers = {};

(function () {
    L.MyHandler = L.Handler.extend({
        addHooks:function () {
            L.DomEvent.on(L.Map, 'click', this._doSome, this);
        },

        removeHooks: function () {
            L.DomEvent.off(L.Map, 'click', this._doSome, this);
        },

        _doSome: function (event) {
            console.log("from handlers!")
        }
    });

    L.Map.addInitHook('addHandler', 'myclick', L.MyHandler);

    handlers.deleteVertex = function (e) {
        console.log("deleted vertex:");
        console.log(e.latlng);
        JAVA.deletePoint(e);
    };

    handlers.creteNewVertex = function (e) {
        e.myField = "field";
        console.log("vertex created");
        console.log(e);
        points.push(e);
        JAVA.addPoint(e);
    };

    handlers.createLine = function (e) {
        console.log("Create");
    };

    handlers.stopCreatingLine = function (e) {
        e.layer.setStyle({"weight":10});
        console.log("stop creating:");
        console.log(e.layer instanceof L.Polyline);
        console.log(e.layer.getLatLngs());
        lines[e.layer._leaflet_id] = e.layer;
        console.log(lines);
        JAVA.log("create");
        JAVA.addLine(e.layer);
    };

    handlers.addNewLayer = function (e) {
        console.log("add layer")
    }

    handlers.clickPoint = function (e) {
        JAVA.clickPoint(e);
    }

    handlers.clickLine = function (e) {
        JAVA.log("Click")
    }
})();