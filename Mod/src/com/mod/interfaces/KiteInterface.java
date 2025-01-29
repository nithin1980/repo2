package com.mod.interfaces;

import static com.mod.support.ApplicationConstant.KITE_ACCESS_TOKEN;
import static com.mod.support.ApplicationConstant.KITE_API_KEY;
import static com.mod.support.ApplicationHelper.getObjectMapper;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import com.mod.enums.EnumMinuteType;
import com.mod.enums.KiteDataConstant;
import com.mod.interfaces.kite.LTPKiteAPIWebsocket;
import com.mod.objects.KitePositionDataLayer1;
import com.mod.objects.KitePositionDataLayer2;
import com.mod.process.models.CacheService;
import com.mod.support.ApplicationHelper;
import com.mod.support.Candle;
import com.mod.support.ConfigData;
import com.mod.support.KiteCandleData;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;
import com.zerodhatech.models.Position;

public class KiteInterface implements SystemInterface {
	
	private static final KiteInterface kiteinterface = new KiteInterface();
	private static final String FAILED="FAILED";
	private KiteInterface() {
		// TODO Auto-generated constructor stub
	}
	
	public static KiteInterface getInstance() {
		return kiteinterface;
	}
	
	public ConfigData appConfig(){
		return ApplicationHelper.Application_Config_Cache.get("app");
	}
	
	

	@Override
	public HoldingsQueryResponse queryCurrentHoldings(HoldingsQueryRequest queryPositionObject) {
		// TODO Auto-generated method stub
		
		String url="https://api.kite.trade/portfolio/holdings";
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(url);
		get.addHeader("X-Kite-Version", "3");
		
		String authorisation = "token "+CacheService.variables.get(KITE_API_KEY)+":"+CacheService.variables.get(KITE_ACCESS_TOKEN);
		get.addHeader("Authorization", authorisation);
		
		get.addHeader("Connection", "keep-alive");
		get.addHeader("Accept-Encoding", "gzip, deflate, br");
		get.addHeader("Accept", "application/json, text/plain, */*");
		StringBuffer result = new StringBuffer();
		
		try {
			//post.setEntity(new UrlEncodedFormEntity(urlParameters));
			//post.setEntity(requestEntity);
			long t = System.currentTimeMillis();
			HttpResponse response = client.execute(get);
			
			BufferedReader rd = new BufferedReader(
			        new InputStreamReader(response.getEntity().getContent()));

			
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			System.out.println("Time taken for holdings data:"+(System.currentTimeMillis()-t));
			System.out.println(result);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		KiteHoldingsQueryResponse response = null;
		
		try {
			response = ApplicationHelper.getObjectMapper().readValue(result.toString(), KiteHoldingsQueryResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return response;
	}

	@Override
	public PostionQueryResponse queryCurrentPosition(QueryPositionObject queryPositionObject) {
		// TODO Auto-generated method stub
		
		KitePositionQueryResponse response = new KitePositionQueryResponse();
		
		try {
			Map<String, List<Position>> positionData =  ApplicationHelper.getKiteSDK().getPositions();
			
			response = populatePositionData(positionData);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			response.setStatus("FAILED");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			response.setStatus("FAILED");
		} catch (KiteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			response.setStatus("FAILED");
		}
		
		
		
		return response;
	}
	
	public KitePositionQueryResponse populatePositionData(Map<String, List<Position>> positionData) {
		String net="net";
		String day="day";
		
		List<Position> dayPos =  positionData.get(day);
		List<Position> netPos =  positionData.get(net);
		
		KitePositionQueryResponse response = new KitePositionQueryResponse();
		response.setData(new KitePositionDataLayer1());
		
		List<KitePositionDataLayer2> dayPosResponse = new ArrayList<KitePositionDataLayer2>();
		List<KitePositionDataLayer2> netPosResponse = new ArrayList<KitePositionDataLayer2>();
		
		
		if(dayPos!=null && dayPos.size()>0) {
			Iterator<Position> itr =  dayPos.iterator();
			
			while(itr.hasNext()) {
				
				Position pos = itr.next();
				
				KitePositionDataLayer2 dtLayer2 = new KitePositionDataLayer2();
				dtLayer2.setAverage_price(pos.averagePrice);
				dtLayer2.setBuy_price(pos.buyPrice);
				dtLayer2.setBuy_quantity(pos.buyQuantity);
				dtLayer2.setBuy_value(pos.buyValue);
				dtLayer2.setClose_price(pos.closePrice);
				dtLayer2.setDay_buy_price(pos.dayBuyPrice);
				dtLayer2.setDay_buy_quantity(pos.dayBuyQuantity);
				dtLayer2.setDay_buy_value(pos.dayBuyValue);
				dtLayer2.setDay_sell_price(pos.daySellPrice);
				dtLayer2.setDay_sell_quantity(pos.daySellQuantity);
				dtLayer2.setDay_sell_value(pos.daySellValue);
				dtLayer2.setExchange(pos.exchange);
				dtLayer2.setInstrument_token(pos.instrumentToken);
				dtLayer2.setMultiplier(pos.multiplier);
				
				dtLayer2.setProduct(pos.product);
				//dtLayer2.setQuantity(pos.qu);
				dtLayer2.setSell_price(pos.sellPrice);
				dtLayer2.setSell_quantity(pos.sellQuantity);
				dtLayer2.setSell_value(pos.sellValue);
				dtLayer2.setTradingsymbol(pos.tradingSymbol);
				dtLayer2.setValue(pos.value);
				
				dtLayer2.setNetQuantity(pos.netQuantity);
				dtLayer2.setNetValue(pos.netValue);
				dayPosResponse.add(dtLayer2);
				
			}
			
			response.getData().setDay(dayPosResponse.toArray(new KitePositionDataLayer2[dayPosResponse.size()]));
			
		}
		
		if(netPos!=null && netPos.size()>0) {
			Iterator<Position> itr =  netPos.iterator();
			
			while(itr.hasNext()) {
				
				Position pos = itr.next();
				
				KitePositionDataLayer2 dtLayer2 = new KitePositionDataLayer2();
				dtLayer2.setAverage_price(pos.averagePrice);
				dtLayer2.setBuy_price(pos.buyPrice);
				dtLayer2.setBuy_quantity(pos.buyQuantity);
				dtLayer2.setBuy_value(pos.buyValue);
				dtLayer2.setClose_price(pos.closePrice);
				dtLayer2.setDay_buy_price(pos.dayBuyPrice);
				dtLayer2.setDay_buy_quantity(pos.dayBuyQuantity);
				dtLayer2.setDay_buy_value(pos.dayBuyValue);
				dtLayer2.setDay_sell_price(pos.daySellPrice);
				dtLayer2.setDay_sell_quantity(pos.daySellQuantity);
				dtLayer2.setDay_sell_value(pos.daySellValue);
				dtLayer2.setExchange(pos.exchange);
				dtLayer2.setInstrument_token(pos.instrumentToken);
				dtLayer2.setMultiplier(pos.multiplier);
				
				dtLayer2.setProduct(pos.product);
				//dtLayer2.setQuantity(pos.qu);
				dtLayer2.setSell_price(pos.sellPrice);
				dtLayer2.setSell_quantity(pos.sellQuantity);
				dtLayer2.setSell_value(pos.sellValue);
				dtLayer2.setTradingsymbol(pos.tradingSymbol);
				dtLayer2.setValue(pos.value);
				
				dtLayer2.setNetQuantity(pos.netQuantity);
				dtLayer2.setNetValue(pos.netValue);
				netPosResponse.add(dtLayer2);
			}
			
			response.getData().setNet(netPosResponse.toArray(new KitePositionDataLayer2[netPosResponse.size()]));
			
		}
		
		
		return response;
		
	}
	
	
	public List<Candle> getCandleData(String stockKey,EnumMinuteType minuteType) {
		// TODO Auto-generated method stub
		
		//4 day before to cover the weekend issue as well
		
		String startDate = new Date(System.currentTimeMillis()-(457400*1000)).toString();
		String endDate = new Date(System.currentTimeMillis()).toString();
		
		if(stockKey.contains(".")) {
			stockKey = stockKey.split(".")[0];
		}
		
		String url="https://api.kite.trade/instruments/historical/"+stockKey+"/"+minuteType.getMinuteString()+"?from="+startDate+"&to="+endDate;
		
		System.out.println("URL for candle data:"+url);
		
		
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(url);
		get.addHeader("X-Kite-Version", "3");
		
		String authorisation = "token "+CacheService.variables.get(KITE_API_KEY)+":"+CacheService.variables.get(KITE_ACCESS_TOKEN);
		get.addHeader("Authorization", authorisation);
		
		get.addHeader("Connection", "keep-alive");
		get.addHeader("Accept-Encoding", "gzip, deflate, br");
		get.addHeader("Accept", "application/json, text/plain, */*");
		StringBuffer result = new StringBuffer();
		
		List<Candle> candles=null;
		
		try {
			//post.setEntity(new UrlEncodedFormEntity(urlParameters));
			//post.setEntity(requestEntity);
			long t = System.currentTimeMillis();
			HttpResponse response = client.execute(get);
			
			BufferedReader rd = new BufferedReader(
			        new InputStreamReader(response.getEntity().getContent()));

			
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			
			System.out.println(result);
			KiteCandleData candleData =  getObjectMapper().readValue(result.toString(), KiteCandleData.class); 
			candles = candleData.getData().candleInformation();
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return candles;
	}
	
	
	public CreateOrderResponse createOrder(CreateOrderRequest orderRequest) {
		// TODO Auto-generated method stub
		
		KiteCreateOrderRequest request = (KiteCreateOrderRequest)orderRequest;
		
		
		
		
		
//		String url="https://api.kite.trade/orders/"+request.getVariety();
		
	//	System.out.println("URL for order:"+url+" for "+request.getTradingSymbol());
		
		assertNotNull(request.getExchange());
		assertNotNull(request.getVariety());
		assertNotNull(request.getTradingSymbol());
		assertNotNull(request.getTransactionType());
		assertNotNull(request.getOrderType());
    	assertNotEquals(0,request.getQuantity());
		assertNotNull(request.getProduct());
		assertNotNull(request.getValidity());

		if(KiteDataConstant.Order_Type_sl.equals(request.getOrderType()) || 
				KiteDataConstant.Order_Type_sl_m.equals(request.getOrderType())){
			assertNotNull(request.getSlTriggerPrice());
		}

		assertNotNull(request.getPrice());

		
		
		
		//for limit order, which can act like SL
		
		
		
		OrderParams params = new OrderParams();
		params.exchange = request.getExchange();
		params.tradingsymbol=request.getTradingSymbol();
		params.transactionType=request.getTransactionType();
		params.orderType=request.getOrderType();
		params.quantity = request.getQuantity();
		params.product=request.getProduct();
		params.validity=request.getValidity();
		
		
		if(!KiteDataConstant.Order_Type_sl_m.equals(request.getOrderType())) {
			params.price=Double.valueOf(request.getPrice());
		}
		
		
		
		if(KiteDataConstant.Order_Type_sl.equals(request.getOrderType()) || 
				KiteDataConstant.Order_Type_sl_m.equals(request.getOrderType())){
				params.triggerPrice= Double.valueOf(request.getSlTriggerPrice());
				
		}

		System.out.println("API Order request:{Exhcnage:"+params.exchange+",-Order Type:,"+request.getOrderType()+",-Price:,"+request.getPrice()+",-Product:,"
				+request.getProduct()+",-Quantity:,"+request.getQuantity()+",-SL Trigger Price:,"+request.getSlTriggerPrice()+",- Trading Symbol:,"
				+params.tradingsymbol+",-,Transaction type:"+request.getTransactionType()+",-,Validaity: "+request.getValidity()+",-,Variety:"+request.getVariety());
		
		KiteCreateOrderResponse orderResponse = new KiteCreateOrderResponse();
		orderResponse.setData(new KiteCreateOrderResponseLayer2());
		Order order = null;
		
		try {
			order = ApplicationHelper.getKiteSDK().placeOrder(params, request.getVariety());
			orderResponse.setStatus(order.status);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			orderResponse.setStatus(FAILED);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			orderResponse.setStatus(FAILED);
		} catch (KiteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			orderResponse.setStatus(FAILED);
		}
		
		if(!FAILED.equalsIgnoreCase(orderResponse.getStatus())) {
			orderResponse.getData().setOrderId(Long.valueOf(order.orderId));
		}
		
		
		
		
		
		
		
		/**
		 * Back up
		 */
		
//		HttpClient client = HttpClientBuilder.create().build();
//		HttpPost post = new HttpPost(url);
//		post.addHeader("X-Kite-Version", "3");
//		
//		String authorisation = "token "+CacheService.variables.get(KITE_API_KEY)+":"+CacheService.variables.get(KITE_ACCESS_TOKEN);
//		post.addHeader("Authorization", authorisation);
//		
//		post.addHeader("Connection", "keep-alive");
//		post.addHeader("Accept-Encoding", "gzip, deflate, br");
//		post.addHeader("Accept", "*/*");
//		post.addHeader("Content-Type", "application/x-www-form-urlencoded");
//		
//		
//		StringBuffer result = new StringBuffer();
//		
//		KiteCreateOrderResponse orderResponse = null;
//		
//		try {
//			//post.setEntity(new UrlEncodedFormEntity(urlParameters));
//			//post.setEntity(requestEntity);
//			long t = System.currentTimeMillis();
//			
//					
//			List<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();	
//			values.add(new BasicNameValuePair("tradingsymbol", request.getTradingSymbol()));
//			values.add(new BasicNameValuePair("exchange", request.getExchange()));
//			values.add(new BasicNameValuePair("transaction_type", request.getTransactionType()));
//			values.add(new BasicNameValuePair("order_type", request.getOrderType()));
//			values.add(new BasicNameValuePair("quantity",String.valueOf(request.getQuantity())));
//			values.add(new BasicNameValuePair("product", request.getProduct()));
//			values.add(new BasicNameValuePair("validity", request.getValidity()));
//			
//			//for SL trigger
//			if(KiteDataConstant.Order_Type_sl.equals(request.getOrderType()) || 
//				KiteDataConstant.Order_Type_sl_m.equals(request.getOrderType())){
//				
//				values.add(new BasicNameValuePair("trigger_price", request.getSlTriggerPrice()));
//			}
//			
//			//for limit order, which can act like SL
//			values.add(new BasicNameValuePair("price", request.getPrice()));
//			
//					
//			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(values);
//			post.setEntity(entity);
//			
//			HttpResponse response = client.execute(post);
//			
//			BufferedReader rd = new BufferedReader(
//			        new InputStreamReader(response.getEntity().getContent()));
//
//			
//			String line = "";
//			while ((line = rd.readLine()) != null) {
//				result.append(line);
//			}
//			
//			System.out.println(result);
//			orderResponse =  getObjectMapper().readValue(result.toString(), KiteCreateOrderResponse.class); 
//			
//			
//			
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		
		return orderResponse;
	}
	
	public UpdateOrderRespone updateOrder(UpdateOrderRequest updateRequest) {
		// TODO Auto-generated method stub
		
		KiteUpdateOrderRequest request = (KiteUpdateOrderRequest)updateRequest;
		
		
		assertNotEquals(0, request.getOrderId());
		assertNotNull(request.getExchange());
		assertNotNull(request.getVariety());
		assertNotNull(request.getTradingSymbol());
		assertNotNull(request.getTransactionType());
		assertNotNull(request.getOrderType());
		assertNotEquals(0,request.getQuantity());
		assertNotNull(request.getProduct());
		assertNotNull(request.getValidity());
		
		if(KiteDataConstant.Order_Type_sl.equals(request.getOrderType()) || 
				KiteDataConstant.Order_Type_sl_m.equals(request.getOrderType())){
			assertNotNull(request.getSlTriggerPrice());
		}
		assertNotNull(request.getPrice());

		
		
		String url="https://api.kite.trade/orders/"+request.getVariety()+"/"+request.getOrderId();
		
		System.out.println("URL for order:"+url+" for "+request.getTradingSymbol());

		System.out.println("API Update Order request:{"+request.getExchange()+",-,"+request.getOrderType()+",-,"+request.getPrice()+",-,"
				+request.getProduct()+",-,"+request.getQuantity()+",-,"+request.getSlTriggerPrice()+",-,"
				+request.getTradingSymbol()+",-,"+request.getTransactionType()+",-,"+request.getValidity()+",-,"+request.getVariety()+",-,"+request.getOrderId());

		
		OrderParams params = new OrderParams();
		params.exchange = request.getExchange();
		params.tradingsymbol=request.getTradingSymbol();
		params.transactionType=request.getTransactionType();
		params.orderType=request.getOrderType();
		params.quantity = request.getQuantity();
		params.product=request.getProduct();
		params.validity=request.getValidity();

		if(!KiteDataConstant.Order_Type_sl_m.equals(request.getOrderType())) {
			params.price=Double.valueOf(request.getPrice());
		}
		
		
		
		if(KiteDataConstant.Order_Type_sl.equals(request.getOrderType()) || 
				KiteDataConstant.Order_Type_sl_m.equals(request.getOrderType())){
				params.triggerPrice= Double.valueOf(request.getSlTriggerPrice());
				
		}

		System.out.println("API Order request:{Exhcnage:"+params.exchange+",-Order Type:,"+request.getOrderType()+",-Price:,"+request.getPrice()+",-Product:,"
				+request.getProduct()+",-Quantity:,"+request.getQuantity()+",-SL Trigger Price:,"+request.getSlTriggerPrice()+",- Trading Symbol:,"
				+params.tradingsymbol+",-,Transaction type:"+request.getTransactionType()+",-,Validaity: "+request.getValidity()+",-,Variety:"+request.getVariety()+",-,Order id:"+request.getOrderId());
		
		
		KiteUpdateOrderRespone orderResponse = new KiteUpdateOrderRespone();
		orderResponse.setData(new KiteUpdateOrderResponseLayer2());
		Order order = null;
		
		try {
			order = ApplicationHelper.getKiteSDK().modifyOrder(String.valueOf(request.getOrderId()), params, request.getVariety());
			orderResponse.setStatus(order.status);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			orderResponse.setStatus(FAILED);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			orderResponse.setStatus(FAILED);
		} catch (KiteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			orderResponse.setStatus(FAILED);
		}
		
		if(!FAILED.equalsIgnoreCase(orderResponse.getStatus())) {
			orderResponse.getData().setOrderId(Long.valueOf(order.orderId));
		}
		
		

		
		/**
		 * Backup
		 */
		
//		HttpClient client = HttpClientBuilder.create().build();
//		HttpPut put = new HttpPut(url);
//		put.addHeader("X-Kite-Version", "3");
//		
//		String authorisation = "token "+CacheService.variables.get(KITE_API_KEY)+":"+CacheService.variables.get(KITE_ACCESS_TOKEN);
//		put.addHeader("Authorization", authorisation);
//		
//		put.addHeader("Connection", "keep-alive");
//		put.addHeader("Accept-Encoding", "gzip, deflate, br");
//		put.addHeader("Accept", "*/*");
//		put.addHeader("Content-Type", "application/x-www-form-urlencoded");
//		
//		
//		StringBuffer result = new StringBuffer();
//		
//		
//		
//		try {
//			//post.setEntity(new UrlEncodedFormEntity(urlParameters));
//			//post.setEntity(requestEntity);
//			long t = System.currentTimeMillis();
//			
//					
//			List<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();	
//			values.add(new BasicNameValuePair("order_type", request.getOrderType()));
//			values.add(new BasicNameValuePair("quantity",String.valueOf(request.getQuantity())));
//			values.add(new BasicNameValuePair("validity", request.getValidity()));
//			
//			//for SL trigger
//			if(KiteDataConstant.Order_Type_sl.equals(request.getOrderType()) || 
//					KiteDataConstant.Order_Type_sl_m.equals(request.getOrderType())){
//					
//					values.add(new BasicNameValuePair("trigger_price", request.getSlTriggerPrice()));
//			}
//			//for limit order, which can act like SL
//			values.add(new BasicNameValuePair("price", request.getPrice()));
//			
//					
//			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(values);
//			put.setEntity(entity);
//			
//			HttpResponse response = client.execute(put);
//			
//			BufferedReader rd = new BufferedReader(
//			        new InputStreamReader(response.getEntity().getContent()));
//
//			
//			String line = "";
//			while ((line = rd.readLine()) != null) {
//				result.append(line);
//			}
//			
//			System.out.println(result);
//			orderResponse =  getObjectMapper().readValue(result.toString(), KiteUpdateOrderRespone.class); 
//			
//			
//			
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		
		return orderResponse;
	}
	
	
	@Override
	public void order(OrderInterfaceObject interfaceObject) {
		
		//KiteOrderObject kiteOrderObject = (KiteOrderObject)interfaceObject;
		Map<String, String> keyvalues = appConfig().getKeyValueConfigs();
		String url = keyvalues.get("kite_order_url");//  "https://kite.zerodha.com/api/orders";
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		
		//http://pp.axa-travel-insurance.com/AxaDE_B2B/login;jsessionid=FA1A27AD9305056ABB95647248D344C4.TI7A
		
		
		post.addHeader("Host", keyvalues.get("Host"));
		post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0");
		post.addHeader("Accept", "application/json, text/plain, */*");
		post.addHeader("Accept-Language", "en-US,en;q=0.5");
		post.addHeader("Accept-Encoding", "gzip, deflate, br");
		post.addHeader("Cache-Control", "no-cache");
		post.addHeader("Pragma", "no-cache");
		post.addHeader("If-Modified-Since", "0");
		post.addHeader("Content-Type", keyvalues.get("Content-Type"));
		post.addHeader("Referer", keyvalues.get("order_referrer"));
//		post.addHeader("Content-Length", "311");
//		
		post.addHeader("Cookie",keyvalues.get("Cookie"));
		post.addHeader("DNT", "1");
		post.addHeader("Connection", "keep-alive");
		
		String json="{\"exchange\":\"NFO\",\"tradingsymbol\":\"NIFTY17OCT10500CE\",\"transaction_type\":\"BUY\",\"order_type\":\"MARKET\",\"quantity\":\"75\",\"price\":\"0\",\"product\":\"NRML\",\"validity\":\"DAY\",\"disclosed_quantity\":\"0\",\"trigger_price\":\"0\",\"squareoff_value\":\"0\",\"squareoff\":\"0\",\"stoploss_value\":\"0\",\"stoploss\":\"0\",\"trailing_stoploss\":\"0\",\"variety\":\"amo\",\"client_id\":\"DV4051\"}";
		StringEntity requestEntity  =  new StringEntity(json,ContentType.APPLICATION_JSON);
		
//		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
//		urlParameters.add(new BasicNameValuePair("__fp", "h_HA5klazKw="));
//		urlParameters.add(new BasicNameValuePair("_sourcePage", "yfp1XQoFJkmHYzQ4nobxRuyKn_CNIWySzRlTyPTQI4Q="));
//		urlParameters.add(new BasicNameValuePair("login", "Einloggen"));
//		urlParameters.add(new BasicNameValuePair("password", "dertererer"));
//		urlParameters.add(new BasicNameValuePair("username", "test"));

		/*
		try {
			//post.setEntity(new UrlEncodedFormEntity(urlParameters));
			post.setEntity(requestEntity);
			long t = System.currentTimeMillis();
			HttpResponse response = client.execute(post);
			
			BufferedReader rd = new BufferedReader(
			        new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			System.out.println(System.currentTimeMillis()-t);
			System.out.println(result);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		//WebSocketClientExample.main(null);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public static void main(String[] args) {
		new KiteInterface().order(null);
	}
}
