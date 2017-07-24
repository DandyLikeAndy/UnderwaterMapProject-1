(function () {

    //определяем TrackManager (TM) в Window
    let TM = {};
    if (typeof window !== 'undefined') {
        expose();
    }
    function expose() {
        let oldTM = window.TM;
        TM.noConflict = function () {
            window.TM = oldTM;
            return this;
        };
        window.TM = TM;
    }

    //main properties of TM //Перечисляем все св-ва, "Оглавление"
    TM.tracks = new Map; //store all tracks: instances of Track
    TM.pointMarkers = new Map;//store pointMarkers: instances of PointMarker//пока не используется
    TM.map = {}; //store instance of L.Map
    TM.currentPoint = [59.8424, 30.3991]; //start and current point
    TM.mapParams = {}; //params for L.Map and L.Map.TileLayers
    TM.currentRadius = 10; //value of accuracy circle
    TM.models = {}; //main Classes of TM
    TM.layers = {}; //store layers//пока не используется
    TM.mapHandlers = {}; //store main handlers for TM.map
    TM.intHandlers = {}; //store other handlers, it used inside methods and mapHandlers
    TM.tbInitProp = {}; //store setup properties of controls
    TM.iconInitProp = {}; //store custom Icon properties
    TM.utils = {}; //store various useful functions
    TM.toolbar = {}; //store controls (it defines in TM.__initMethods.toolbarsInit)
    TM.init = function () {}; //initialize TM
    TM._initMethods = {}; //Methods for initialization, it uses in TM.init

    //DEFINE MAIN PROPERTIES

    TM.mapParams = {
        mapId: 'mapid',
        options: { //default map options
            center: TM.currentPoint,
            zoom: 16,
            source: 'otm',
            'editable': true
        },
        sources: {
            osm: {
                urlTemplate: 'http://{s}.tile.osm.org/{z}/{x}/{y}.png',
                options: {
                    maxZoom: 19
                }
            },
            otm: {
                urlTemplate: 'http://{s}.tile.opentopomap.org/{z}/{x}/{y}.png',
                options: {
                    maxZoom: 17
                }
            },
            googleSat: {
                urlTemplate: 'http://{s}.google.com/vt/lyrs=s&x={x}&y={y}&z={z}',
                options: {
                    maxZoom: 20,
                    subdomains:['mt0','mt1','mt2','mt3']
                }
            },
            googleHybrid: {
                urlTemplate: 'http://{s}.google.com/vt/lyrs=s,h&x={x}&y={y}&z={z}',
                options: {
                    maxZoom: 20,
                    subdomains:['mt0','mt1','mt2','mt3']
                }
            },
            googleStr: {
                urlTemplate: 'http://{s}.google.com/vt/lyrs=m&x={x}&y={y}&z={z}',
                options: {
                    maxZoom: 20,
                    subdomains:['mt0','mt1','mt2','mt3']
                }
            }
        }
    };

    TM.models = (function () {
        class Point {
            constructor({lat, lng, id, pos = 8, azimuth = 123, distance = 0,
                            vertex = null, line = null, circleRadius = TM.currentRadius, circle = null}) {
                this._lng = lng;
                this._lat = lat;
                this._id = id;
                this._pos = pos;
                this._azimuth = azimuth;
                this._distance = distance;
                this._vertex = vertex;
                this._radius = circleRadius;
                this._line = line;
                this._circle = circle;
            }

            set id(val) {
                this._id = val;
            }

            get id() {
                return this._id;
            }

            set lat(val) {
                this._lat = val;
            }

            get lat() {
                return this._lat;
            }

            set lng(value) {
                this._lng = value;
            }

            get lng() {
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

            set circleRadius(r) {
                this._circle.setRadius(r);
                this._radius = r;
            }

            get circleRadius() {
                return this._radius;
            }

            set latlng(val) {//я переименовал latlngs в latlng, т.к. здесь одна точка
                this._lat = val.lat;
                this._lng = val.lng;
            }

            get latlng() {
                return L.latLng(this.lat, this.lng);
            }

            set distance(value) {
                this._distance = value;
            }

            get distance() {
                return this._distance;
            }

            set azimuth(val) {
                this._azimuth = val;
            }

            get azimuth() {
                return this._azimuth;
            }

            set line(line) {
                this._line = line;
            }

            get line() {
                return this._line;
            }

            toJSON() {
                let obj = {};
                obj.id = this.id;
                obj.lat = this.lat;
                obj.lon = this.lng;
                obj.index = this.pos;
                obj.capture_radius = this.circleRadius;
                obj.azimuth = this.azimuth;
                obj.distance = this.distance;
                return obj;
            }

            setIcon() {
                let index = this.pos,
                    lastIndex = this.line.points.size - 1,
                    vertex = this.vertex;

                if (index === 0) {
                    vertex.setIcon(new L.DivIcon.CustomIcon.StartIcon({number: index}));
                } else if (index === lastIndex && !this.line.isDrawing) {
                    vertex.setIcon(new L.DivIcon.CustomIcon.FinishIcon({number: index}));
                } else {
                    if (vertex.options.icon.updateIcon) {
                        vertex.options.icon.updateIcon(index);
                    } else {
                        vertex.setIcon(new L.DivIcon.CustomIcon.NumberIcon({number: index}));
                    }
                }
            }
        }

        class Path {
            constructor({id, name, length = 0, points = new Map(), layer = null}) {
                this._id = id;
                this._name = name;
                this._length = length;
                this._points = points;
                this._layer = layer;
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

            set length(value) {//TODO -- длина трека???
                this._length = value;
            }

            get points() {
                return this._points;
            }

            set layer(layer) {
                this._layer = layer;
            }

            get layer() {
                return this._layer;
            }

            setIcons() {
                for (let point of this.points.values()) {
                    point.setIcon();
                }
            }
        }
        //TODO Проверить состав необходимых св-в
        class Track extends Path {
            constructor({name = "Unnamed track"}) {
                super(arguments[0]);
                this.name = name;
            }

            addPoint(point) {
                this._points.set(point.id, point);
            }

            deletePoint(pointId) {
                let point = this.points.get(pointId),
                    vertex = point.vertex;

                if (vertex.circle) {
                    vertex.circle.remove();
                }

                //if it is not 'editable:vertex:deleted'
                if (~vertex.getIndex()) {
                    vertex.delete();
                }

                this.points.delete(pointId);
                this.updatePositions();
            }

            updatePositions() {
                for (let point of this.points.values()) {
                    point.pos = point.vertex.getIndex();
                    point.setIcon();
                }
                this.updateDistance();
            }

            updateDistance() {
                for (let point of this.points.values()) {
                    let next = point.vertex.getNext();
                    if (next) { //if it's not end point
                        point.distance = point.vertex.latlng.distanceTo(next.latlng);
                    } else {
                        point.distance = 0;
                    }
                }
            }

            toJSON() {
                let obj = {};
                obj.id = this.id;
                obj.length = this.length;
                obj.name = this.name;
                obj.waypoints = [];
                for (let p of this.points.values()) {
                    obj.waypoints.push(p);
                }
                return obj;
            }

        } //TODO Проверить состав необходимых св-в!

        class DoneTrack extends Path {
            constructor({name = "done track", color}) {
                super(arguments[0]);
                this._name = name;
                this._color = color;
            }

            get color() {
                return this._color;
            }

            set color(value) {
                this._color = value;
            }

            toJSON() {
                let obj = {};
                obj.id = this._id;
                obj.color = this._color;
                obj.name = this._name;
                return obj;
            }
        }

        class PointMarker {
            constructor({lat, lng, id, marker, name}) {

                this._lat = lat;
                this._lng = lng;
                this._id = id;
                this._marker = marker;
                this._name = name;
            }

            get name() {
                return this._name;
            }

            set name(value) {
                this._name = value;
            }

            get lat() {
                return this._lat;
            }

            set lat(value) {
                this._lat = value;
            }

            get lng() {
                return this._lng;
            }

            set lng(value) {
                this._lng = value;
            }

            get id() {
                return this._id;
            }

            set id(value) {
                this._id = value;
            }

            get marker() {
                return this._marker;
            }

            set marker(value) {
                this._marker = value;
            }

            toJSON() {
                let obj = {};
                obj.id = this.id;
                obj.lat = this._lat;
                obj.lon = this._lng;
                obj.name = this._name;
                return obj;
            }

        } //TODO Проверить состав необходимых св-в

        return {
            Point: Point,
            Track: Track,
            DoneTrack: DoneTrack,
            PointMarker: PointMarker
        };
    })();

    //store mapHandlers, устанавливаются при инициализации MT
    /* например:
        nameHandlers: {
            handler: fn || [fns],
            eventType: val,
            context: context //default - windows
        }
     */
    TM.mapHandlers = {

        createNewVertex: {
            eventType: 'editable:vertex:new',
            handler: function (e) {
                if (e.layer instanceof L.Polyline && !(e.layer instanceof L.Polygon)) {

                    let layerId = L.stamp(e.layer),
                        track = TM.tracks.get(layerId),
                        circle = L.circle(e.latlng, {radius: TM.currentRadius}).addTo(TM.map), //accuracy circle
                        index = e.vertex.getIndex(),
                        point = new TM.Point({
                            vertex: e.vertex,
                            circleRadius: TM.currentRadius,
                            id: L.stamp(e.vertex),
                            lat: e.latlng.lat,
                            lng: e.latlng.lng,
                            pos: index,
                            circle: circle
                        });

                    if (!track) {//create track
                        track = new TM.Track({id: layerId, layer: e.layer});
                        track.isDrawing = true; //temp property (delete in 'editable:drawing:end')
                        TM.tracks.set(layerId, track);
                        e.layer.setStyle({color: '#c33a34'});
                        JAVA.log("new track");
                    }

                    track.addPoint(point);
                    point.line = track;

                    //it use in other handlers
                    e.vertex.point = point;
                    e.vertex.circle = circle;
                    if (index === e.vertex.getLastIndex()) {
                        e.layer.lastVertex = e.vertex;
                    }

                    if (!track.isDrawing) {
                        JAVA.addPoint(point, track.id);
                    }

                    if (index !== e.vertex.getLastIndex()) { //if it’s not end point
                        track.updatePositions();
                        JAVA.updateTrack(track);
                    }

                    point.setIcon();
                }

                //TODO подумать об установке обработчика другим способом, для возможности быстрой отмены/назначения всех подобных обработчиков
                TM.showPPopup(TM.getVInfo(e));
                e.vertex.on('mouseover', TM.intHandlers.showVPopup);
                e.vertex.on('mouseout', TM.intHandlers.closePopup);
            }
        },

        stopCreatingLine: {
            eventType: 'editable:drawing:end',
            handler: function (e) {

                if (!(e.layer instanceof L.Polyline) || e.layer instanceof L.Polygon) return;
                if (!e.layer.lastVertex.latlngs.length) return;

                let layerId = L.stamp(e.layer),
                    track = TM.tracks.get(layerId);

                e.layer.setStyle({"weight": 8, color: '#c23731'});
                JAVA.addLine(JSON.stringify(track));

                if (track) delete track.isDrawing;

                e.layer.on('click', TM.intHandlers.lineClick);

                //set finishIcon
                e.layer.lastVertex.point.setIcon();
            }
        },

        cancelDrawing: {
            eventType: 'editable:drawing:cancel',
            handler: function (e) {
                TM.map.closePopup();

                if (!(e.layer instanceof L.Polyline) || e.layer instanceof L.Polygon) return;
                if (e.layer.lastVertex.latlngs.length > 1) return; //if polyline has 1 point

                let index = L.stamp(e.layer),
                    vertex = e.layer.lastVertex;

                vertex.circle.remove();
                TM.tracks.delete(index);

            }
        },

        deleteVertex: {
            eventType: 'editable:vertex:deleted',
            handler: function (e) {

                if (e.layer instanceof L.Polyline && !(e.layer instanceof L.Polygon)) {
                    let layerId = L.stamp(e.layer),
                        pointId = L.stamp(e.vertex),
                        track = TM.tracks.get(layerId);

                    track.deletePoint(pointId);
                    JAVA.log("delete " + pointId + " " + layerId);
                    JAVA.deletePoint(pointId, layerId);
                    JAVA.updateTrack(track);
                }

                TM.intHandlers.closePopup(e);
            }
        },

        dragStartVertex: {
            eventType: 'editable:vertex:dragstart',
            handler: function (e) {
                e.vertex.off('mouseover', TM.intHandlers.showVPopup);
                e.vertex.off('mouseout', TM.intHandlers.closePopup);
                }
        },

        dragVertex: {
            eventType: 'editable:vertex:drag',
            handler: function (e) {
                if (e.vertex.circle) {
                    e.vertex.circle.redraw();
                }

                TM.showPPopup(TM.getVInfo(e));
            }
        },

        dragEndVertex: {
            eventType: 'editable:vertex:dragend',
            handler: function (e) {
                //if it is point of track
                if (e.vertex.point) {
                    let track = e.vertex.point.line;
                    e.vertex.point.latlng = e.vertex.latlng;
                    track.updateDistance();
                    JAVA.updateTrack(track);
                }

                //fix bugs
                if (e.vertex.circle) {
                    e.vertex.circle.redraw();
                }

                e.vertex.on('mouseover', TM.intHandlers.showVPopup);
                e.vertex.on('mouseout', TM.intHandlers.closePopup);
            }
        },

        dragMarker: {
            eventType: 'editable:drag',
            handler: function (e) {
                if (!(e.layer.editor instanceof L.Editable.MarkerEditor)) return;
                TM.showMPopup(TM.getMInfo(e));
            }
        },

        dragStartMarker: {
            eventType: 'editable:dragstart',
            handler: function (e) {
                if (!(e.layer.editor instanceof L.Editable.MarkerEditor)) return;
                e.layer.off('mouseover', TM.intHandlers.showMPopup);
                e.layer.off('mouseout', TM.intHandlers.closePopup);
            }
        },

        dragEndMarker: {
            eventType: 'editable:dragend',
            handler: function (e) {
                if (!(e.layer.editor instanceof L.Editable.MarkerEditor)) return;
                e.layer.on('mouseover', TM.intHandlers.showMPopup);
                e.layer.on('mouseout', TM.intHandlers.closePopup);
            }
        },

        mouseMove: {
            eventType: 'mousemove',
            handler: function (e) {
                JAVA.setDistance(e.latlng.distanceTo(TM.currentPoint));
                JAVA.setMouseCoords(e.latlng.lat + ", " + e.latlng.lng);
            }
        },

        setZoom: {
            eventType: 'zoomend',
            handler: function () {
                JAVA.setZoom(TM.map.getZoom());
            }
        },

        addNewLayer: {
            eventType: 'editable:created',
            handler: function (e) {
                JAVA.log("add layer");
            }
        },

        clickPoint: {//TODO - как используется -????
            eventType: 'editable:vertex:click',
            handler: function (e) {
                JAVA.clickPoint(e);
            }
        },

        drawingMouseDown: {
            eventType: 'editable:drawing:mousedown',
            handler: function (e) {
                let editor = e.layer.editor;
                if (editor instanceof L.Editable.CircleEditor){
                    let info = {//TM.getVInfo(e) неккоректно отображает info, в данном случае, поэтому создаем info "вручную"
                        latlng: e.latlng,
                        radius: 0,
                        type: 'circle'
                    };
                    TM.showPPopup(info);
                }
                if (editor instanceof L.Editable.RectangleEditor){
                    let info = {//TM.getVInfo(e) неккоректно отображает info, в данном случае, поэтому создаем info "вручную"
                        latlng: e.latlng,
                        forwardDist: 0,
                        backDist: 0,
                        type: 'rectangle'
                    };
                    TM.showPPopup(info);
                }
            }
        },

        addLayer: {
            eventType: 'layeradd',
            handler: function (e) {
                let layer = e.layer;

                if ((layer.editor instanceof L.Editable.CircleEditor || layer.editor instanceof L.Editable.RectangleEditor)
                    && layer instanceof L.Marker) {
                    layer.on('mouseover', TM.intHandlers.showVPopup);
                    layer.on('mouseout', TM.intHandlers.closePopup);
                }

                if (layer.editor instanceof L.Editable.MarkerEditor) {
                    let latLng = layer.getLatLng(),
                        id = L.stamp(layer),
                        newMarker = new TM.models.PointMarker({
                            lat: latLng.lat,
                            lng: latLng.lng,
                            id: id,
                            marker: layer,
                            name: 'Unnamed marker'
                        });

                    TM.pointMarkers.set(id, newMarker);

                    JAVA.log("custom marker added");
                    JAVA.addMarker(newMarker);

                    //TM.showMPopup(TM.getMInfo(e));
                    layer.on('mouseover', TM.intHandlers.showMPopup);
                    layer.on('mouseout', TM.intHandlers.closePopup);
                }
            }
        }

    };

    //store other handlers, используемые внутри mapHandlers и других методов
    TM.intHandlers = {
        //show vertex popup
        showVPopup: function (e) {
            TM.showPPopup(TM.getVInfo(e));
        },

        showMPopup: function (e) {
            TM.showMPopup(TM.getMInfo(e));
        },

        closePopup: function (e) {
            TM.map.closePopup();
        },

        lineClick: function (e) {
            let id = L.stamp(e.target);

            TM.showLPopup(TM.getLInfo(e));
            TM.utils.selectLine(id);

            JAVA.log("Click line");
        },

        hoverMarker: function (e) { //TODO пока не используется
        },

        keyDownDelTrack: function (e) {
            if (e.keyCode === 46) {
                TM.utils.deleteLine(TM._selectedTrack.id);
                TM.map.closePopup();
            }
        }
    };

    //defines Leaflet controls - toolbar's initialize properties
    TM.tbInitProp = {
        vectorControl: {
            extendClass: L.Control,
            props: {
                options: {
                    position: 'topleft',
                    callback: null,
                    kind: '',
                    html: ''
                },
                onAdd: function (map) {
                    let container = L.DomUtil.create('div', 'tleaflet-conrol leaflet-bar custom-control'),
                        link = L.DomUtil.create('a', '', container);

                    link.href = '#';
                    link.title = 'Create a new ' + this.options.kind;
                    link.innerHTML = this.options.html;
                    L.DomEvent.on(link, 'click', L.DomEvent.stop)
                        .on(link, 'click', function () {
                            this.options.callback.call(map.editTools);
                        }, this);

                    return container;
                }
            },
            childOpts: {
                NewLineControl: {
                    options: {
                        position: 'topleft',
                        callback: function () {
                            TM.map.editTools.startPolyline()
                        },
                        kind: 'track',
                        html: '<span class="icon-share"></span>'/*'\\/\\'*/
                    }
                },
                NewPolygonControl: {
                    options: {
                        position: 'topleft',
                        callback: function () {
                            TM.map.editTools.startPolygon()
                        },
                        kind: 'polygon',
                        html: '<span class="icon-triangle"></span>'/*'▰'*/
                    }
                },
                NewMarkerControl: {
                    options: {
                        position: 'topleft',
                        callback: function () {
                            TM.map.editTools.startMarker()
                        },
                        kind: 'marker',
                        html: '<span class="icon-map-pin"></span>'/*'🖈'*/
                    }
                },
                NewRectangleControl: {
                    options: {
                        position: 'topleft',
                        callback: function () {
                            TM.map.editTools.startRectangle()
                        },
                        kind: 'rectangle',
                        html: '<span class="icon-square"></span>'/*'⬛'*/
                    }
                },
                NewCircleControl: {
                    options: {
                        position: 'topleft',
                        callback: function () {
                            TM.map.editTools.startCircle(null, {radius: 0});
                        },
                        kind: 'circle',
                        html: '<span class="icon-circle"></span>'/*'⊙'*/
                    }
                }
            },
            noCallback: true
            //TODO пренести callback сюда из TM._initMethods?
        },
        layerControl: {
            extendClass: L.Control,
            props: {
                options: {
                    position: 'topright',
                    callback: null,
                    kind: '',
                    html: ''
                },
                onAdd: function () {
                    let container = L.DomUtil.create('div', 'tleaflet-conrol leaflet-bar custom-layer-control'),
                        link = L.DomUtil.create('a', '', container);

                    link.href = '#';
                    link.title = '' + this.options.kind;
                    link.innerHTML = this.options.html;
                    L.DomEvent.on(link, 'click', L.DomEvent.stop)
                        .on(link, 'click', function () {
                            this.options.callback();
                        }, this);

                    return container;
                }
            },
            childOpts: {
                OSM: {
                    options: {
                        callback: function () {
                            TM.changeMapUrl('osm')
                        },
                        kind: 'OpenStreetMap',
                        html: '<span>OSM</span>'
                    }
                },
                OTM: {
                    options: {
                        callback: function () {
                            TM.changeMapUrl('otm')
                        },
                        kind: 'OpenTopoMap',
                        html: '<span>OTM</span>'
                    }
                },
                googleStr: {
                    options: {
                        callback: function () {
                            TM.changeMapUrl('googleStr')
                        },
                        kind: 'Google streets',
                        html: '<span>GStr</span>'
                    }
                },
                googleSat: {
                    options: {
                        callback: function () {
                            TM.changeMapUrl('googleSat')
                        },
                        kind: 'Google Satellite',
                        html: '<span>GSat</span>'
                    }
                },
                googleHybrid: {
                    options: {
                        callback: function () {
                            TM.changeMapUrl('googleHybrid')
                        },
                        kind: 'Google Hybrid',
                        html: '<span>GH</span>'
                    }
                }
            },
            noCallback: true

        }
    };

    //defines custom Icon properties
    TM.iconInitProp = {
        CustomIcon: {
            extendClass: L.DivIcon,
            props: {
                options: {
                    number: '',
                    numClass: 'number',
                    imgClass: '',
                    imgSrc: '',
                    elemNumber: '',
                    elemImg: ''
                },

                createIcon: function (elem) {
                    let divElem = L.divIcon().createIcon(elem),
                        divNum = L.DomUtil.create('div', this.options.numClass, divElem),
                        img = this.options.imgSrc ? L.DomUtil.create('img', this.options.imgClass, divElem) : '';

                    divNum.innerHTML = this.options.number;
                    this.options.elemNumber = divNum;

                    if (img) img.src = this.options.imgSrc;

                    return divElem;
                },

                updateIcon: function (num) {
                    this.options.number = num;
                    this.setNum(num);
                },

                setNum: function (num) {
                    this.options.elemNumber.innerHTML = num;
                }

            },
            childOpts: {
                StartIcon: {
                    options: {
                        imgSrc: 'img/start-flag-big.png',
                        imgClass: 'start-icon'
                    }
                },
                FinishIcon: {
                    options: {
                        imgSrc: 'img/finish-flag-big.png',
                        imgClass: 'finish-icon'
                    }
                },
                NumberIcon: {
                    options: {
                        numClass: 'number'
                    }
                }
            }
        }
    };

    //initialize TM
    TM.init = function () {

        if (window.L && window.L.Editable) {

            let initMethods = this._initMethods;

            for (let f in initMethods) {

                if (initMethods[f] instanceof Array) {
                    initMethods[f].forEach(function(f) {f()}, this);
                    continue;
                }
                initMethods[f].call(this);
            }
        }

    };

    //various useful functions
    TM.utils = {
        getPosByIp: function () {
            return $.getJSON('//ipinfo.io');
        },

        setPosByIp: function () {
            TM.utils.getPosByIp()
                .success(function (data) {
                    let pos = data.loc.split(',');
                    TM.map.setView(pos);
                    TM.currentPoint = pos;
                })
                .error(function () {
                    throw new Error('Connection error with ipinfo.io')
                });
        },

        changeMapUrl: function (source) {
            let params = TM.mapParams.sources[source];
            TM.utils.setMapUrl(params.urlTemplate, params.options);
        },

        setMapUrl: function (url, opts) {//todo разобраться с subdomains

            TM.map.tileLayer.remove();
            //переработаны строки  setMapUrl из start.html
            /*if (subdomains !== undefined) {
                JAVA.log("url " + url + " " + subdomains.split(","));
                mapLayer = L.tileLayer(url, {
                    attribution: '',
                    subdomains: subdomains.split(",")
                });
            } else {
                mapLayer = L.tileLayer(url, {
                    attribution: ''
                });
            }*/
            //if opts is subdomains
            if (typeof opts === 'string') {

                let subdomains = opts.split(",");

                JAVA.log("url " + url + " " + subdomains.split(","));
                opts = {
                    attribution: '',
                    subdomains: subdomains.split(",")
                }
            }

            TM.map.tileLayer = L.tileLayer(url, opts);
            TM.map.tileLayer.addTo(TM.map);
        },

        /**
         * Расширяет класс Leaflet
         * @param {object} opts о-т параметров для расширения, например:
         *    props:{
                myCustomClass: {
                    //класс, который расширяем (необязательный параметр, если не указан, передаем во втором аргументе ф-ии -  extClass )
                    extendClass: L.Class,
                    //объект с параметрами, передаваемый методу L.extend
                    props: {
                        //любые с-ва объекта передаваемого, в качестве аргумента, в L.class.extend
                        }
                    }
                },
                //объект с параметрами классов наследуемых от customClass
                childOpts: {
                    myChildClass: {
                        //любые с-ва объекта передаваемого, в качестве аргумента, в L.class.extend
                        }
                    },
                    myChildClass2: {}
                },
                noCallback: true //если true  не вызываем callback
            }
         *
         * @param {function} extClass класс который расширяем, необязательный параметр(если не передан, должен определяется в props)
         * @param {function} callback - callback(class, name), в качестве аргументов передаются след пар-ры class - расширенный класс, name - имя расширенного класса
         */
        extLClass: function extLClass(opts, extClass, callback) {

            for (let i in opts) {

                extClass = extClass || opts[i].extendClass;
                let props = opts[i].props || opts[i];

                let cl = extClass[i] = extClass.extend(props);

                if (!opts[i].noCallback) {
                    if (typeof callback === 'function') callback(cl, i);
                }

                if ('childOpts' in opts[i]) {
                    extLClass(opts[i].childOpts, extClass[i], callback);
                }
            }
        },

        //delete map handlers,it defines in TM.mapHandlers
        offMHandler: function (handlerName) {

            let evHandler = TM.mapHandlers[handlerName],
                context = evHandler.context || window,
                handler = evHandler.handler,
                eventType = evHandler.eventType;


            if (handler instanceof Array) {
                handler.forEach(function (item) {
                    TM.map.off(eventType, item, context);
                });
                return;
            }

            TM.map.off(eventType, handler, context);
        },

        //Reinitialization map handlers(TM.mapHandlers)
        onMHandler: function (handlerName) {

            let evHandler = TM.mapHandlers[handlerName],
                context = evHandler.context || window,
                handler = evHandler.handler,
                eventType = evHandler.eventType;


            if (handler instanceof Array) {
                handler.forEach(function (item) {
                    TM.map.on(eventType, item, context)
                });
                return;
            }

            TM.map.on(eventType, handler, context);
        },

        //show Point Popup TODO: Сделать свой Popup, для лучшего контроля
        showPPopup: function (info) {
            let popup = TM._pPopup,
                content = '';

            switch (info.type) {
                case 'polyline':
                case 'polygon':
                    content = '<p>lat: ' + info.latlng.lat +
                        '<br />lng: ' + info.latlng.lng +
                        '<br />forward distance: ' + info.forwardDist +
                        '<br />back distance: ' + info.backDist + '</p>';
                    break;
                case 'circle':
                    content = '<p>lat: ' + info.latlng.lat +
                        '<br />lng: ' + info.latlng.lng +
                        '<br />Radius: ' + info.radius + '</p>';
                    break;
                case 'rectangle':
                    content = '<p>lat: ' + info.latlng.lat +
                        '<br />lng: ' + info.latlng.lng +
                        '<br />side a: ' + info.backDist +
                        '<br />side b: ' + info.forwardDist + '</p>';
                    break;
            }

            if (!popup) {
                popup = TM._pPopup = L.popup({
                    offset: L.point(0, -3),
                    closeOnClick: false
                });
            }

            return popup
                .setContent(content)
                .setLatLng(info.latlng)
                .openOn(TM.map);
        },

        //show Line Popup (info about track),It pops up when click on the track line
        showLPopup: function (info) {
            let popup = TM._lPopup,
                content = '<p>lat: ' + info.latlng.lat + '<br />lng: ' + info.latlng.lng + '<br />name: ' + info.name + '</p>';

            if (!popup) {
                popup = TM._lPopup = L.popup({
                    offset: L.point(0, 3),
                    closeOnClick: true
                });
            }

            return popup
                .setLatLng(info.latlng)
                .setContent(content)
                .openOn(TM.map);
        },

        showMPopup: function (info) {
            let popup = TM._mPopup,
                content = '<p>lat: ' + info.latlng.lat +
                    '<br />lng: ' + info.latlng.lng +
                    '<br />name: ' + info.name + '</p>';

            if (!popup) {
                popup = TM._mPopup = L.popup({
                    offset: L.point(0, -25),
                    closeOnClick: false
                });
            }

            return popup
                .setLatLng(info.latlng)
                .setContent(content)
                .openOn(TM.map);
        },

        //get info about Editable vertex from Event object
        getVInfo: function (e) {
            let vertex = e.vertex || e.target,// e.target используется при vertex.on('mouseover', TM.showVInfo)
                forwardDist = 0,
                backDist = 0,
                radius = 0,
                type = '',
                latlng = e.latlng || vertex.latlng, //лучше использовать e.latlng т.к. vertex.latlng при drag дает значения до изменения
                index = vertex.getIndex(),
                lastIndex = vertex.getLastIndex(),
                getForwardDist = () => latlng.distanceTo(vertex.getNext().latlng).toFixed(2),
                getBackDist = () => latlng.distanceTo(vertex.getPrevious().latlng).toFixed(2);

            if (vertex.editor instanceof L.Editable.PolylineEditor) {
                type = 'polyline';

                if (index !== 0) {
                    backDist = getBackDist();
                }
                if (index !== lastIndex) {
                    forwardDist = getForwardDist();
                }

            } else if (vertex.editor instanceof L.Editable.CircleEditor) {
                type = 'circle';
                radius = vertex.editor.feature.getRadius().toFixed(2);//определяем так из-за неточности определения по latlngs при drag

            } else if (vertex.editor instanceof L.Editable.PolygonEditor) {
                type = 'polygon';
                if (!(index === 0 && lastIndex === 0)) {
                    forwardDist = getForwardDist();
                    backDist = getBackDist();
                }

            } else if (vertex.editor instanceof L.Editable.RectangleEditor) {
                type = 'rectangle';
                forwardDist = getForwardDist();
                backDist = getBackDist();
            }

            return {
                latlng: latlng,
                forwardDist: forwardDist,
                backDist: backDist,
                radius: radius,
                type: type
            };
        },

        //get info about done track vertex from Event object
        getVDoneTrackInfo: function (e) {
            let index = e.target.point.pos,
                latlngs = e.target.latlngs,
                lastIndex = latlngs.length - 1,
                latlng = latlngs[index],
                backDist = 0,
                forwardDist = 0;

            if (index !== 0) {
                backDist = latlng.distanceTo(latlngs[index - 1]).toFixed(2);
            }
            if (index !== lastIndex) {
                forwardDist = latlng.distanceTo(latlngs[index + 1]).toFixed(2);
            }

            return {
                type: 'polyline',
                backDist: backDist,
                forwardDist: forwardDist,
                latlng: latlng
            }
        },

        //get info about marker from Event object
        getMInfo: function (e) {
            let name = e.target.name || (e.layer && e.layer.name),
                latlng = e.latlng || (e.layer && e.layer.getLatLng());

            return {
                type: 'marker',
                name: name,
                latlng: latlng
            };
        },

        //get info about line from Event object
        getLInfo: function (e) {
            let id = L.stamp(e.target),
                track = TM.tracks.get(id);

            return {
                latlng: e.latlng,
                name: track.name
            };
        },

        //delete track
        deleteLine: function (lineId) {
            let track = TM.tracks.get(lineId);

            for (let point of track.points.values()) {
                if (point.vertex.circle) {
                    point.vertex.circle.remove()
                }
                if (track instanceof TM.DoneTrack) {
                    point.vertex.remove();
                }
            }

            track.layer.remove();
            TM.tracks.delete(lineId);
            JAVA.deleteLine(lineId);
        },

        //delete point from track
        deletePoint: function (pointId, lineId) {
            let track = TM.tracks.get(lineId);
            track.deletePoint(pointId);
        },

        selectLine: function (id) {

            if (TM._selectedTrack) {
                let oldSelTrack = TM._selectedTrack;
                TM.utils.unSelectLine();
                if (oldSelTrack.id === id) return;
            }

            let track = TM.tracks.get(id),
                oldColor = track.layer.options.color;

            TM._selectedTrack = track;
            track.layer.setStyle({"color": "#ff0000"});
            track.layer.options.oldColor = oldColor;

            document.addEventListener('keydown', TM.intHandlers.keyDownDelTrack, false)
        },

        unSelectLine: function () {
            let track = TM._selectedTrack,
                oldColor = track.layer.options.oldColor;

            if (oldColor) track.layer.setStyle({color: oldColor});
            TM._selectedTrack = null;

            document.removeEventListener('keydown', TM.intHandlers.keyDownDelTrack, false)
        },

        //TODO: Что этот метод делает??
        getFromJava: function (msg) {
            let parsed = JSON.parse(msg);
            JAVA.log("ID: " + parsed.id + " NAME: " + parsed.name)
        },

        resizeMap: function (width, height) {
            let mapEl =  document.getElementById(TM.mapParams.mapId);
            mapEl.style.width = width;
            mapEl.style.height = height;
        },

        getTilesImg: function () {
            let imgs = '',
                imgEls = document.getElementsByClassName('leaflet-tile-container')[0].getElementsByTagName('img');

            for (let img of imgEls) {
                imgs += img.src + ',';
            }

            JAVA.returnTilesImg(imgs);
        },

        startTrack: function () {
            TM.map.editTools.startPolyline();
            JAVA.setStatus("Start polyline");
        },

        startRegion: function () {
            TM.map.editTools.startRectangle();
            JAVA.setStatus("Start region");
        },

        startMarker: function () {
            JAVA.log("Call method Start marker from JAVA");
            let marker = TM.map.editTools.startMarker();
            marker.name = "Click marker";
            marker.isCustom = true;
        },

        addMarker: function (lat, lng, name) {
            let marker = L.marker([lat, lng]);

            marker.isCustom = true;
            marker.name = name;
            marker.addTo(TM.map);

            marker.on('mouseover', TM.intHandlers.showMPopup);
            marker.on('mouseout', TM.intHandlers.closePopup);

            JAVA.log("Call method ADD marker from JAVA");
        },

        zoomPlus: function () {
            TM.map.setZoom(TM.map.getZoom() + 1);
            JAVA.setStatus("Map zoomed up");
        },

        zoomMinus: function zoomMinus() {
            TM.map.setZoom(TM.map.getZoom() - 1);
            JAVA.setStatus("Map zoomed down");
        },

        getZoom: function () {
            return TM.map.getZoom();
        },

        setPointsRadius: function (radius) {
            for (let track of TM.tracks.values()) {
                for (let point of track.points.values()) {
                    point.vertex.circle.setRadius(radius);
                }
            }
        },

        setPointRadius: function (pointId, radius) {
            for (let track of TM.tracks.values()) {
                for (let point of track.points.values()) {
                    if (point.id === pointId) {
                        point.vertex.circle.setRadius(radius);
                    }
                }
            }
        },

        initJavaController: function () {
            JAVA = initJava();
            TM.utils.removeToolBars();
        },

        //hidden toolbars from TM.toolbar
        removeToolBars: function () {
            for (let t in TM.toolbar) {
                TM.map.removeControl(TM.toolbar[t]);
            }
        },

        //Show toolbars from TM.toolbar
        showToolBars: function () {
            for (let t in TM.toolbar) {
                TM.toolbar[t].addTo(TM.map);
            }
        },

        /**
         * adds track to TM.tracks and TM.map
         * @param {JSON} coords  an array of arrays of LatLng points
         */
        addActiveLine: function(coords) {
            let newLine = L.polyline(JSON.parse(coords)).addTo(TM.map),
                id = L.stamp(newLine),
                newTrack = new TM.models.Track({id: id, layer: newLine}),
                latLngs; //define after newLine.enableEdit();

            TM.tracks.set(id, newTrack);
            newLine.enableEdit(TM.map);
            newLine.setStyle({"weight": 8, color: '#c23731'});

            newLine.on('click', TM.intHandlers.lineClick);

            latLngs = newLine.getLatLngs();

            latLngs.forEach(function (latlng) {
                let vertex = latlng.__vertex,
                    id = L.stamp(vertex),
                    circle = L.circle(latlng, {radius: TM.currentRadius}).addTo(TM.map),
                    point = new TM.models.Point({
                        id: id,
                        vertex: vertex,
                        circleRadius: TM.currentRadius,
                        lat: latlng.lat,
                        lng: latlng.lng,
                        pos: vertex.getIndex(),
                        circle: circle,
                        line: newTrack
                    });

                vertex.point = point;
                vertex.circle = circle;

                vertex.on('mouseover', TM.intHandlers.showVPopup);
                vertex.on('mouseout', TM.intHandlers.closePopup);

                newTrack.addPoint(point);
            });

            newTrack.setIcons();
            newTrack.updateDistance();

            return JSON.stringify(newTrack);

        },
        //add done track to TM.tracks and TM.map from geoJson
        addGeoJson: function (geoJson) {
            let geoJsonObj = JSON.parse(geoJson);

            L.geoJson(geoJsonObj, {
                style: function (feature) {
                    if (feature.geometry.type === "LineString") {
                        let style = feature.properties.style;
                        style.dashArray = style.dashArray || '10,10';
                        style.weight = style.weight || 5;
                        style.color = style.color || '#a81a1a';
                        style.opacity = style.opacity || .7;
                        return feature.properties.style;
                    }
                },
                onEachFeature: onEach
            }).addTo(TM.map);

            function onEach(feature, layer) {
                if (feature.geometry.type !== "LineString") return;

                let points = new Map(),
                    id = L.stamp(layer),
                    doneTrack = new TM.DoneTrack({
                        id: id,
                        name: feature.properties.name,
                        color: feature.properties.style.color,
                        layer: layer,
                        points: points
                    }),
                    pointPosition = 0,
                    latlngs = [];

                layer.name = feature.properties.name;

                feature.geometry.coordinates.forEach(function (lnglat) {
                    let latlng = L.latLng([lnglat[1], lnglat[0]]),
                        vertex = L.marker(latlng),
                        point = new TM.Point({
                            lat: latlng[0],
                            lng: latlng[1],
                            line: doneTrack,
                            id: L.stamp(vertex),
                            pos: pointPosition++,
                            vertex: vertex
                        });

                    doneTrack._points.set(point.id, point);

                    vertex.point = point;
                    vertex.addTo(TM.map);

                    latlngs.push(latlng);
                    vertex.latlngs = latlngs;

                    vertex.on('mouseover', function (e) { TM.showPPopup(TM.getVDoneTrackInfo(e)) });
                    vertex.on('mouseout', TM.intHandlers.closePopup);
                });

                doneTrack.setIcons();
                TM.tracks.set(doneTrack.id, doneTrack);

                doneTrack.layer.on('click', TM.intHandlers.lineClick);

                JAVA.log("Create layer, name: " + feature.properties.name);
                JAVA.addGeoJson(JSON.stringify(doneTrack));
            }
        }
    };

    /**
     * Добаваляет пользовательский код который будет вызван после инициализации
     * @param f
     */
    TM.addInitHook = function (f) {
        this._initMethods.customInit = TM._initMethods.customInit || [];
        this._initMethods.customInit.push(f);
        return this;
    };


    //Methods for initialization, it uses in TM.init
    TM._initMethods = {

        mapInit: function () {

            let map = TM.map = L.map(TM.mapParams.mapId, TM.mapParams.options),
                source = TM.mapParams.options.source || 'osm',
                getUrlTemplate = (name) => TM.mapParams.sources[name].urlTemplate,
                getMapOpts = (name) => TM.mapParams.sources[name].options;


            map.tileLayer = L.tileLayer(
                getUrlTemplate(source), getMapOpts(source)
            ).addTo(map);
        },

        toolbarsInit: function () {
            this.toolbar = {};

            TM.utils.extLClass(this.tbInitProp, null, function (cl, i) {
                let name = i.charAt(0).toLowerCase() + i.slice(1);
                let c = TM.toolbar[name] = new cl();
                TM.map.addControl(c);
            });
        },

        iconInit: function () {
            TM.utils.extLClass(this.iconInitProp, null, null);
        },

        mapHandlersInit: function () {

            for (let eventHandler in TM.mapHandlers) {

                let evHandler = TM.mapHandlers[eventHandler],
                    type = evHandler.eventType,
                    handler = evHandler.handler,
                    context = evHandler.context || window;

                if (!type) continue;

                if (evHandler.target && (evHandler.target !== '' || 'TM.map' || 'map')) continue;

                if (handler instanceof Array) {

                    handler.forEach(function (handler) {
                        TM.map.on(type, handler, this);
                    }, context);

                    continue;
                }

                TM.map.on(type, handler, context);
            }
        },

        customInit: []
    };

    //shortcuts for most used functions
    TM.Point = TM.models.Point;
    TM.Track = TM.models.Track;
    TM.DoneTrack = TM.models.DoneTrack;
    TM.PointMarker = TM.models.PointMarker;
    TM.onMHandler = TM.utils.onMHandler;
    TM.offMHandler = TM.utils.offMHandler;
    TM.changeMapUrl = TM.utils.changeMapUrl;
    TM.setMapUrl = TM.utils.setMapUrl;
    TM.getVInfo = TM.utils.getVInfo;
    TM.getVDoneTrackInfo = TM.utils.getVDoneTrackInfo;
    TM.getMInfo = TM.utils.getMInfo;
    TM.getLInfo = TM.utils.getLInfo;
    TM.showPPopup = TM.utils.showPPopup;
    TM.showMPopup = TM.utils.showMPopup;
    TM.showLPopup = TM.utils.showLPopup;


})();