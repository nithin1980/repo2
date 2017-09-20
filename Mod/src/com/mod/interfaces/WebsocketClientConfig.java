package com.mod.interfaces;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpointConfig.Configurator;
import javax.websocket.HandshakeResponse;

public class WebsocketClientConfig extends Configurator {
	
	static volatile boolean called = false;

    @Override
    public void beforeRequest(Map<String, List<String>> headers) {
        called = true;
        headers.put("Accept-Encoding", Arrays.asList("gzip, deflate, br"));
        headers.put("Accept-Language", Arrays.asList("en-US,en;q=0.8"));
        headers.put("Cache-Control", Arrays.asList("no-cache"));
        headers.put("Connection", Arrays.asList("Upgrade"));
        headers.put("Host", Arrays.asList("websocket.kite.trade"));
        headers.put("Origin", Arrays.asList("https://kite.zerodha.com"));
        headers.put("Pragma", Arrays.asList("no-cache"));
        headers.put("Sec-WebSocket-Extensions", Arrays.asList("permessage-deflate; client_max_window_bits"));
        
        headers.put("Sec-WebSocket-Key", Arrays.asList("QspFwfcdv1cL7c27w85xXA=="));
        headers.put("Sec-WebSocket-Version", Arrays.asList("13"));
        headers.put("Upgrade", Arrays.asList("websocket"));
        headers.put("User-Agent", Arrays.asList("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36"));
        
    }

    @Override
    public void afterResponse(HandshakeResponse handshakeResponse) {
        final Map<String, List<String>> headers = handshakeResponse.getHeaders();
        System.out.println(headers);
        
//        assertEquals(HEADER_VALUE[0], headers.get(HEADER_NAME).get(0));
//        assertEquals(HEADER_VALUE[1], headers.get(HEADER_NAME).get(1));
//        assertEquals(HEADER_VALUE[2], headers.get(HEADER_NAME).get(2));
//        assertEquals("myOrigin", headers.get("origin").get(0));
    }
    
    

}
