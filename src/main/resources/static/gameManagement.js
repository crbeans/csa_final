import { connect, subscribeTo } from "./stompClient.js";
let connected = false;

function setConnected(val) {
  connected = val;
  if (val) {
    $("#connectionStatus").html("<p>Connected to websocket!</p>");
  }
}

function onClick() {
  connect();
  setConnected(true);
  subscribeTo("/topic/joined", (message) => {
    $("#greetings").append(
      "<tr><td>" + JSON.parse(message.body).content + "</td></tr>"
    );
  });
}

$(function () {
  $("#connect").click(() => onClick());
});
