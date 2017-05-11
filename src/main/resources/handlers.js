/**
 * Created by User on 11.05.2017.
 */
var handlers = {};

(function () {
    L.MyHandler = L.Handler.extend({
        addHooks:function () {
            L.DomEvent.on(document, 'click', this._doSome, this);
        },

        removeHooks: function () {
            L.DomEvent.off(document, 'click', this._doSome, this);
        },

        _doSome: function (event) {
            console.log("from handlers!")
        }
    });

    L.Map.addInitHook('addHandler', 'myclick', L.MyHandler);

    handlers.deleteVertex = function (e) {
        console.log("deleted vertex:");
        console.log(e.latlng);
    };

    handlers.creteNewVertex = function (e) {
        console.log("vertex created");
        console.log(e);
        points.push(e.latlng);
    };

    handlers.createLine = function (e) {
        console.log("Create");
    };

    handlers.stopCreatingLine = function (e) {
        console.log("stop creating:");
        console.log(e.target);
    }
})();