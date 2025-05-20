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
  });
}

function startGame() {
  publishMsg("/app/startGame", "123");
}

$(function () {
  $("#connect").click(() => onClick());
  $("#startGame").click(() => startGame());
  $("#players").on("click", ".kick-player", function () {
    const pid = $(this).data("player");
    publishMsg("/app/kickPlayer", { content: pid });
  });
});
