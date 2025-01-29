package com.mod.web;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mod.process.models.DashBoard;
import com.mod.process.models.dashboard.BNFOptionSellingWithBuyDashboard;
import com.mod.support.ApplicationHelper;
import com.test.TestBNFOptionSellingWithBuy;

@ServerEndpoint(value = "/socket/data")
public class DataSocketServer {
	
	public Session session;
	
	public static List<Session> sessions = new ArrayList<Session>();
	
    @OnOpen
    public void start(Session session) {
        this.session = session;
        System.out.println("starting.."+session.getId());
        String message = "has joined.";
        broadcast(message);
        sessions.add(session);
    }


    @OnClose
    public void end() {
    	System.out.println("end..");

        
    }


    @OnMessage
    public void incoming(String message) {
        // Never trust the client
    	System.out.println("message recieved.."+message+":"+new Date());
    	
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
    	}else if("bothranged".equals(message)){
    		DashBoard.kiteProcess.bothEquals();
    	}else if("combination".equals(message)){
    		DashBoard.kiteProcess.combination();
    	} else if(message!=null && message.contains("ui_")) {
    		DashBoard.parse_and_ProcessUIData(message);
    		
    		new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Session storedSession =  DataSocketServer.sessions.get(0);
					if(!storedSession.isOpen()) {
						storedSession =  DataSocketServer.sessions.get(1);
					}
					
		            try {
		            	String dashb = null;
		            	if(message.contains("CSS")) {
		            		
		            		new Thread( new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									
					            	try {
					            		
										TestBNFOptionSellingWithBuy td = new TestBNFOptionSellingWithBuy();
										td.before();
										//storedSession.getBasicRemote().sendText("{\"ui_1\""+":"+"\"before processed\"}");
										td.testStdFlow();
										
										String dashb = ApplicationHelper.getObjectMapper().writeValueAsString(BNFOptionSellingWithBuyDashboard.getInstance().info);
										Session storedSession =  DataSocketServer.sessions.get(0);
										storedSession.getAsyncRemote().sendText(dashb);
									} catch (JsonProcessingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
								}
							}).start();
		            		
		            		storedSession.getAsyncRemote().sendText("{\"ui_1\""+":"+"\"new thread created\"}");
		            	}
		            	
		            	
		            	
		            	if(message.contains("HTML")) {
		            		dashb = ApplicationHelper.getObjectMapper().writeValueAsString(BNFOptionSellingWithBuyDashboard.getInstance().info);
		            		storedSession.getAsyncRemote().sendText(dashb);
		            	}
		            	
		            	//storedSession.getBasicRemote().sendText("{\"ui_1\""+":"+"\"some data is here\"}");
		            	System.out.println("Sending message back to the browser");
		                
		            } catch (IOException e) {
		                try {
		                    session.close();
		                } catch (IOException e1) {
		                    // Ignore
		                }
		                String message = "has been disconnected.";
		                
		            }

				}
			}).start();
    		
    		System.out.println("new thread done....."+DataSocketServer.sessions.size());
    		Session storedSession =  DataSocketServer.sessions.get(0);
    		if(!storedSession.isOpen()) {
    			storedSession =  DataSocketServer.sessions.get(1);
    		}
    		
    		storedSession.getAsyncRemote().sendText("{\"ui_1\""+":"+"\"thread finished \"}");

    	}
    	
    	
    	

    	
    	
    }




    @OnError
    public void onError(Throwable t) throws Throwable {
        System.out.println("Chat Error: ");
        t.printStackTrace();
    }
    
    public void broadcast(String msg) {
      
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
    
    @Test
    public void test() {
    	DashBoard.kiteProcess.startProcess();
    }

}
