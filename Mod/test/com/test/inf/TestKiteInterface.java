package com.test.inf;

import static com.mod.support.ApplicationHelper.getObjectMapper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import com.mod.enums.EnumMinuteType;
import com.mod.enums.KiteDataConstant;
import com.mod.interfaces.CreateOrderRequest;
import com.mod.interfaces.CreateOrderResponse;
import com.mod.interfaces.HoldingsQueryRequest;
import com.mod.interfaces.HoldingsQueryResponse;
import com.mod.interfaces.KiteCreateOrderRequest;
import com.mod.interfaces.KiteCreateOrderResponse;
import com.mod.interfaces.KiteCreateOrderResponseLayer2;
import com.mod.interfaces.KiteUpdateOrderRequest;
import com.mod.interfaces.KiteUpdateOrderRespone;
import com.mod.interfaces.KiteUpdateOrderResponseLayer2;
import com.mod.interfaces.OrderInterfaceObject;
import com.mod.interfaces.PostionQueryResponse;
import com.mod.interfaces.QueryPositionObject;
import com.mod.interfaces.SystemInterface;
import com.mod.interfaces.UpdateOrderRequest;
import com.mod.interfaces.UpdateOrderRespone;
import com.mod.support.Candle;
import com.mod.support.KiteCandleData;

public class TestKiteInterface implements SystemInterface {
	
	private static final TestKiteInterface kiteinterface = new TestKiteInterface();
	
	public static Map<Long, String> orderBook = new HashMap<Long, String>();
	
	private TestKiteInterface() {
		// TODO Auto-generated constructor stub
	}
	
	public static TestKiteInterface getInstance() {
		return kiteinterface;
	}
	
	static int responseCount=0;

	@Override
	public void order(OrderInterfaceObject interfaceObject) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public HoldingsQueryResponse queryCurrentHoldings(HoldingsQueryRequest queryPositionObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PostionQueryResponse queryCurrentPosition(QueryPositionObject queryPositionObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Candle> getCandleData(String stockKey, EnumMinuteType minuteType) {
		// TODO Auto-generated method stub
		
		List<Candle> candles=null;
		
		System.out.println("response count:"+responseCount);
		
		String result = "{\"status\": \"success\", \"data\": { \"candles\":[[\"2022-05-13T09:15:00+0530\",123.10,124.10,122.50,123.50,0],[\"2022-05-13T09:20:00+0530\",124.10,125.10,123.10,124.50,0],[\"2022-05-13T09:25:00+0530\",125.10,126.10,124.10,125.50,0] ]}}";
		
		if(responseCount==1)
		result = "{\"status\": \"success\", \"data\": { \"candles\":[[\"2022-05-13T09:15:00+0530\",124.10,125.10,123.50,124.50,0],[\"2022-05-13T09:20:00+0530\",125.10,126.10,124.10,125.50,0],[\"2022-05-13T09:25:00+0530\",126.10,127.10,125.10,126.50,0] ]}}";
		
		if(responseCount==2)
		result = "{\"status\": \"success\", \"data\": { \"candles\":[[\"2022-05-13T09:15:00+0530\",127.10,126.10,124.10,125.50,0],[\"2022-05-13T09:20:00+0530\",126.10,127.10,125.10,126.50,0],[\"2022-05-13T09:25:00+0530\",127.10,128.10,126.10,127.50,0] ]}}";
		
		if(responseCount==3)
		result = "{\"status\": \"success\", \"data\": { \"candles\":[[\"2022-05-13T09:15:00+0530\",126.10,127.10,127.10,128.50,0],[\"2022-05-13T09:20:00+0530\",127.10,128.10,126.10,127.50,0],[\"2022-05-13T09:25:00+0530\",128.10,129.10,127.10,128.50,0] ]}}";
		
		try {
			KiteCandleData candleData =  getObjectMapper().readValue(result.toString(), KiteCandleData.class); 
			candles = candleData.getData().candleInformation();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		responseCount++;
		
		return candles;
	}
	
	
	@Override
	public CreateOrderResponse createOrder(CreateOrderRequest orderRequest) {
		// TODO Auto-generated method stub
		
		KiteCreateOrderRequest request =  (KiteCreateOrderRequest)orderRequest;
		System.out.println("API Order request:{"+request.getExchange()+",-,"+request.getOrderType()+",-,"+request.getPrice()+",-,"
				+request.getProduct()+",-,"+request.getQuantity()+",-,"+request.getSlTriggerPrice()+",-,"
				+request.getTradingSymbol()+",-,"+request.getTransactionType()+",-,"+request.getValidity()+",-,"+request.getVariety());
		
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
		
		Long id = System.currentTimeMillis();

		orderBook.put(id, request.getPrice());
		
		KiteCreateOrderResponse response = new KiteCreateOrderResponse();
		response.setStatus("success");
		response.setData(new KiteCreateOrderResponseLayer2());
		response.getData().setOrderId(id);
		
		System.out.println("Order sucessfully created for :"+request.getTradingSymbol()+" at "+request.getPrice());
		
		return response;
	}
	
	@Override
	public UpdateOrderRespone updateOrder(UpdateOrderRequest updateRequest) {
		// TODO Auto-generated method stub
		
		KiteUpdateOrderRequest request =  (KiteUpdateOrderRequest)updateRequest;
		
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
		
		
		

		orderBook.put(request.getOrderId(), request.getPrice());

		

		KiteUpdateOrderRespone response = new KiteUpdateOrderRespone();
		response.setData(new KiteUpdateOrderResponseLayer2());
		
		response.getData().setOrderId(request.getOrderId());
		response.setStatus("success");

		System.out.println("Order sucessfully updated for :"+request.getTradingSymbol()+" at "+request.getPrice());
		
		return response;
	}

}
