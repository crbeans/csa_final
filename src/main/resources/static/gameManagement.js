import { connect, publishMsg, subscribeTo } from "./stompClient.js";
let connected = false;

function setConnected(val) {
  connected = val;
  if (val) {
    $("#connectionStatus").html("<p>Connected to websocket!</p>");
  }
}

function onClick() {
  connect().then(() => {
    setConnected(true);
    subscribeTo("/topic/joined", (message) => {
      $("#players").append(
        "<tr><td>" +
          JSON.parse(message.body).content +
          '<button class="btn btn-danger kick-player" data-player="' +
          JSON.parse(message.body).data +
          '">Kick Player</button></td></tr>'
      );
    });
    subscribeTo("/topic/main", (msg) => {
      $("#mainWS").append(
        "<tr><td>" +
          new Date().toLocaleTimeString() +
          " | " +
          JSON.parse(msg.body).content +
          " | " +
          JSON.parse(msg.body).data +
          "</tr></td>"
      );
    });
  });
}

function startGame() {
  publishMsg("/app/startGame", { content: "123" });
}

$(function () {
  $("#connect").click(() => onClick());
  $("#startGame").click(() => startGame());
  $("#players").on("click", ".kick-player", function () {
    const pid = $(this).data("player");
    publishMsg("/app/kickPlayer", { content: pid });
  });
  $("#selectPlayers").click(() => {
    publishMsg("/app/manageGame", { content: "getplayersjson" });
  });
  $("#startRound").click(() => {
    publishMsg("/app/manageGame", { content: "startround" });
  });
});
