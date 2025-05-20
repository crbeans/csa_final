import {
  connect,
  getIsConnected,
  publishMsg,
  subscribeTo,
} from "./stompClient.js";

let playerName = "";

function onSubmitAnswer() {
  console.log($("#answer").val());
  publishMsg("/app/submit", {
    answer: $("#answer").val(),
    submitter: playerName,
  });
}

function setGameStart(val) {
  if (val) {
    $("#statusText").text("Game Started.");
  }
}

function onMain(msg) {
  const message = JSON.parse(msg.body).content;
  console.log(message);
  if (message == "gamestart") {
    setGameStart(true);
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
      } else {
        // publishMsg("/app/join", { name: "Player" });
        window.location.href = "/?error=NAME_INVALID";
        return;
      }

      subscribeTo("/topic/main", (msg) => onMain(msg));
    });
  }

  $("#submitAnswer").click(() => onSubmitAnswer());
});
