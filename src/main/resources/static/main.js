import { connect, publishMsg, subscribeTo } from "./stompClient.js";

let connected = false;
let players = 0;

function setConnected(val) {
  connected = val;
  if (val) {
    $("#connectionStatus").html("<span>Connected as main!</span>");
  }
}

function startGame() {}

function onMainMessage(msg) {
  const content = JSON.parse(msg.body).content;
  const data = JSON.parse(msg.body).data;
  if (content == "gamestart") {
    $("#statusText").text("Game Started");
  }
}

function onPlayerJoin(msg) {
  players++;
  $("#playersList").append(
    '<tr><td class="text-center">' + JSON.parse(msg.body).content + "</td></tr>"
  );
  $("#playersListTableHeader").text("Players - " + players);
}

function onVoting(msg) {}

let mainSub, joinedSub, votingSub;
function onConnect() {
  connect().then(() => {
    setConnected(true);
    mainSub = subscribeTo("/topic/main", (msg) => onMainMessage(msg));
    joinedSub = subscribeTo("/topic/joined", (msg) => onPlayerJoin(msg));
    votingSub = subscribeTo("/topic/voting", (msg) => onVoting(msg));
  });
}

$(function () {
  onConnect();
  $("#connect").click(() => onConnect());
});
