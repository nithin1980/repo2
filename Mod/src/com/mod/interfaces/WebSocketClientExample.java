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
    	
    	session.getBasicRemote().sendText("{\"a\": \"subscribe\", \"v\": [408065]}");
        session.getBasicRemote().sendText("{\"a\": \"mode\", \"v\": [\"full\", [408065]]}");
    	System.out.println("connection opened");
    }

    @OnMessage
    public void onMessage(ByteBuffer message, Session session){
        // do something

    	parseBuffer(message, "");
    	
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
            URI serverEndpointUri = new URI("wss://websocket.kite.trade/?api_key=kitefront&user_id=DV4051&public_token=ce9a863f36d4e67347965c9bb60371a3");
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
							parseQuotePktBuffer(pktBuffer, time);
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
    
	private void parseQuotePktBuffer(ByteBuffer pktBuffer, String time){
		StreamingQuote streamingQuote = null;
		
		if(streamingQuoteParser != null){
			streamingQuote = streamingQuoteParser.parse(pktBuffer, time);
		}
		System.out.println("Quote: " + streamingQuote);
		
	}
	
    
}
