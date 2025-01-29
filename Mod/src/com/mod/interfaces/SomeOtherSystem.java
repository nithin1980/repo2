package com.mod.interfaces;

import java.util.List;

import com.mod.enums.EnumMinuteType;
import com.mod.support.Candle;

public class SomeOtherSystem implements SystemInterface {

	
	@Override
	public HoldingsQueryResponse queryCurrentHoldings(HoldingsQueryRequest queryPositionObject) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void order(OrderInterfaceObject interfaceObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public PostionQueryResponse queryCurrentPosition(QueryPositionObject queryPositionObject) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Candle> getCandleData(String stockKey, EnumMinuteType minuteType) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public CreateOrderResponse createOrder(CreateOrderRequest orderRequest) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public UpdateOrderRespone updateOrder(UpdateOrderRequest updateRequest) {
		// TODO Auto-generated method stub
		return null;
	}
}
