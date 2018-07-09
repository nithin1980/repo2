package com.mod.interfaces;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.mod.datafeeder.DataFeed;
import com.mod.process.models.CacheService;
import com.mod.process.models.ProcessModelAbstract;
import com.mod.process.models.ProcessingBlock;
import com.mod.process.models.ProcessingBlock10;
import com.mod.process.models.ProcessingBlock2;
import com.mod.process.models.ProcessingBlock3;
import com.mod.process.models.ProcessingBlock5;
import com.mod.process.models.ProcessingBlock6;
import com.mod.process.models.ProcessingBlock7;
import com.mod.support.ApplicationHelper;
import com.mod.support.ConfigData;

@ClientEndpoint(configurator=WebsocketClientConfig.class)
public class KiteGeneralWebSocketClient extends WebSocketClient {
	
	
	protected static  WebSocketContainer container = ContainerProvider.getWebSocketContainer();
	
	//Quote Parser
	private StreamingQuoteParserModeFull streamingQuoteParser = new StreamingQuoteParserModeFull();
	
	private StreamingQuoteParserModeLtp parserModeLtp = new StreamingQuoteParserModeLtp();
	
	private List<ProcessModelAbstract> processingModels;
	
	private CountDownLatch latch;
	
	private Session kiteSession;
	
	public KiteGeneralWebSocketClient() {
		// TODO Auto-generated constructor stub
		processingModels = new ArrayList<ProcessModelAbstract>();
//		processingModels.add(new ProcessingBlock());
//		processingModels.add(new ProcessingBlock2());
//		processingModels.add(new ProcessingBlock6(CacheService.getInstance()));
		
//		processingModels.add(new ProcessingBlock7(CacheService.getInstance()));
		processingModels.add(new ProcessingBlock10(CacheService.getInstance()));
		
	}
	
	public KiteGeneralWebSocketClient(CountDownLatch latch) {
		// TODO Auto-generated constructor stub
		this.latch =latch;
		
		processingModels = new ArrayList<ProcessModelAbstract>();
//		processingModels.add(new ProcessingBlock());
//		processingModels.add(new ProcessingBlock2());
		//processingModels.add(new ProcessingBlock5(CacheService.getInstance()));
		
		processingModels.add(new ProcessingBlock10(CacheService.getInstance()));
	}
	
	
	
	@Override
	public String mode() {
		// TODO Auto-generated method stub
		return "genwsclient";
	}
	
	@Override
	public ConfigData modeConfig() {
		return ApplicationHelper.Application_Config_Cache.get(mode());
	}

    @OnError
	public void onError(Session session,Throwable t) {
		System.out.println("errror");
	}

    @OnOpen
    public void onOpen(Session session) throws Exception {
    	//send initialising data from a config source....
    	System.out.println("trying to open connect...");
    	session.getBasicRemote().sendText(appConfig().getKeyValueConfigs().get("subscribe_string"));
    	
    	String[] ltpStrings = ApplicationHelper.ltpStrings();
    	
    	for(int i=0;i<ltpStrings.length;i++){
    		session.getBasicRemote().sendText(ltpStrings[0]);
    	}
        
    	System.out.println("connection opened");
    }


    
    @OnMessage
    public void onMessage(ByteBuffer message, Session session){
        // do something
    	
    	//Before doing anything.. add metadata to the cache.
    	//CacheService.addMetaDataToDateRecording(groupName, metadata);
    	
    	//different thread...
    	//just need the current price, high low open.
    	final ByteBuffer data = message;
    	ApplicationHelper.threadService.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				long t = System.currentTimeMillis();
				long currentIncrementedTime = DataFeed.incrementTime();
				ApplicationHelper.parseBuffer(data, String.valueOf(currentIncrementedTime),processingModels,parserModeLtp);
				System.out.println("Time taken:"+(System.currentTimeMillis()-t));
				
			}
			
		});
    }
    
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
    	if(latch!=null){
    		latch.countDown();
    	}
    	System.out.println("Connection closed...");
    }
    
    public void connect(){
        
//      SSLContext sslContext = null;
//      try {
//          sslContext = SSLContext.getInstance( "TLS" );
//          sslContext.init( null, null, null ); // will use java's default key and trust store which is sufficient unless you deal with self-signed certificates
//      } catch (NoSuchAlgorithmException e) {
//          e.printStackTrace();
//      } catch (KeyManagementException e) {
//          e.printStackTrace();
//      }        
      
      
      try {
          //URI serverEndpointUri = new URI("wss://websocket.kite.trade/?api_key=kitefront&user_id=DV4051&public_token=f88c4a16db7899fc39d2446098ea0c9a");
          URI serverEndpointUri = new URI(appConfig().getKeyValueConfigs().get("destination_url"));
          kiteSession = container.connectToServer(this, serverEndpointUri);
          
          //webSocketClient.connect(new WebSocketClientExample(), serverEndpointUri);
          
          

      } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
    
    public void closeSession(){
    	try {
			if(kiteSession!=null){
				kiteSession.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
