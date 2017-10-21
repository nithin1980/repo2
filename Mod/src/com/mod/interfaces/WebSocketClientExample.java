package com.mod.interfaces;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;

import javax.net.ssl.SSLContext;
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
import com.mod.process.models.ProcessingBlock;
import com.mod.support.ApplicationHelper;

@ClientEndpoint(configurator=WebsocketClientConfig.class)
public class WebSocketClientExample {
	
	private static CountDownLatch latch;
	protected static  WebSocketContainer container = ContainerProvider.getWebSocketContainer();
	
	//Quote Parser
	private StreamingQuoteParserModeFull streamingQuoteParser = new StreamingQuoteParserModeFull();

    @OnError
	public void onError(Session session,Throwable t) {
		System.out.println("errror");
	}

    @OnOpen
    public void onOpen(Session session) throws Exception {
    	//send initialising data from a config source....
    	session.getBasicRemote().sendText("{\"a\": \"subscribe\", \"v\": [408065]}");
        session.getBasicRemote().sendText("{\"a\": \"mode\", \"v\": [\"full\", [408065]]}");
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
				parseBuffer(data, "");
			}
		});
    }
    
    public static String bytesToAlphabeticString(byte[] bytes) {
    	StringBuilder builder = new StringBuilder();
    	for(int i=0;i<bytes.length;i++){
    		System.out.println(Character.toString((char)bytes[i]));
    		
    		
    		
    	}
        return builder.toString();
    }    

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        latch.countDown();
    }

    public static void main(String[] args) {
        latch = new CountDownLatch(1);
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance( "TLS" );
            sslContext.init( null, null, null ); // will use java's default key and trust store which is sufficient unless you deal with self-signed certificates
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }        
        
        

//        webSocketClient.set
//        webSocketClient.setWebSocketFactory( new DefaultSSLWebSocketClientFactory( sslContext ) );
       // ClientManager client = ClientManager.createClient();
        try {
            URI serverEndpointUri = new URI("wss://websocket.kite.trade/?api_key=kitefront&user_id=DV4051&public_token=f88c4a16db7899fc39d2446098ea0c9a");
            Session session = container.connectToServer(WebSocketClientExample.class, serverEndpointUri);
            //webSocketClient.connect(new WebSocketClientExample(), serverEndpointUri);
            
            latch.await();

        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	private void parseBuffer(ByteBuffer buffer, String time){
		// start parse Buffer array
		
		int start = buffer.position();
		int buffLen = buffer.capacity();
		if (buffLen == 1) {
			// HeartBeat
//			if(ZStreamingConfig.isHeartBitMsgPrintable()){
//				System.out.println("StreamingQuoteParserThread.parseBuffer(): WS HEARTBIT Byte");
//			}
		} else {
			// num of Packets
			int numPackets = buffer.getShort();
			if (numPackets == 0) {
				// Invalid Case: Zero Num of Packets - ignore
				System.out.println(
						"StreamingQuoteParserThread.parseBuffer(): ERROR: WS Byte numPackets is 0 in WS message, Ignoring !!!");
			} else {
				start += 2;
				//System.out.println("numPackets: " + numPackets);
				long currentIncrementedTime = DataFeed.incrementTime();
				for (int i = 0; i < numPackets; i++) {
					// each packet
					//System.out.println("packet num: " + i);
					int numBytes = buffer.getShort();
					if (numBytes != 0) {
						// Valid Number of Bytes
						start += 2;
						
						// packet structure
						byte[] pkt = new byte[numBytes];
						buffer.get(pkt, 0, numBytes);
						ByteBuffer pktBuffer = ByteBuffer.wrap(pkt);
						if (pktBuffer != null) {
							//parse quote packet buffer
							parseQuotePktBuffer(pktBuffer, String.valueOf(currentIncrementedTime));
							//put the time and collection of data here
							//CacheService.addDateRecordingCache(data);
							start += numBytes;
						} else {
							// Invalid Case: ByteBuffer could not wrap the bytes
							// - ignore
							System.out.println(
									"StreamingQuoteParserThread.parseBuffer(): ERROR: pktBuffer is null in WS message, Ignoring !!!");
						}
					} else {
						// Invalid Case: Zero Num of Bytes in packet - ignore
						System.out.println("StreamingQuoteParserThread.parseBuffer(): ERROR: numBytes is 0 in WS message packet[" + i + "], Ignoring !!!");
					}
				}
			}
		}
	}    
    
	/**
	 * CREATE TEST CASE TO FEED THIS DATA.....
	 * @param pktBuffer
	 * @param time
	 */
	private void parseQuotePktBuffer(ByteBuffer pktBuffer, String time){
		StreamingQuote streamingQuote = null;
		
		if(streamingQuoteParser != null){
			streamingQuote = streamingQuoteParser.parse(pktBuffer,time );
		}
		StreamingQuoteModeFull fullObject = (StreamingQuoteModeFull)streamingQuote;
		System.out.println("Quote: " + streamingQuote);
		System.out.println(fullObject.getLtp());
		System.out.println(fullObject.getInstrumentToken());
		
		
		
		CacheService.PRICE_LIST.put(Double.valueOf(fullObject.getInstrumentToken()), fullObject.getLtp().doubleValue());
		/**
		 * Need meta data in place before it is triggered.
		 */
		
		ProcessingBlock processingBlock = new ProcessingBlock();
		//----- do the processing....
		
	}
	
    
}
