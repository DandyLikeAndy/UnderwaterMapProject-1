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



        e.vertex.on("mouseover", function () {
            var distance;
            var index = e.layer._latlngs.indexOf(e.latlng);
            //console.log("index: "+index);
            //console.log("lengs: "+e.layer._latlngs.length);


            lastPoint = e.latlng;
            if (index == e.layer._latlngs.length-1){
                distance = 0;
            } else {
                distance = e.latlng.distanceTo(e.layer._latlngs[index+1]);
            }
            handlers.showPopup(e.latlng, distance)
        });


        var index = e.layer._latlngs.indexOf(e.vertex.latlng);

        console.log("index = "+index);
        console.log("length = " + e.layer._latlngs.length)
        if (index != e.layer._latlngs.length-1){
            console.log("update icons")
            var arr = e.layer._latlngs;
            for (var i = index+1; i<arr.length; i++){
                arr[i].__vertex.options.icon.updateIcon(i);
            }
        }
        if (index == 0){
            e.vertex.setIcon(new L.StartIcon({'number': index}));
        } else e.vertex.setIcon(new L.MyIcon({'number': index}));

        //JAVA.addPoint(e);
    };



    handlers.stopCreatingLine = function (e) {
        if (e.layer instanceof L.Polyline ) {
            e.layer.setStyle({"weight":10});
            lines[e.layer._leaflet_id] = e.layer;
            let latlngs = e.layer.getLatLngs();


            let line = {};
            line.id = e.layer._leaflet_id;
            line.points = [];
            for (let l in latlngs){
                line.points.push({lat:latlngs[l].lat, lng:latlngs[l].lng});
                line.points.lat = latlngs[l].lat;
                line.points.lng = latlngs[l].lng;
            }
            JAVA.log("create");
            JAVA.addLine(JSON.stringify(line));

            e.layer._latlngs[ e.layer._latlngs.length-1].__vertex.setIcon(new L.FinishIcon({'number':e.layer._latlngs.length-1}));
            console.log(e)
        }

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

    handlers.hoverMarker = function (e) {
        console.log(e);
    }

    handlers.showPopup = function (latlng, distance) {
        var popup = L.popup()
            .setLatLng(latlng)
            .setContent('<p>lat: '+latlng.lat+'<br />lng: '+latlng.lng+'<br />distance: '+distance+'</p>')
            .openOn(map);
    };

    handlers.mouseMove = function (e) {
        JAVA.setDistance(e.latlng.distanceTo(lastPoint));
       JAVA.setMouseCoords(e.latlng.lat + ", " + e.latlng.lng);
    }

    handlers.setZoom = function () {
        JAVA.setZoom(map.getZoom());
    }
})();