package com.mod.interfaces;

import java.util.List;

import com.mod.enums.EnumMinuteType;
import com.mod.support.Candle;

public interface SystemInterface {

	public void order(OrderInterfaceObject interfaceObject);
	
	public PostionQueryResponse queryCurrentPosition(QueryPositionObject queryPositionObject);
	
	public List<Candle> getCandleData(String stockKey,EnumMinuteType minuteType);
	
	public CreateOrderResponse createOrder(CreateOrderRequest orderRequest);
	
	public UpdateOrderRespone updateOrder(UpdateOrderRequest updateRequest);
	
	public HoldingsQueryResponse queryCurrentHoldings(HoldingsQueryRequest queryPositionObject);
	
	
}
