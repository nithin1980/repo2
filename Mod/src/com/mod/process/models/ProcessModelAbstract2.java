package com.mod.process.models;

import com.mod.objects.Position;
import com.mod.objects.ScriptData;
import com.mod.order.Order;
import com.mod.order.OrderInfo;
import com.mod.support.Candle;

/**
 * 
 * @author nkumar
 * 
 * Would be used to process only Nifty trends.
 *
 */
public abstract class ProcessModelAbstract2 {
	
	public abstract String modelid();
	
	public abstract void processNow();
	
	private Order orderInterface = new Order();
	
	private static CacheService cacheService;
	
	public boolean completedProcess=true;
	
	private Position currentPosition;
	
	private ScriptData NIFTY;
	
	private Candle previousCandle;
	
	private Candle currentCandle;
	
	public ProcessModelAbstract2() {
		// TODO Auto-generated constructor stub
		setCurrentCandle(new Candle());
		setPreviousCandle(new Candle());
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
	public static CacheService getCacheService() {
		return cacheService;
	}

	public static void setCacheService(CacheService cacheService) {
		ProcessModelAbstract2.cacheService = cacheService;
	}

	public Position getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(Position currentPosition) {
		this.currentPosition = currentPosition;
	}

	public ScriptData getNIFTY() {
		return NIFTY;
	}

	public void setNIFTY(ScriptData nIFTY) {
		NIFTY = nIFTY;
	}

	public Candle getPreviousCandle() {
		return previousCandle;
	}

	public void setPreviousCandle(Candle previousCandle) {
		this.previousCandle = previousCandle;
	}

	public Candle getCurrentCandle() {
		return currentCandle;
	}

	public void setCurrentCandle(Candle currentCandle) {
		this.currentCandle = currentCandle;
	}
}
