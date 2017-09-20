package com.mod.order;

import com.mod.interfaces.KiteInterface;
import com.mod.interfaces.KiteOrderObject;
import com.mod.interfaces.SystemInterface;

public class Order {
	private SystemInterface systemInterface;
	
	public Order() {
		setSystemInterface(new KiteInterface());
	}
	
	public void orderKiteOption(OrderInfo order){
		KiteOrderObject orderObject = new KiteOrderObject();
		
		orderObject.setClient_id("DV4051");
		orderObject.setDisclosed_quantity("0");
		orderObject.setExchange("NFO");
		orderObject.setOrder_type("MARKET");
		orderObject.setPrice("0");
		orderObject.setProduct("NRML");
		orderObject.setSquareoff_value("0");
		orderObject.setStoploss_value("0");
		orderObject.setTradingsymbol("NIFTY17JUL9700CE");
		orderObject.setTrailing_stoploss("0");
		orderObject.setTransaction_type("BUY");
		orderObject.setTrigger_price("0");
		orderObject.setValidity("DAY");
		orderObject.setVariety("amo");
		
		
		systemInterface.order(orderObject);
	}

	public SystemInterface getSystemInterface() {
		return systemInterface;
	}

	public void setSystemInterface(SystemInterface systemInterface) {
		this.systemInterface = systemInterface;
	}

}
