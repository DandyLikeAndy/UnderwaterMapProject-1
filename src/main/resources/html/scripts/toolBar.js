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

    L.MyIcon = L.DivIcon.extend({
        options: {
            'number': '',
            'elem': ''
        },
        createIcon: function (elem) {
            var divElem = L.divIcon().createIcon(elem);
            var numdiv = document.createElement('div');
            numdiv.setAttribute("class", "number");
            numdiv.innerHTML = this.options['number'] || '';
            divElem.appendChild(numdiv);

            this.options.elem = numdiv;
            return divElem;
        },
        updateIcon: function (num) {
            this.options.number = num;
            this.options.elem.innerHTML = num;
        }
    });

    L.StartIcon = L.MyIcon.extend({
        options: {
            'imgPath':'img/start-flag-big.png'
        },
        createIcon: function (elem) {
            var divElem = L.divIcon().createIcon(elem);
            var numdiv = document.createElement('div');
            numdiv.setAttribute("class", "number");
            numdiv.innerHTML = this.options['number'] || '';
            var img = document.createElement('img');
            img.setAttribute("src", this.options.imgPath);
            img.setAttribute("class", 'start-icon');
            divElem.appendChild(img);
            //divElem.appendChild(numdiv);

            this.options.elem = numdiv;
            return divElem;
        }
    });

    L.FinishIcon = L.MyIcon.extend({
        options: {
            'imgPath':'img/finish-flag-big.png'
        },
        createIcon: function (elem) {
            var divElem = L.divIcon().createIcon(elem);
            var numdiv = document.createElement('div');
            numdiv.setAttribute("class", "finish-number");
            numdiv.innerHTML = this.options['number'] || '';
            var img = document.createElement('img');
            img.setAttribute("src", this.options.imgPath);
            img.setAttribute("class", 'start-icon');
            divElem.appendChild(img);
            divElem.appendChild(numdiv);

            this.options.elem = numdiv;
            return divElem;
        }
    });

    let toolBars = {};
    toolBars.marker = new L.NewMarkerControl();
    toolBars.line = new L.NewLineControl();
    toolBars.polygon = new L.NewPolygonControl();
    toolBars.rectangle = new L.NewRectangleControl();
    toolBars.circle = new L.NewCircleControl();

    map.addControl(toolBars.marker);
    map.addControl(toolBars.line);
    map.addControl(toolBars.polygon);
    map.addControl(toolBars.rectangle);
    map.addControl(toolBars.circle);

    return toolBars;
}