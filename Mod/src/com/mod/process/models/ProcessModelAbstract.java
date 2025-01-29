package com.mod.process.models;

import java.util.concurrent.atomic.AtomicBoolean;

import com.mod.objects.Action;
import com.mod.objects.Position;
import com.mod.objects.ScriptData;
import com.mod.order.Order;
import com.mod.order.OrderInfo;
import com.mod.support.ApplicationHelper;
import com.mod.support.ConfigData;

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
	private ScriptData NIFTY;
	private Position currentPosition;
	private double positionExpense = 0;
	private Action currentAction;
	
	private static CacheService cacheService;
	
	public boolean completedProcess=true;
	
	public int count;
	
	public int saleRecord;
	
	private double niftyEarlyHigh;
	private double niftyEarlyLow;
	
	public ProcessModelAbstract() {
		// TODO Auto-generated constructor stub
		this.setPositionExpense(positionExpense());
		setPE_PRICE(new ScriptData(getPositionId("pe_id"), 0, ""));
		setCE_PRICE(new ScriptData(getPositionId("ce_id"), 0, ""));
		setNIFTY(new ScriptData(256265,0,""));
	}
	
	public ConfigData appConfig(){
		return ApplicationHelper.Application_Config_Cache.get("app");
	}
	public ConfigData modeConfig(){
		return ApplicationHelper.Application_Config_Cache.get(modelid());
	}

	private double positionExpense(){
		String val = appConfig().getKeyValueConfigs().get("option_position_expense");
		if(val==null){
			throw new RuntimeException("Position expense must be set");
		}
		return Double.valueOf(val);
	}

	protected long getPositionId(String key){
		String val = modeConfig().getKeyValueConfigs().get(key);
		if(val==null){
			throw new RuntimeException("Position must be set up:"+key);
		}
		return Long.valueOf(val);
		
	}
	
	public static CacheService getCacheService() {
		return cacheService;
	}

	public static void setCacheService(CacheService cacheService) {
		ProcessModelAbstract.cacheService = cacheService;
	}

	public Position getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(Position currentPosition) {
		this.currentPosition = currentPosition;
	}

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

	public double getPositionExpense() {
		return positionExpense;
	}

	public void setPositionExpense(double positionExpense) {
		this.positionExpense = positionExpense;
	}

	public Action getCurrentAction() {
		return currentAction;
	}

	public void setCurrentAction(Action currentAction) {
		this.currentAction = currentAction;
	}

	public ScriptData getNIFTY() {
		return NIFTY;
	}

	public void setNIFTY(ScriptData nIFTY) {
		NIFTY = nIFTY;
	}

	/**
	 * @return the niftyEarlyHigh
	 */
	public double getNiftyEarlyHigh() {
		return niftyEarlyHigh;
	}

	/**
	 * @param niftyEarlyHigh the niftyEarlyHigh to set
	 */
	public void setNiftyEarlyHigh(double niftyEarlyHigh) {
		this.niftyEarlyHigh = niftyEarlyHigh;
	}

	/**
	 * @return the niftyEarlyLow
	 */
	public double getNiftyEarlyLow() {
		return niftyEarlyLow;
	}

	/**
	 * @param niftyEarlyLow the niftyEarlyLow to set
	 */
	public void setNiftyEarlyLow(double niftyEarlyLow) {
		this.niftyEarlyLow = niftyEarlyLow;
	}
	
	
	
}
