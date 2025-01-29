package com.mod.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KitePositionDataLayer2 {
	
	
	private int netQuantity;
	
	private double netValue;
	
	@JsonProperty("tradingsymbol")
	private String tradingsymbol;
	
	@JsonProperty("exchange")
	private String exchange;
	
	@JsonProperty("instrument_token")
	private String instrument_token;
	
	@JsonProperty("product")
	private String product;
	
	@JsonProperty("quantity")
	private int quantity;
	
	@JsonProperty("overnight_quantity")
	private String overnight_quantity;
	
	@JsonProperty("multiplier")
	private double multiplier;
	
	@JsonProperty("average_price")
	private double average_price;
	
	@JsonProperty("close_price")
	private double close_price;
	
	@JsonProperty("last_price")
	private double last_price;
	
	@JsonProperty("value")
	private double value;
	
	@JsonProperty("buy_quantity")
	private int buy_quantity;
	
	@JsonProperty("buy_price")
	private double buy_price;
	
	@JsonProperty("buy_value")
	private double buy_value;
	
	@JsonProperty("sell_quantity")
	private int sell_quantity;
	
	@JsonProperty("sell_price")
	private double sell_price;
	
	@JsonProperty("sell_value")
	private double sell_value;
	
	@JsonProperty("day_buy_quantity")
	private double day_buy_quantity;
	
	@JsonProperty("day_buy_price")
	private double day_buy_price;
	
	@JsonProperty("day_buy_value")
	private double day_buy_value;

	@JsonProperty("day_sell_quantity")
	private double day_sell_quantity;
	
	@JsonProperty("day_sell_price")
	private double day_sell_price;
	
	@JsonProperty("day_sell_value")
	private double day_sell_value;
	
	
	
	public int getNetQuantity() {
		return netQuantity;
	}
	public void setNetQuantity(int netQuantity) {
		this.netQuantity = netQuantity;
	}
	public double getNetValue() {
		return netValue;
	}
	public void setNetValue(double netValue) {
		this.netValue = netValue;
	}
	public String getTradingsymbol() {
		return tradingsymbol;
	}
	public void setTradingsymbol(String tradingsymbol) {
		this.tradingsymbol = tradingsymbol;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public String getInstrument_token() {
		return instrument_token;
	}
	public void setInstrument_token(String instrument_token) {
		this.instrument_token = instrument_token;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getOvernight_quantity() {
		return overnight_quantity;
	}
	public void setOvernight_quantity(String overnight_quantity) {
		this.overnight_quantity = overnight_quantity;
	}
	public double getMultiplier() {
		return multiplier;
	}
	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}
	public double getAverage_price() {
		return average_price;
	}
	public void setAverage_price(double average_price) {
		this.average_price = average_price;
	}
	public double getClose_price() {
		return close_price;
	}
	public void setClose_price(double close_price) {
		this.close_price = close_price;
	}
	public double getLast_price() {
		return last_price;
	}
	public void setLast_price(double last_price) {
		this.last_price = last_price;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public int getBuy_quantity() {
		return buy_quantity;
	}
	public void setBuy_quantity(int buy_quantity) {
		this.buy_quantity = buy_quantity;
	}
	public double getBuy_price() {
		return buy_price;
	}
	public void setBuy_price(double buy_price) {
		this.buy_price = buy_price;
	}
	public double getBuy_value() {
		return buy_value;
	}
	public void setBuy_value(double buy_value) {
		this.buy_value = buy_value;
	}
	public int getSell_quantity() {
		return sell_quantity;
	}
	public void setSell_quantity(int sell_quantity) {
		this.sell_quantity = sell_quantity;
	}
	public double getSell_price() {
		return sell_price;
	}
	public void setSell_price(double sell_price) {
		this.sell_price = sell_price;
	}
	public double getSell_value() {
		return sell_value;
	}
	public void setSell_value(double sell_value) {
		this.sell_value = sell_value;
	}
	public double getDay_buy_quantity() {
		return day_buy_quantity;
	}
	public void setDay_buy_quantity(double day_buy_quantity) {
		this.day_buy_quantity = day_buy_quantity;
	}
	public double getDay_buy_price() {
		return day_buy_price;
	}
	public void setDay_buy_price(double day_buy_price) {
		this.day_buy_price = day_buy_price;
	}
	public double getDay_buy_value() {
		return day_buy_value;
	}
	public void setDay_buy_value(double day_buy_value) {
		this.day_buy_value = day_buy_value;
	}
	public double getDay_sell_quantity() {
		return day_sell_quantity;
	}
	public void setDay_sell_quantity(double day_sell_quantity) {
		this.day_sell_quantity = day_sell_quantity;
	}
	public double getDay_sell_price() {
		return day_sell_price;
	}
	public void setDay_sell_price(double day_sell_price) {
		this.day_sell_price = day_sell_price;
	}
	public double getDay_sell_value() {
		return day_sell_value;
	}
	public void setDay_sell_value(double day_sell_value) {
		this.day_sell_value = day_sell_value;
	}
	
	
	
	
	
	

}
