//imports
//importing a http.js file from node runtime
var http = require("http");
var comments = require("comments")

function onrequest(request,response){
	comments.display();
	response.writeHead(200, {"Content-Type": "text/plain"});
	response.write("Hello World");
	response.end();
}

//pass the onrequest function
http.createServer(onrequest).listen(8080);