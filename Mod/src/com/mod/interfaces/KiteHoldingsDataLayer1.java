package com.mod.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KiteHoldingsDataLayer1 {

	@JsonProperty("tradingsymbol")
	private String tradingSymbol;
	
	@JsonProperty("exchange")
	private String exchange;
	
	@JsonProperty("instrument_token")
	private long instrument_token;
	
	@JsonProperty("isin")
	private String isin;
	
	@JsonProperty("product")
	private String product;
	
	@JsonProperty("price")
	private double price;
	
	@JsonProperty("quantity")
	private long quantity;

	@JsonProperty("t1_quantity")
	private long t1_quantity;

	
	@JsonProperty("average_price")
	private double average_price;
	
	public String getTradingSymbol() {
		return tradingSymbol;
	}
	public void setTradingSymbol(String tradingSymbol) {
		this.tradingSymbol = tradingSymbol;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public long getInstrument_token() {
		return instrument_token;
	}
	public void setInstrument_token(long instrument_token) {
		this.instrument_token = instrument_token;
	}
	public String getIsin() {
		return isin;
	}
	public void setIsin(String isin) {
		this.isin = isin;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	public double getAverage_price() {
		return average_price;
	}
	public void setAverage_price(double average_price) {
		this.average_price = average_price;
	}
	public long getT1_quantity() {
		return t1_quantity;
	}
	public void setT1_quantity(long t1_quantity) {
		this.t1_quantity = t1_quantity;
	}

	
}
