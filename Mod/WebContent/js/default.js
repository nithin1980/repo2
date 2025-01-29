/**
 * http://usejsdoc.org/
 */

var socket;

$(document).ready(function() {
	connect();
	
});

function connect(){
    try{

	
	var host = "ws://localhost:8080/Mod/socket/data";
    socket = new WebSocket(host);

        message('<p class="event">Socket Status: '+socket.readyState);

        socket.onopen = function(){
       		 message('<p class="event">Socket Status: '+socket.readyState+' (open)');
        }

        socket.onmessage = function(msg){
			console.log("incoming message:"+msg.data);
        	 var data = JSON.parse(msg.data);
        	 
        	 var keys = Object.keys(data);
        	 
        	 for( i=0;i<keys.length;i++){
				
				var identifier = '#'+keys[i];
				
				$(identifier).val(data[keys[i]]);
			 }
        	 
        	 
        	 
        	 
        	// $('#td15').text(data.value);
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

function stopSome(){
	const form = document.querySelector('#form1');
	const formData = new FormData(form);
	const object = {};
	formData.forEach((value, key) => object[key] = value);
	object.action='stop_everything';

	const json = JSON.stringify(object);
	
	send(json);
	
}

function displayjson(){
	const form = document.querySelector('#form1');
	const formData = new FormData(form);
	const object = {};
	formData.forEach((value, key) => object[key] = value);
	object.action='stop_everything';
	
	const json = JSON.stringify(object);
	console.log(json);
	var values  = JSON.parse(json);
	console.log(values);
	var keys = Object.keys(values);
	console.log(keys[0]);
	//get the values using the key.
	
	console.log(values[keys[0]]);
	
	
	
	if(values.missin_field==null){
		console.log("working......");
	}
	$('#some_2').val(values.some_1);
}