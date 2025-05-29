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
    // Remove highlight class
    $(this).removeClass("bg-primary");

    // Reset vote count display
    const p = $(this).find(".vote-count");
    p.text("");
    p.addClass("d-none");

    // Hide submitter name
    $(this).find("#submitter").addClass("d-none");

    // (Optional) Reset any additional custom styles or text here
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
    const parsed = JSON.parse(message.data);
    const data = parsed.voteCounts; // vote counts per answer
    const winners = parsed.winners; // array of winner indices (0-based)

    $(".answer-box").each(function () {
      const option = $(this).data("option");
      const index = option - 1;

      // Check if there is vote data for this option
      if (index < data.length && data[index] !== undefined) {
        $(this).show(); // Show the box if data exists

        // Set vote count
        const p = $(this).find(".vote-count");
        p.text(`Votes: ${data[index]}`);
        p.removeClass("d-none");

        // Highlight if this is a winner
        if (winners.includes(index)) {
          $(this).addClass("bg-primary");
        }

        // Show submitter
        $(this).find("#submitter").removeClass("d-none");
      } else {
        $(this).hide(); // Hide the box if no data
      }
    });
  } else if (message.content == "votingOn") {
    const data = JSON.parse(message.data);
    $("#votingBoxes").removeClass("d-none");

    // Hide and reset all answer boxes first
    $(".answer-box").each(function () {
      $(this).hide(); // Start hidden
      const p = $(this).find(".font-weight-bold");
      p.text(""); // Clear any old text
    });

    // Show and populate only boxes with data
    $(".answer-box").each(function () {
      const option = $(this).data("option");
      const index = option - 1;

      if (index < data.length && data[index] !== undefined) {
        const p = $(this).find(".font-weight-bold");
        p.text(`${data[index]}`);
        $(this).show(); // Show only if data exists
      }
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
