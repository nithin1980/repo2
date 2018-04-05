/**
 * http://usejsdoc.org/
 */

var socket;

$(document).ready(function() {
	connect();
	
});

function connect(){
    try{

	
	var host = "ws://localhost:8980/Mod/socket/data";
    socket = new WebSocket(host);

        message('<p class="event">Socket Status: '+socket.readyState);

        socket.onopen = function(){
       		 message('<p class="event">Socket Status: '+socket.readyState+' (open)');
        }

        socket.onmessage = function(msg){
        	 var data = JSON.parse(msg.data);
        	 $('#td15').text(data.value);
       		 //message('<p class="message">Received: '+data.value);
        }

        socket.onclose = function(){
       		 message('<p class="event">Socket Status: '+socket.readyState+' (Closed)');
        }			

    } catch(exception){
   		 message('<p>Error'+exception);
    }
}
function send(msg){
    try{
        socket.send(msg);
    } catch(exception){
    	message('<p class="warning"> Error:' + exception);
    }

    

}
function disconnect(){
	socket.close();
}

function message(msg){
	console.log(msg);
}