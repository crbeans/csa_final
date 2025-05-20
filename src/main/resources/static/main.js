import { connect, publishMsg, subscribeTo } from "./stompClient.js";

let connected = false;
let players = 0;

function setConnected(val) {
  connected = val;
  if (val) {
    $("#connectionStatus").html("<span>Connected as main!</span>");
  }
}

function onMainMessage(msg) {}

function onPlayerJoin(msg) {
  players++;
  $("#playersList").append(
    '<tr><td class="text-center">' + JSON.parse(msg.body).content + "</td></tr>"
  );
  $("#playersListTableHeader").val(
    '<th scope="col" class="text-center" id="playersListTableHeader">Players - (' +
      players +
      " connected)</th>"
  );
}

let mainSub, joinedSub;
function onConnect() {
  connect().then(() => {
    setConnected(true);
    mainSub = subscribeTo("/topic/main", (msg) => onMainMessage(msg));
    joinedSub = subscribeTo("/topic/joined", (msg) => onPlayerJoin(msg));
  });
}

$(function () {
  $("#connect").click(() => onConnect());
});
