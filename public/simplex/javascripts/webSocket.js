/**
 * User: Mart0
 * Date: 4/30/12
 */
var socket;

function initWebSocket(webSocketUrl) {
    window.alert = function (txt) {
        smoke.alert(txt);
    };
    if (WebSocket || window["MozWebSocket"]) {
        var WS = WebSocket ? WebSocket : window["MozWebSocket"];
        socket = new WS(webSocketUrl);
        socket.onopen = null;
        socket.onclose = null;
        socket.onmessage = null;
        socket.onerror = null;
        initApp();
    } else {
        alert("Your browser doesn't support WebSocket");
    }
}

function initApp() {
//    smoke.signal("web socket connected");
    initSimplexController();
}

function sendToServer(json) {
    socket.send(json);
}

function setOnMessageCallback(fcallback) {
    socket.onmessage = fcallback;
}

function setOnOpenCallback(fcallback) {
    socket.onopen = fcallback;
}

function setOnCloseCallback(fcallback) {
    socket.onclose = fcallback;
}

function setOnErrorCallback(fcallback) {
    socket.onerror = fcallback;
}