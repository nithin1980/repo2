package com.mod.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KiteCreateOrderResponse implements CreateOrderResponse {
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("data")
	private KiteCreateOrderResponseLayer2 data;
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public KiteCreateOrderResponseLayer2 getData() {
		return data;
	}

	public void setData(KiteCreateOrderResponseLayer2 data) {
		this.data = data;
	}
	
	

}
