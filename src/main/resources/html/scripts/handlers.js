/**
 * Created by User on 11.05.2017.
 */
// @flow
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
        if (e.vertex.circle != undefined) {
            e.vertex.circle.remove();
        }
        let layerId = e.layer._leaflet_id;
        lines.get(layerId).points.delete(e.vertex._leaflet_id);
        handlers.updatePositions(lines.get(layerId));
        JAVA.deletePoint(e);
    };

    handlers.creteNewVertex = function (e) {
        console.log("vertex created");
        console.log(e);

        if (e.layer instanceof L.Polyline && !(e.layer instanceof L.Polygon)){
            let layerId = e.layer._leaflet_id;
            let track = lines.get(layerId);

            console.log("track "+layerId);
            console.log(track);
            if (track == undefined){
                if (tempLine != null){
                    track = tempLine;
                } else {
                    track = new Track();
                    track.id = layerId;
                    tempLine = track;
                    console.log("new track")
                }

            }

            let point = new Point();

            let circle = new L.circle(e.latlng, {radius:50}).addTo(map);

            e.vertex.circle = circle;

            e.vertex.point = point;
            let index = e.vertex.latlngs.indexOf(e.vertex.latlng);

            if (index != e.vertex.latlngs.length-1){
                handlers.updatePositions(track);
            }
            if (index == 0){
                e.vertex.setIcon(new L.StartIcon({'number': index}));
            } else e.vertex.setIcon(new L.MyIcon({'number': index}));

            point.vertex = e.vertex;
            point.circle = circle;
            point.id = e.vertex._leaflet_id;
            point.lat = e.latlng.lat;
            point.lng = e.latlng.lng;
            point.pos = index;
            point.circleRadius = 50;

            e.vertex.point = point;

            console.log("track2 "+layerId);
            console.log(track);

            track.addPoint(point);
        }


        e.vertex.on("mouseover", function (e) {

            let distance;
            let index = e.latlng.__vertex.latlngs.indexOf(e.latlng);

            if (index == e.latlng.__vertex.latlngs.length-1){
                distance = 0;
            } else {
                distance = e.latlng.distanceTo(e.latlng.__vertex.latlngs[index+1]);
            }
            handlers.showPopup(e.latlng, distance)
        });




        //JAVA.addPoint(e);
    };



    handlers.stopCreatingLine = function (e) {
        if (e.layer instanceof L.Polyline && !(e.layer instanceof L.Polygon)) {
            e.layer.setStyle({"weight":10});
            let lineId = e.layer._leaflet_id;
            //lines.set(lineId, e.layer);
            let latlngs = e.layer.getLatLngs();

            lines.set(lineId, tempLine);
            tempLine = null;

            console.log("lines:");
            console.log(lines);

            let line = {};
            line.id = e.layer._leaflet_id;
            line.points = [];

            for (let l in latlngs){
                let vertex = e.layer._latlngs[l].__vertex;
                line.points.push({id:vertex._leaflet_id,lat:latlngs[l].lat, lng:latlngs[l].lng});
               /* line.points.lat = latlngs[l].lat;
                line.points.lng = latlngs[l].lng;*/
            }
            JAVA.log("create");
            JAVA.addLine(JSON.stringify(line));
            console.log(line);
            e.layer._latlngs[ e.layer._latlngs.length-1].__vertex.setIcon(new L.FinishIcon({'number':e.layer._latlngs.length-1}));
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

    handlers.dragVertex = function (e) {
        console.log(e);
        if (e.vertex.circle != undefined) {
            e.vertex.circle.redraw();
        }
        e.vertex.point.latlngs = e.latlng;
    }

    handlers.updatePositions = function(track) {
        for(let point of track.points.values()) {
            if (point.pos != point.vertex.latlngs.indexOf(point.vertex.latlng)) {
                point.pos = point.vertex.latlngs.indexOf(point.vertex.latlng);
                point.vertex.options.icon.updateIcon(point.pos);
                JAVA.updatePoint(point);
            }
        }
    }
})();