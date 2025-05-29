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
    answer: $("#playerAnswer").val(),
    submitter: playerName,
  });
  $("#submitAnswer").addClass("disabled");
}

function startGame() {
  $("#statusText").text("Game Started.");
  $("#promptSection").addClass("d-none");
  $("#statusSection").removeClass("d-none");
  $("#submitAnswer").removeClass("disabled");
}

function onMain(msg) {
  const message = JSON.parse(msg.body).content;
  if (message == "gamestart") {
    startGame();
  }
}

function onUserMain(msg) {
  const message = JSON.parse(msg.body);
  console.log(message);
  if (message.content == "answerprompt") {
    $("#statusSection").addClass("d-none");
    $("#promptSection").removeClass("d-none");
    $("#prompt").text(message.data);
  } else if (message.content == "voting") {
    $("#statusText").text("Waiting for responses to be submitted");
  } else if (message.content == "votingOn") {
    const data = JSON.parse(message.data);
    $("#statusSection").addClass("d-none");
    $("#votingSection").removeClass("d-none");

    $(".vote-box").each(function () {
      const option = parseInt($(this).find(".answer-button").data("option"));
      const answer = data[option - 1];

      if (answer !== undefined && answer !== null && answer !== "") {
        $(this).removeClass("d-none");
        $(this).find("#answerText").text(answer);
      } else {
        $(this).addClass("d-none");
      }
    });
  } else if (message.content == "kicked") {
    alert("You have been kicked, someone joined with the same name.");
  }
}

function onVote(option) {
  publishMsg("/app/vote", {
    option: option,
    submitter: playerName,
  });
  $("#votingSection").addClass("d-none");
  $("#statusSection").removeClass("hidden");
  $("#statusText").text("Vote submitted!");
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
  $(".vote-box").click(function () {
    const answer = $(this).find(".answer-button");
    const option = answer.data("option");
    onVote(option);
  });
});
