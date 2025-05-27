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

function startRound() {
  $(".answer-box").each(function () {
    const p = $(this).find(".font-weight-bold");
    p.addClass("d-none");
  });
  $("#submitter").each(function () {
    $(this).addClass("d-none");
  });
  $("#votingBoxes").addClass("d-none");
}

function onMainMessage(msg) {
  const message = JSON.parse(msg.body);
  const data = JSON.parse(msg.body).data;
  if (message.content == "gamestart") {
    $("#statusText").text("Game Started");
    startRound();
  }
}

function onPlayerJoin(msg) {
  players++;
  $("#playersList").append(
    '<tr><td class="text-center">' + JSON.parse(msg.body).content + "</td></tr>"
  );
  $("#playersListTableHeader").text("Players - " + players);
}

function onVoting(msg) {
  const message = JSON.parse(msg.body);
  if (message.content == "votingend") {
    const data = JSON.parse(message.data);

    let winner = 0;

    for (let i = 1; i < data.length; i++) {
      if (data[i] > data[winner]) {
        winner = i;
      }
    }

    $(".answer-box").each(function () {
      const option = $(this).data("option");
      // vote count
      const p = $(this).find(".vote-count");
      p.text(`Votes: ${data[option - 1]}`);
      p.removeClass("d-none");

      if (option - 1 == winner) {
        $(this).addClass("bg-primary");
      }

      // submitter name
      $(this).find("#submitter").text();
      $(this).find("#submitter").removeClass("d-none");
    });
  } else if (message.content == "votingOn") {
    const data = JSON.parse(message.data);
    $("#votingBoxes").removeClass("d-none");
    $(".answer-box").each(function () {
      const option = $(this).data("option");
      const p = $(this).find(".font-weight-bold");
      p.text(`${data[option - 1]}`);
    });
  } else if (message.content == "votingOnSubmitters") {
    const data = JSON.parse(message.data);
    $(".answer-box").each(function () {
      const option = $(this).data("option");
      const p = $(this).find("#submitter");
      p.text(`Submitter: ${data[option - 1]}`);
    });
  }
}

let mainSub, joinedSub, votingSub;
function onConnect() {
  connect().then(() => {
    console.log("Connected to websocket");
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
