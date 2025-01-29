package com.mod.interfaces.kite;

import static com.mod.support.ApplicationConstant.KITE_ACCESS_TOKEN;
import static com.mod.support.ApplicationConstant.KITE_API_KEY;
import static com.mod.support.ApplicationConstant.KITE_PUBLIC_TOKEN;
import static com.mod.support.ApplicationConstant.KITE_USER_ID;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.mod.interfaces.KiteInterface;
import com.mod.process.models.CacheService;
import com.mod.support.ApplicationHelper;
import com.mod.support.ConfigData;
import com.mod.support.OpenHighLowSupport;
import com.mod.support.XMLParsing;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.SessionExpiryHook;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.ticker.KiteTicker;
import com.zerodhatech.ticker.OnConnect;
import com.zerodhatech.ticker.OnDisconnect;

public class LTPKiteAPIWebsocket {
	
	public static KiteTicker tickerProvider = null;
	private static KiteConnect kiteSdk = null;
	
	private static final ConfigData appConfig(){
		return ApplicationHelper.Application_Config_Cache.get("app");
	}

	public static void main(String[] args) {
		startConnecting();
	}
	
	public static void startConnecting() {
		
		initialSetup();

		//API key
		kiteSdk = new KiteConnect(CacheService.variables.get(KITE_API_KEY));

		// Set userId.
		kiteSdk.setUserId(CacheService.variables.get(KITE_USER_ID));

		kiteSdk.setAccessToken(CacheService.variables.get(KITE_ACCESS_TOKEN));
		kiteSdk.setPublicToken(CacheService.variables.get(KITE_PUBLIC_TOKEN));
		
		kiteSdk.setSessionExpiryHook(new SessionExpiryHook() {
		    @Override
		    public void sessionExpired() {
		        System.out.println("session expired");                    
		    }
		});
		
		
		tickerProvider = new KiteTicker(kiteSdk.getAccessToken(), kiteSdk.getApiKey());
		System.out.println("New ticker instance created..");

		tickerProvider.setOnDisconnectedListener(new OnDisconnect() {
			
			@Override
			public void onDisconnected() {
				// Reconnect, if disconnected.
				tickerProvider.connect();
				System.out.println("Disconnected....");
				
			}
		});
		
		/***
		 * This is to send more subscription...or change 
		 * the mode type for a ticket
		 */
		
		tickerProvider.setOnConnectedListener(new OnConnect() {
			
			@Override
			public void onConnected() {
				
				
				String[] subList = appConfig().getKeyValueConfigs().get("kite_ltp_subscribe").split("\\,");
				
				ArrayList<Long> args = new ArrayList<Long>();
				for(int i=0;i<subList.length;i++) {
					args.add(Long.valueOf(subList[i]));
				}
				
				// TODO Auto-generated method stub
				tickerProvider.setMode(args, KiteTicker.modeLTP);
				tickerProvider.subscribe(args);
				
				System.out.println("It is connected...and subscribed:"+new Date());
			}
		});
		
		
		tickerProvider.setOnTickerArrivalListener(new KiteLTPOnTickerArrivalListener());
		
		
        tickerProvider.setTryReconnection(true);
        //maximum retries and should be greater than 0
        try {
			tickerProvider.setMaximumRetries(10);
			//set maximum retry interval in seconds
			tickerProvider.setMaximumRetryInterval(30);
		} catch (KiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        tickerProvider.connect();
        
        
        boolean isConnected = tickerProvider.isConnectionOpen();
        System.out.println(isConnected);
		
		while(true) {
			
		}
		
		
	}
	
	public static KiteConnect getSDK() {
		
		synchronized (kiteSdk) {
			return kiteSdk;
		}
	}
	
	public static void initialSetup() {
		ApplicationHelper.botInitialSetup();
	}

}
