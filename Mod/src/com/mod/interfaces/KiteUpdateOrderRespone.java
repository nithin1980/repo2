package com.mod.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KiteUpdateOrderRespone implements UpdateOrderRespone {

	@JsonProperty("status")
	private String status;
	
	@JsonProperty("data")
	private KiteUpdateOrderResponseLayer2 data;
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public KiteUpdateOrderResponseLayer2 getData() {
		return data;
	}

	public void setData(KiteUpdateOrderResponseLayer2 data) {
		this.data = data;
	}

}
