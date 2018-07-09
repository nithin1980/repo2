package com.mod.web;
import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.mod.process.models.DashBoard;

@ServerEndpoint(value = "/socket/data")
public class DataSocketServer {
	
	private Session session;
	
    @OnOpen
    public void start(Session session) {
        this.session = session;
        System.out.println("starting..");
        String message = "has joined.";
        broadcast(message);
    }


    @OnClose
    public void end() {
    	System.out.println("end..");

        
    }


    @OnMessage
    public void incoming(String message) {
        // Never trust the client
    	System.out.println("message recieved.."+message);
    	
    	if("startkite".equals(message)){
    		DashBoard.kiteProcess.startProcess();
    	}else if("dumpkite".equals(message)){
    		DashBoard.kiteProcess.dumpData();
    	}else if("stopkite".equals(message)){
    		DashBoard.kiteProcess.stopProcess();
    	}else if("startpe".equals(message)){
    		DashBoard.kiteProcess.startPE();
    	}else if("stoppe".equals(message)){
    		DashBoard.kiteProcess.stopPE();
    	}else if("both".equals(message)){
    		DashBoard.kiteProcess.bothPE_CE();
    	}else if("startce".equals(message)){
    		DashBoard.kiteProcess.startCE();
    	}else if("stopce".equals(message)){
    		DashBoard.kiteProcess.stopCE();
    	}
    }




    @OnError
    public void onError(Throwable t) throws Throwable {
        System.out.println("Chat Error: ");
        t.printStackTrace();
    }
    
    private void broadcast(String msg) {
      
            try {
                
                session.getBasicRemote().sendText(msg);
                
            } catch (IOException e) {
                try {
                    session.close();
                } catch (IOException e1) {
                    // Ignore
                }
                String message = "has been disconnected.";
                broadcast(message);
            }
    }    

}
