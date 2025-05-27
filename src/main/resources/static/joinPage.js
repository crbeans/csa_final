import { connect, setIsConnected } from "./stompClient.js";

function onJoin() {
  connect();
  setIsConnected(true);
  window.location.href = "/game.html?playerName=" + $("#input").val();
}

$(function () {
  $("form").on("submit", (e) => e.preventDefault());
  $(document).ready(function () {
    $("#joinButton").on("click", function (event) {
      const form = $(this).closest("form")[0];
      if (!form.checkValidity()) {
        event.preventDefault();
        form.reportValidity();
        return;
      }
      onJoin();
    });
  });
});
