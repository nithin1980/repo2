package com.mod.process.models;

import com.mod.objects.ScriptData;
import com.mod.order.Order;
import com.mod.order.OrderInfo;

public abstract class ProcessModelAbstract {

	private Order orderInterface = new Order();
	
	public abstract void processNow();
	
	public abstract String modelid();
	
	
	public static final String BUY_PE = "buy_pe";
	public static final String BUY_CE = "buy_ce";
	public static final String HOLD = "hold";
	public static final String REVERSE_TO_PE = "reverse_position_pe";
	public static final String REVERSE_TO_CE = "reverse_position_ce";
	public static final String HOLDING_PE = "hold_pe";
	public static final String HOLDING_CE = "hold_ce";
	public static final String HOLDING_NOTHING = "hold_nothing";
	
	public static final String INITIAL_POSITION = "INITIAL_POSITION";
	public static final String ENTERED_POSITION = "ENTERED_POSITION";

	
	
	public static final String RANGE_BOUND_PE = "range_bound_pe";
	public static final String RANGE_BOUND_CE = "range_bound_ce";
	
	
	public static final String HOLD_OPTION_DROP = "hold_option_drop";
	
	public static final String TOWARDS_HIGH = "towards_high";
	public static final String TOWARDS_LOW = "towards_low";
	public static final String TOWARDS_NEITHER = "neither";
	
	public static final int NEAR_MISS_COUNT = 3;
	
	
	private ScriptData PE_PRICE;
	private ScriptData CE_PRICE;

	

	public ScriptData getPE_PRICE() {
		return PE_PRICE;
	}

	public void setPE_PRICE(ScriptData pE_PRICE) {
		PE_PRICE = pE_PRICE;
	}

	public ScriptData getCE_PRICE() {
		return CE_PRICE;
	}

	public void setCE_PRICE(ScriptData cE_PRICE) {
		CE_PRICE = cE_PRICE;
	}

	public OrderInfo createOrderObject(){
		return new OrderInfo();
	}
	
	public Order getOrderInterface() {
		return orderInterface;
	}

	public void setOrderInterface(Order orderInterface) {
		this.orderInterface = orderInterface;
	}
	
	
	
}
