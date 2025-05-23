import {
  connect,
  getIsConnected,
  publishMsg,
  subscribeTo,
} from "./stompClient.js";

let playerName = "";
let mainSub, joinedSub, userSub;

function onSubmitAnswer() {
  publishMsg("/app/submit", {
    answer: $("#answer").val(),
    submitter: playerName,
  });
}

function startGame() {
  $("#statusText").text("Game Started.");
}

function onMain(msg) {
  const message = JSON.parse(msg.body).content;
  if (message == "gamestart") {
    startGame();
  }
}

function onUserMain(msg) {
  const message = JSON.parse(msg.body);
  if (message.content == "answerprompt") {
    $("#statusSection").addClass("hidden");
    $("#answerSection").removeClass("hidden");
    $("#prompt").text(message.data);
  } else if (message.content == "voting") {
    $("#statusSection").addClass("hidden");
    $("#votingSection").removeClass("hidden");
    $("#statusText").text("Waiting for responses to be submitted");
  }
}

$(function () {
  // connect to websocket
  if (getIsConnected() == false) {
    const params = new URLSearchParams(window.location.search);
    connect().then(() => {
      console.log("Connected to websocket");
      if (params.has("playerName")) {
        playerName = params.get("playerName");
        publishMsg("/app/join", { name: params.get("playerName") });
        $("#playerName").text("You are: " + playerName);
      } else {
        // publishMsg("/app/join", { name: "Player" });
        window.location.href = "/?error=NAME_INVALID";
        return;
      }
      userSub = subscribeTo("/user/topic/main", (msg) => onUserMain(msg));
      mainSub = subscribeTo("/topic/main", (msg) => onMain(msg));
    });
  }

  $("#submitAnswer").click(() => onSubmitAnswer());
});
