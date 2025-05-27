const stompClient = new StompJs.Client({
  brokerURL: `${
    window.location.protocol == "https:"
      ? "wss://csa-final-project.onrender.com/ws"
      : `ws://${window.location.host}/ws`
  }`,
});

let isConnected = false;

// stompClient.onConnect = (frame) => {
//   isConnected = true;
//   console.log("Connected: " + frame);
//   stompClient.subscribe("/topic/greetings", (greeting) => {
//     showGreeting(JSON.parse(greeting.body).content);
//   });
// };

// stompClient.onWebSocketError = (error) => {
//   console.error("Error with websocket", error);
// };

// stompClient.onStompError = (frame) => {
//   console.error("Broker reported error: " + frame.headers["message"]);
//   console.error("Additional details: " + frame.body);
// };

// export function connect() {
//   stompClient.activate();
// }

export function connect() {
  return new Promise((resolve, reject) => {
    stompClient.onConnect = (frame) => {
      isConnected = true;
      console.log("Connected: " + frame);
      stompClient.subscribe("/topic/greetings", (greeting) => {
        showGreeting(JSON.parse(greeting.body).content);
      });
      resolve(frame); // Resolve promise on successful connect
    };

    stompClient.onWebSocketError = (error) => {
      console.error("Error with websocket", error);
      reject(error); // Reject promise on websocket error
    };

    stompClient.onStompError = (frame) => {
      console.error("Broker reported error: " + frame.headers["message"]);
      console.error("Additional details: " + frame.body);
      reject(new Error(frame.headers["message"])); // Reject promise on broker error
    };

    stompClient.activate();
  });
}

export function disconnect() {
  stompClient.deactivate();
  isConnected = false;
  console.log("Disconnected");
}

export function setIsConnected(val) {
  isConnected = val;
}

export function getIsConnected() {
  return isConnected;
}

function sendName() {
  stompClient.publish({
    destination: "/app/hello",
    body: JSON.stringify({ name: $("#name").val(), age: $("#age").val() }),
  });
}

function joinGame() {
  stompClient.subscribe("/topic/joined", (joinMsg) => {
    showGreeting(JSON.parse(joinMsg.body).content);
  });
  stompClient.publish({
    destination: "/app/join",
    body: JSON.stringify({ name: $("#playerName").val() }),
  });
}

export function publishMsg(destination, values) {
  stompClient.publish({
    destination: destination,
    body: JSON.stringify(values),
  });
}

export function subscribeTo(endpoint, callback) {
  return stompClient.subscribe(endpoint, callback);
}

function showGreeting(message) {
  $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

// $(function () {
//   $("form").on("submit", (e) => e.preventDefault());
//   $("#connect").click(() => connect());
//   $("#disconnect").click(() => disconnect());
//   $("#send").click(() => sendName());
//   $("#joinGame").click(() => joinGame());
// });
