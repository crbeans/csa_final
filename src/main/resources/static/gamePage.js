import { connect, getIsConnected, publishMsg } from "./stompClient.js";

let playerName = "";

function onSubmitAnswer() {
  console.log($("#answer").val());
  publishMsg("/app/submit", {
    answer: $("#answer").val(),
    submitter: playerName,
  });
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
      }
    });
  }

  $("#submitAnswer").click(() => onSubmitAnswer());
});
