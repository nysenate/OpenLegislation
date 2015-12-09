<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <title>Hello WebSocket</title>
  <script type="application/javascript" async src="/legislation/static/bower_components/sockjs/sockjs.min.js"></script>
  <script type="application/javascript" async src="/legislation/static/bower_components/stomp-websocket/lib/stomp.min.js"></script>
  <script type="text/javascript">
    var stompClient = null;

    function setConnected(connected) {
      document.getElementById('connect').disabled = connected;
      document.getElementById('disconnect').disabled = !connected;
      document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
      document.getElementById('response').innerHTML = '';
    }

    function connect() {
      var socket = new SockJS('/legislation/sock');
      stompClient = Stomp.over(socket);
      stompClient.connect({}, function(frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/event/apiLogs', function(greeting){
          showGreeting(JSON.parse(greeting.body));
        });
      });
    }

    function disconnect() {
      if (stompClient != null) {
        stompClient.disconnect();
      }
      setConnected(false);
      console.log("Disconnected");
    }

    function sendName() {
      var name = document.getElementById('name').value;
      stompClient.send("/app/hello", {}, name);
    }

    function showGreeting(message) {
      var response = document.getElementById('response');
      var p = document.createElement('p');
      p.style.wordWrap = 'break-word';
      p.appendChild(document.createTextNode(message.apiResponse.baseRequest.url));
      response.appendChild(p);
    }
  </script>
</head>
<body onload="disconnect()">
<noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websocket relies on Javascript being enabled. Please enable
  Javascript and reload this page!</h2></noscript>
<div>
  <div>
    <button id="connect" onclick="connect();">Connect</button>
    <button id="disconnect" disabled="disabled" onclick="disconnect();">Disconnect</button>
  </div>
  <div id="conversationDiv">
    <label>What is your name?</label><input type="text" id="name" />
    <button id="sendName" onclick="sendName();">Send</button>
    <p id="response"></p>
  </div>
</div>
</body>
</html>
