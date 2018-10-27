const WebSocket = require('uws').Server;
const VAL_32 = 0x100000000;

function start(){
  console.log('starting ws...');
  var wss = new WebSocket({
    port: 9000,
    path: '/wss'
  });
  console.log('uws started listening on port 9000');

  wss.on('connection', function(ws){
    sendJson(ws, 'ready');

    ws.on('message', function(message){
      if(typeof message == 'string'){
        console.log("string message ", message);
      }
      else{
        var buff = Buffer.from(message);
        console.log("buffer ", buff);
        console.log("buffer len ", buff.length);
        var ack = buff.readUIntLE(0, 4);
        var vid = buff.readUIntLE(4, 4);
        console.log("ack is ", ack, "vid is ", vid);
      }
    });

    ws.on('close', function(e){
      console.log("close web socket.");
      ws.close();
      process.exit(1);
    });

    ws.on('end', function(e){
      console.log("end web socket.");
      clearWS(e);
    });

    ws.on('disconnect', function(e){
      console.log("disconnect web socket.");
      clearWS(e);
    });

    ws.on('error', function(e){
      console.log("error web socket.");
      clearWS(e);
    });

    function clearWS(e){
      console.log('on connection end', e);
    }
  });
}

function sendJson(ws, action, data){
  try{
    if(ws && ws.readyState == 1)
      ws.send(JSON.stringify({a:action, d:data}));
  }
  catch(e){
    logger.error('ws sendJson error', e);
  }
}

start();