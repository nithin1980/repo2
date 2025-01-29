package com.mod.support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KiteCandleData {
	
	
	@JsonProperty("data")
	private KiteCandles data;

	public KiteCandles getData() {
		return data;
	}

	public void setData(KiteCandles data) {
		this.data = data;
	}
	
	

}
