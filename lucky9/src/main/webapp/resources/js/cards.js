
var currMessage;

function handleMessage(message) {
  currMessage = message;
  console.log("command:" + message.command);

  if (message.command === "UPDATE_LIST") {
    updateAll();
  } else if (message.command === 'SET_BET') {
    updateAll();
    addMessage(message.message);
    addCountdown("Game start", 10);
  } else if (message.command === 'START_TURN') {
    updateAll();
    addMessage(message.message);
    addCountdown("Turn", 10);
  } else if (message.command === 'START_TURN_DEALER') {
    updateAll();
    addMessage(message.message);
    addCountdown("Dealer's turn", 3);
  } else if (message.command === 'TURN_STAND') {
    updateAll();
    addMessage(message.message);
  } else if (message.command === 'TURN_HIT') {
    updateAll();
    addMessage(message.message);
  } else if (message.command === 'END_GAME') {
    updateAll();
    addMessage(message.message);
  }
}

function updateComplete() {
  console.log(currMessage);
  if (currMessage && currMessage.command === 'END_GAME') {
    PF('resultsDialog').show();
  }
}

function handleError(error) {
  alert("Socket error: " + error);
}

var gameStatus;
var countdownLabel;
var cd;

$(document).ready(function() {
  gameStatus = $(PrimeFaces.escapeClientId('gameStatus'));

});

function addMessage(text) {
  gameStatus.append(text + '<br />');
  gameStatus.scrollTop(gameStatus.height());
}

function addCountdown(message, targetSec) {
  clearInterval(cd);
  countdownLabel = $(document.createElement('span'));
  gameStatus.append(countdownLabel).append('<br/>');
  gameStatus.scrollTop(gameStatus.height());
  var t = targetSec;
  var r = function() {
    if (t > 0) {
      countdownLabel.text(message + " " + t + " sec");
      t--;
    } else {
      clearInterval(cd);
      countdownLabel.text(message);
    }
  };
  r();
  cd = setInterval(r, 1000);
}