package com.mod.interfaces;

public class KiteOrderObject implements OrderInterfaceObject{
/**
 * {"exchange":"NFO","tradingsymbol":"NIFTY17JUL9700CE","transaction_type":"BUY","order_type":"MARKET","quantity"
:"75","price":"0","product":"NRML","validity":"DAY","disclosed_quantity":"0","trigger_price":"0","squareoff_value"
:"0","stoploss_value":"0","trailing_stoploss":"0","variety":"amo","client_id":"DV4051"}
 */
	private String exchange;
	private String tradingsymbol;
	private String transaction_type;
	private String order_type;
	private String price;
	private String product;
	private String validity;
	private String disclosed_quantity;
	private String trigger_price;
	private String squareoff_value;
	private String stoploss_value;
	private String trailing_stoploss;
	private String variety;
	private String client_id;
	
	
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public String getTradingsymbol() {
		return tradingsymbol;
	}
	public void setTradingsymbol(String tradingsymbol) {
		this.tradingsymbol = tradingsymbol;
	}
	public String getTransaction_type() {
		return transaction_type;
	}
	public void setTransaction_type(String transaction_type) {
		this.transaction_type = transaction_type;
	}
	public String getOrder_type() {
		return order_type;
	}
	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getValidity() {
		return validity;
	}
	public void setValidity(String validity) {
		this.validity = validity;
	}
	public String getDisclosed_quantity() {
		return disclosed_quantity;
	}
	public void setDisclosed_quantity(String disclosed_quantity) {
		this.disclosed_quantity = disclosed_quantity;
	}
	public String getTrigger_price() {
		return trigger_price;
	}
	public void setTrigger_price(String trigger_price) {
		this.trigger_price = trigger_price;
	}
	public String getSquareoff_value() {
		return squareoff_value;
	}
	public void setSquareoff_value(String squareoff_value) {
		this.squareoff_value = squareoff_value;
	}
	public String getStoploss_value() {
		return stoploss_value;
	}
	public void setStoploss_value(String stoploss_value) {
		this.stoploss_value = stoploss_value;
	}
	public String getTrailing_stoploss() {
		return trailing_stoploss;
	}
	public void setTrailing_stoploss(String trailing_stoploss) {
		this.trailing_stoploss = trailing_stoploss;
	}
	public String getVariety() {
		return variety;
	}
	public void setVariety(String variety) {
		this.variety = variety;
	}
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	
	
	
	
	
}
