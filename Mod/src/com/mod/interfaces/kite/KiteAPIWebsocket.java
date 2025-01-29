package com.mod.interfaces.kite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import static com.mod.support.ApplicationConstant.*;

import com.mod.enums.EnumMinuteType;
import com.mod.interfaces.KiteHoldingsDataLayer1;
import com.mod.interfaces.KiteHoldingsQueryResponse;
import com.mod.interfaces.KiteInterface;
import com.mod.interfaces.KitePositionQueryResponse;
import com.mod.interfaces.PostionQueryResponse;
import com.mod.interfaces.SystemInterface;
import com.mod.objects.DayCandles;
import com.mod.objects.KitePositionDataLayer2;
import com.mod.objects.EnumPositionStatus;
import com.mod.objects.PositionalData;
import com.mod.process.models.BollingerBandModel1;
import com.mod.process.models.CacheService;
import com.mod.support.ApplicationHelper;
import com.mod.support.Candle;
import com.mod.support.ConfigData;
import com.mod.support.XMLParsing;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.SessionExpiryHook;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Depth;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.Quote;
import com.zerodhatech.models.Tick;
import com.zerodhatech.models.User;
import com.zerodhatech.ticker.KiteTicker;
import com.zerodhatech.ticker.OnConnect;
import com.zerodhatech.ticker.OnDisconnect;
import com.zerodhatech.ticker.OnOrderUpdate;
import com.zerodhatech.ticker.OnTicks;

public class KiteAPIWebsocket {
	
	public static KiteTicker tickerProvider = null;
	
	private static final ConfigData appConfig(){
		return ApplicationHelper.Application_Config_Cache.get("app");
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		initialSetup();
		
		
		
		//API key
		KiteConnect kiteSdk = new KiteConnect(CacheService.variables.get(KITE_API_KEY));

		// Set userId.
		kiteSdk.setUserId(CacheService.variables.get(KITE_USER_ID));

		/* First you should get request_token, public_token using kitconnect login and then use request_token, public_token, api_secret to make any kiteconnect api call.
		Get login url. Use this url in webview to login user, after authenticating user you will get requestToken. Use the same to get accessToken. */
//		String url = kiteSdk.getLoginURL();
//		System.out.println(url);
//		User user = null;
//		//
//		
//		try {
//			//request token & API secret
//			 user =  kiteSdk.generateSession("fz8KoH2HA8qCH1CV0uSpJn2HTEpGoJBj", "3yopb6w9ge1nlts97u8f4qzhdkjq901e");
//			 System.out.println("User:"+user);
//		} catch (JSONException | IOException | KiteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("Access token:"+user.accessToken);
//		System.out.println("Public token:"+user.publicToken);
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
				
				
				String[] subList = appConfig().getKeyValueConfigs().get("kite_subscribe").split("\\,");
				
				ArrayList<Long> args = new ArrayList<Long>();
				for(int i=0;i<subList.length;i++) {
					args.add(Long.valueOf(subList[i]));
				}
				
				// TODO Auto-generated method stub
				tickerProvider.setMode(args, KiteTicker.modeFull);
				tickerProvider.subscribe(args);
				
				System.out.println("It is connected...and subscribed");
			}
		});
		
		
		tickerProvider.setOnTickerArrivalListener(new KiteOnFullTickerArrivalListener());
		tickerProvider.setOnOrderUpdateListener(new KiteOnOrderUpdateListener());
		
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

//		ArrayList<Long> scripts = new ArrayList<Long>();
//		scripts.add(Long.valueOf(260105));
//		
//		// TODO Auto-generated method stub
//		tickerProvider.subscribe(scripts);
//		tickerProvider.setMode(scripts, KiteTicker.modeFull);     
		
//        tickerProvider.unsubscribe(tokens);
//
//        // After using com.zerodhatech.com.zerodhatech.ticker, close websocket connection.
//        tickerProvider.disconnect();
		
		while(true) {
			
		}
	}
	
	
	void marketDepth(KiteConnect kiteSdk) {
		
		try {
			Map<String, Quote> quote =  kiteSdk.getQuote(null);
			
			List<Depth> bid = quote.get(null).depth.buy;
			List<Depth> sell = quote.get(null).depth.sell;
			
			int quantity = sell.get(0).getQuantity();
			double price = sell.get(0).getPrice();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	
	public static void initialSetup() {
		
		
		ApplicationHelper.botInitialSetup();
		

		
		
		
		if(true) {
			System.out.println("Adding TEST position");
			//addTestPosition();
		}
		
		
		/**
		 * Adding Bollinger band data
		 */
//		Double[] bbClosingData = {348.70,347.79,383.20,393.81,405.23,399.22,403.43,407.34,402.83,406.24,408.54,408.24,419.67,407.34,421.17,406.44,419.37,412.75,404.3,408.00};
//
//		DayCandles trackingCandles = new DayCandles(new Candle(), new Candle());
//		trackingCandles.getBB_Close_Records().addAll(Arrays.asList(bbClosingData));
//		BollingerBandModel1.trackingCandles.put(CacheService.BN_KEY, trackingCandles);
		
		
		/**
		 * disabled entire block for Bollinger band.
		 */

//		{ 
//		List<Candle> candles = kiteInterface.getCandleData(CacheService.BN_KEY.toString(),EnumMinuteType.Fiveminute );
//		DayCandles trackingCandles = new DayCandles(new Candle(), new Candle());
//		add5MntClosingData(candles, CacheService.BN_KEY, trackingCandles);
//		
//		BollingerBandModel1.trackingCandles.put(CacheService.BN_KEY, trackingCandles);
//
//		candles = kiteInterface.getCandleData(CacheService.NF_KEY.toString(),EnumMinuteType.Fiveminute );
//		trackingCandles = new DayCandles(new Candle(), new Candle());
//		add5MntClosingData(candles, CacheService.NF_KEY, trackingCandles);
//		
//		BollingerBandModel1.trackingCandles.put(CacheService.NF_KEY, trackingCandles);
//		}
		
		
		
	}
	
	private static void addStatic5MntCandles(Double[] data, Double stockkey, DayCandles trackingCandles) {
		
	}
	
	private static void add5MntClosingData(List<Candle> candles, Double stockkey, DayCandles trackingCandles) {
		
		int size = candles.size();
		
		if(size<22) {
			throw new RuntimeException("Doesn't have 22 5 minutes data for BB calculation:"+stockkey);
		}
		
		for(int i=(size-22);i<size;i++) {
			 trackingCandles.getBB_Close_Records().add(candles.get(i).getClose());
		}
		
	}
	
	private static void addTestPosition() {

		PositionalData data = new PositionalData();
		data.setBuyPrice(555.00);
		//position 1
		data.setKey(Long.valueOf(4632577));
		data.setStatus(EnumPositionStatus.InPoistionLong);
		data.setCount(1);
		
		
		CacheService.positionalData.add(data);
		data = new PositionalData();
		data.setBuyPrice(1159.50);
		//position 1
		data.setKey(1102337);
		data.setStatus(EnumPositionStatus.InPoistionLong);
		data.setCount(1);
		
		
		CacheService.positionalData.add(data);

		data = new PositionalData();
		data.setBuyPrice(2546.50);
		//position 1
		data.setKey(837889);
		data.setStatus(EnumPositionStatus.InPoistionLong);
		data.setCount(1);

		CacheService.positionalData.add(data);

		data = new PositionalData();
		data.setBuyPrice(36675);
		//position 1
		data.setKey(11176962);
		data.setStatus(EnumPositionStatus.InPoistionLong);
		data.setCount(1);

		CacheService.positionalData.add(data);

	}
}
