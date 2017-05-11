/**
 * Created by User on 11.05.2017.
 */
function addToolBar(map) {
    L.EditControl = L.Control.extend({

        options: {
            position: 'topleft',
            callback: null,
            kind: '',
            html: ''
        },

        onAdd: function (map) {
            var container = L.DomUtil.create('div', 'leaflet-control leaflet-bar'),
                link = L.DomUtil.create('a', '', container);

            link.href = '#';
            link.title = 'Create a new ' + this.options.kind;
            link.innerHTML = this.options.html;
            L.DomEvent.on(link, 'click', L.DomEvent.stop)
                .on(link, 'click', function () {
                    window.LAYER = this.options.callback.call(map.editTools);
                }, this);

            return container;
        }

    });

    L.NewLineControl = L.EditControl.extend({

        options: {
            position: 'topleft',
            callback: map.editTools.startPolyline,
            kind: 'line',
            html: '\\/\\'
        }

    });

    L.NewPolygonControl = L.EditControl.extend({

        options: {
            position: 'topleft',
            callback: map.editTools.startPolygon,
            kind: 'polygon',
            html: '▰'
        }

    });

    L.NewMarkerControl = L.EditControl.extend({

        options: {
            position: 'topleft',
            callback: map.editTools.startMarker,
            kind: 'marker',
            html: '🖈'
        }

    });

    L.NewRectangleControl = L.EditControl.extend({

        options: {
            position: 'topleft',
            callback: map.editTools.startRectangle,
            kind: 'rectangle',
            html: '⬛'
        }

    });

    L.NewCircleControl = L.EditControl.extend({

        options: {
            position: 'topleft',
            callback: map.editTools.startCircle,
            kind: 'circle',
            html: '⬤'
        }

    });

    map.addControl(new L.NewMarkerControl());
    map.addControl(new L.NewLineControl());
    map.addControl(new L.NewPolygonControl());
    map.addControl(new L.NewRectangleControl());
    map.addControl(new L.NewCircleControl());
}